package net.guardian_of_stone.world.entitites;

import net.guardian_of_stone.world.entitites.ai.goals.attack.*;
import net.guardian_of_stone.world.entitites.ai.goals.support.*;
import net.guardian_of_stone.world.entitites.ai.goals.other.*;

import net.guardian_of_stone.world.entitites.interfaces.IGuardianActiveState;
import net.guardian_of_stone.world.entitites.interfaces.IGuardianOreVariant;
import net.guardian_of_stone.world.entitites.interfaces.IGuardianScouting;
import net.guardian_of_stone.world.entitites.interfaces.IGuardianThreatDetector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;

/**
 * Represents the <b>Guardian of Stone</b> entity — an ancient golem-like protector
 * that awakens when a threat enters its territory.
 *
 * <h2>Behavior Overview</h2>
 * <ul>
 *   <li><b>Dormant by default:</b> The Guardian stands perfectly still, resembling a
 *       stone statue, until a hostile entity enters its detection radius.</li>
 *   <li><b>Threat response:</b> Upon detecting a threat, the Guardian enters an active
 *       state and pursues its target using standard pathfinding.</li>
 *   <li><b>Spider neutrality:</b> The Guardian ignores {@link Spider} mobs entirely.</li>
 *   <li><b>Warden deference:</b> The Guardian never attacks a {@link Warden}.
 *       Upon detecting one within range it immediately enters a forced-dormant
 *       "statue" state and plays dead. If the Warden strikes it, it flees at high
 *       speed. See {@link GuardianWardenFleeGoal}.</li>
 *   <li><b>Sculk aversion:</b> The Guardian refuses to walk on or near any sculk
 *       family block. See {@link GuardianAvoidSculkGoal}.</li>
 *   <li><b>Neutral toward players:</b> The Guardian implements {@link NeutralMob} and
 *       will only attack players if provoked.</li>
 *   <li><b>Monster aggression:</b> The Guardian actively hunts all hostile mobs
 *       except spiders.</li>
 *   <li><b>Ore scouting:</b> When a player right-clicks the Guardian with an ore item,
 *       the Guardian enters scouting mode and guides the player to the nearest matching
 *       vein. See {@link GuardianOreScoutGoal} and {@link IGuardianScouting}.</li>
  * </ul>
 *
 * <h2>Interface responsibilities</h2>
 * <ul>
 *   <li>{@link IGuardianActiveState}   — dormant / awake toggle and panic flag</li>
 *   <li>{@link IGuardianScouting}      — ore-scouting and pointing lifecycle</li>
 *   <li>{@link IGuardianThreatDetector}— {@link #isThreat} predicate shared by goals</li>
 *   <li>{@link IGuardianOreVariant}    — cosmetic ore-overlay variant</li>
 * </ul>
 *
 * <h2>Inspiration</h2>
 * The model and animation set are borrowed from {@link Creaking},
 * but the behavior is entirely different.
 */
public class GuardianOfStoneEntity extends PathfinderMob implements NeutralMob, IGuardianActiveState, IGuardianScouting, IGuardianThreatDetector, IGuardianOreVariant {

    public static final double DETECTION_RADIUS = 28.0;
    public static final float BASE_ATTACK_DAMAGE = 20.0F;
    public static final int POINTING_DURATION_TICKS = 60;
    public static final byte EVENT_DEATH = 3;
    public static final byte EVENT_ATTACK = 4;
    public static final byte EVENT_POINTING = 5;
    public static final byte EVENT_GROUND_SLAM = 6;
    public static final byte EVENT_PANIC = 7;

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private static final Map<Item, Block> ORE_ITEM_TO_BLOCK = Map.ofEntries(
            Map.entry(Items.COAL, Blocks.COAL_ORE),
            Map.entry(Items.RAW_IRON, Blocks.IRON_ORE),
            Map.entry(Items.RAW_COPPER, Blocks.COPPER_ORE),
            Map.entry(Items.RAW_GOLD, Blocks.GOLD_ORE),
            Map.entry(Items.REDSTONE, Blocks.REDSTONE_ORE),
            Map.entry(Items.LAPIS_LAZULI, Blocks.LAPIS_ORE),
            Map.entry(Items.DIAMOND, Blocks.DIAMOND_ORE),
            Map.entry(Items.EMERALD, Blocks.EMERALD_ORE)
    );

    private static final EntityDataAccessor<@NotNull Boolean> DATA_ACTIVE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<@NotNull Boolean> DATA_SCOUTING =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<@NotNull Boolean> DATA_POINTING =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<@NotNull BlockPos> DATA_POINTING_TARGET =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BLOCK_POS);

    private static final EntityDataAccessor<@NotNull Byte> DATA_ORE_VARIANT =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<@NotNull Boolean> DATA_PANICKING =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState attackAnimationState    = new AnimationState();
    public final AnimationState deathAnimationState     = new AnimationState();
    public final AnimationState pointAnimationState     = new AnimationState();
    public final AnimationState groundSlamAnimationState = new AnimationState();
    public final AnimationState panicAnimationState     = new AnimationState();

    private long persistentAngerEndTime;
    private int  attackAnimationTicks;
    private boolean wasActive = false;
    private boolean isPanicAnimPlaying = false;
    private int  deathTime;
    private int  groundSlamAnimationTicks;
    private int  pointingTicksRemaining = 0;

    @Nullable private BlockPos pointingTarget = null;
    @Nullable private EntityReference<@NotNull LivingEntity> persistentAngerTarget;
    @Nullable private Block scoutBlock = null;

    private GuardianGroundSlamGoal groundSlamGoal;

    /**
     * Constructs a new Guardian of Stone.
     *
     * @param type  the entity type registered for this mob
     * @param level the level (world) the mob is being spawned into
     */
    public GuardianOfStoneEntity(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    /**
     * Builds the default attribute map for the Guardian of Stone.
     *
     * <ul>
     *   <li><b>Max health:</b> 220 HP (110 hearts)</li>
     *   <li><b>Movement speed:</b> 0.18</li>
     *   <li><b>Attack damage:</b> {@value BASE_ATTACK_DAMAGE} HP per hit</li>
     *   <li><b>Knockback resistance:</b> 1.0 — total immunity</li>
     *   <li><b>Follow range:</b> 56 blocks</li>
     *   <li><b>Armor:</b> 10 — equivalent to full iron armor</li>
     *   <li><b>Armor toughness:</b> 6</li>
     * </ul>
     *
     * @return a fully-configured {@link AttributeSupplier.Builder}
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 220.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 56.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Registers all {@code DATA_*} keys for automatic client synchronization.</p>
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTIVE, false);
        builder.define(DATA_SCOUTING, false);
        builder.define(DATA_POINTING, false);
        builder.define(DATA_POINTING_TARGET, BlockPos.ZERO);
        builder.define(DATA_ORE_VARIANT, OreVariant.NONE.toId());
        builder.define(DATA_PANICKING, false);
    }

    public static boolean checkSpawnRules(EntityType<@NotNull GuardianOfStoneEntity> type,
                                          ServerLevelAccessor level,
                                          EntitySpawnReason reason,
                                          BlockPos pos,
                                          RandomSource random) {
        return pos.getY() <= 40 && checkMobSpawnRules(type, level, reason, pos, random);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level,
                                                  @NotNull DifficultyInstance difficulty,
                                                  @NotNull EntitySpawnReason spawnReason,
                                                  @Nullable SpawnGroupData groupData) {
        this.setOreVariant(OreVariant.random(this.getRandom()));
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }
    /**
     * {@inheritDoc}
     *
     * <p>Goal priority list (lower index = higher priority):
     * <ol>
     *   <li>Warden flee — forces dormancy near a Warden; overrides everything.</li>
     *   <li>Float on water — prevents drowning.</li>
     *   <li>Avoid sculk — steers the Guardian away from all sculk-family blocks.</li>
     *   <li>Ground slam — AoE when surrounded by multiple enemies.</li>
     *   <li>Melee attack — only while {@link #isActive()} and not scouting.</li>
     *   <li>Ore scouting — guides the player toward an ore vein.</li>
     *   <li>Wander — slow random stroll, only while active.</li>
     * </ol>
     *
     * Target selector priority list (all suppressed while scouting):
     * <ol>
     *   <li>Retaliate against whoever hurt the Guardian (Wardens excluded).</li>
     *   <li>Attack an angry-at player.</li>
     *   <li>Hunt the nearest non-spider, non-warden hostile mob.</li>
     * </ol>
     * </p>
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GuardianWardenFleeGoal(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GuardianAvoidSculkGoal(this));

        this.groundSlamGoal = new GuardianGroundSlamGoal(this);
        this.goalSelector.addGoal(1, this.groundSlamGoal);

        this.goalSelector.addGoal(2, new GuardianMeleeAttackGoal(this));
        this.goalSelector.addGoal(3, new GuardianOreScoutGoal(this));
        this.goalSelector.addGoal(5, new GuardianWanderGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override public boolean canUse() { return !GuardianOfStoneEntity.this.isScouting() && super.canUse(); }
            @Override public boolean canContinueToUse() { return !GuardianOfStoneEntity.this.isScouting() && super.canContinueToUse(); }
        }.setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                this::isAngryAt) {
            @Override public boolean canUse() { return !GuardianOfStoneEntity.this.isScouting() && super.canUse(); }
            @Override public boolean canContinueToUse() { return !GuardianOfStoneEntity.this.isScouting() && super.canContinueToUse(); }
        });
        this.targetSelector.addGoal(3, new GuardianNearestMonsterGoal(this));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Each tick the Guardian:
     * <ol>
     *   <li>Decrements the attack-animation counter.</li>
     *   <li><i>(Server only)</i> Refreshes its active state by scanning for nearby threats.</li>
     *   <li><i>(Server only)</i> Ticks the ground-slam cooldown.</li>
     *   <li><i>(Server only)</i> Ticks the pointing countdown when pointing.</li>
     *   <li><i>(Server only)</i> Advances the persistent-anger timer.</li>
     *   <li><i>(Server only)</i> Forces a target-selector tick immediately on wake-up.</li>
     *   <li><i>(Server only)</i> Updates the biome coating accumulation.</li>
     *   <li><i>(Client only)</i> Drives animation states.</li>
     * </ol>
     * </p>
     */
    @Override
    public void tick() {
        super.tick();

        if (this.attackAnimationTicks > 0) {
            this.attackAnimationTicks--;
        }

        if (!this.level().isClientSide()) {
            tickServer();
        } else {
            tickClient();
        }
    }

    /** Server-side tick logic, extracted for readability. */
    private void tickServer() {
        this.updateActiveState();
        this.updatePersistentAnger((ServerLevel) this.level(), true);

        if (this.groundSlamGoal != null) {
            this.groundSlamGoal.tickCooldown();
        }

        if (this.isPointing()) {
            tickPointing();
        }

        boolean nowActive = this.isActive();
        if (nowActive && !wasActive) {
            this.targetSelector.tick();
        }
        wasActive = nowActive;
    }

    /** Advances the pointing countdown and orients the Guardian toward the vein. */
    private void tickPointing() {
        this.pointingTicksRemaining--;

        if (this.pointingTarget != null) {
            double dx = this.pointingTarget.getX() + 0.5 - this.getX();
            double dz = this.pointingTarget.getZ() + 0.5 - this.getZ();
            float desiredYRot = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));
            this.setYRot(desiredYRot);
            this.yBodyRot = desiredYRot;
            this.yHeadRot = desiredYRot;
        }

        if (this.pointingTicksRemaining <= 0) {
            this.stopPointing();
            this.stopScouting(true);
        }
    }

    /** Client-side tick logic, extracted for readability. */
    private void tickClient() {
        if (this.attackAnimationTicks <= 0) {
            this.attackAnimationState.stop();
        }

        if (!this.isPanicking() && this.isPanicAnimPlaying) {
            this.panicAnimationState.stop();
            this.isPanicAnimPlaying = false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Setting {@code false} additionally clears mob effects, navigation,
     * current target, and horizontal velocity so the Guardian truly freezes.</p>
     */
    @Override
    public void setActive(boolean active) {
        this.entityData.set(DATA_ACTIVE, active);
        if (!active) {
            this.removeAllEffects();
            this.getNavigation().stop();
            this.setTarget(null);
            this.setDeltaMovement(
                    this.getDeltaMovement().x * 0,
                    this.getDeltaMovement().y,
                    this.getDeltaMovement().z * 0
            );
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPanicking() {
        return this.entityData.get(DATA_PANICKING);
    }

    /** {@inheritDoc} */
    @Override
    public void setPanicking(boolean panicking) {
        this.entityData.set(DATA_PANICKING, panicking);
        if (panicking) {
            this.level().broadcastEntityEvent(this, EVENT_PANIC);
        }
    }

    /**
     * Evaluates whether the Guardian should currently be active and updates its state.
     *
     * <p>The Guardian wakes up when at least one hostile entity (excluding spiders)
     * is within {@link #DETECTION_RADIUS} blocks, or when it already has a current
     * attack target, or when it is in scouting / pointing mode, or when sculk
     * blocks are nearby.</p>
     *
     * <p>If a {@link Warden} is within {@link GuardianWardenFleeGoal#DETECT_RADIUS}
     * blocks, this method does nothing — {@link GuardianWardenFleeGoal} owns the
     * active state in that situation.</p>
     */
    private void updateActiveState() {
        if (hasNearbyWarden()) return;

        boolean shouldBeActive = this.getTarget() != null
                || this.hasNearbyThreat()
                || this.isScouting()
                || this.isPointing()
                || GuardianAvoidSculkGoal.hasSculkNearby(this);

        if (shouldBeActive != this.isActive()) {
            this.setActive(shouldBeActive);

            if (shouldBeActive && this.getTarget() == null && !this.isScouting() && !this.isPointing()) {
                LivingEntity nearestThreat = this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        this.getBoundingBox().inflate(DETECTION_RADIUS),
                        e -> isThreat(e, this)
                ).stream().min(
                        Comparator.comparingDouble(e -> e.distanceToSqr(this))
                ).orElse(null);

                this.setTarget(nearestThreat);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Threats are: {@link Monster} subclasses (except {@link Spider} and
     * {@link Warden}), and {@link Player}s the Guardian is currently angry at.</p>
     */
    @Override
    public boolean isThreat(@NotNull LivingEntity entity, @NotNull GuardianOfStoneEntity guardian) {
        if (entity == guardian) return false;
        if (entity instanceof Spider) return false;
        if (entity instanceof Warden) return false;
        return switch (entity) {
            case Monster ignored -> true;
            case Player player -> this.isAngryAt(player, (ServerLevel) this.level());
            default -> false;
        };
    }

    /**
     * Returns {@code true} if at least one living {@link Warden} is within
     * {@link GuardianWardenFleeGoal#DETECT_RADIUS} blocks.
     */
    private boolean hasNearbyWarden() {
        return !this.level().getEntitiesOfClass(
                Warden.class,
                this.getBoundingBox().inflate(GuardianWardenFleeGoal.DETECT_RADIUS),
                LivingEntity::isAlive
        ).isEmpty();
    }

    /**
     * Returns {@code true} if at least one threat is within {@link #DETECTION_RADIUS}.
     */
    private boolean hasNearbyThreat() {
        return !this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(DETECTION_RADIUS),
                e -> isThreat(e, this)
        ).isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public void startScouting(@NotNull Item item, @NotNull Block targetBlock) {
        this.scoutBlock = targetBlock;
        this.entityData.set(DATA_SCOUTING, true);
    }

    /**
     * {@inheritDoc}
     *
     * <p>If the Guardian successfully reached the vein and there is no active combat,
     * it goes back to dormancy.</p>
     */
    @Override
    public void stopScouting(boolean success) {
        this.scoutBlock = null;
        this.entityData.set(DATA_SCOUTING, false);

        if (success && this.getTarget() == null && !this.hasNearbyThreat()) {
            this.setActive(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isScouting() {
        return this.entityData.get(DATA_SCOUTING);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public Block getScoutBlock() {
        return this.scoutBlock;
    }

    /** {@inheritDoc} */
    @Override
    public void startPointing(@NotNull BlockPos veinPos) {
        this.pointingTarget = veinPos;
        this.pointingTicksRemaining = POINTING_DURATION_TICKS;
        this.entityData.set(DATA_POINTING,        true);
        this.entityData.set(DATA_POINTING_TARGET, veinPos);
        this.getNavigation().stop();
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    /** {@inheritDoc} */
    @Override
    public void stopPointing() {
        this.pointingTarget = null;
        this.entityData.set(DATA_POINTING, false);
        this.entityData.set(DATA_POINTING_TARGET, BlockPos.ZERO);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPointing() {
        return this.entityData.get(DATA_POINTING);
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull BlockPos getPointingTarget() {
        return this.entityData.get(DATA_POINTING_TARGET);
    }

    /** {@inheritDoc} */
    @Override
    public OreVariant getOreVariant() {
        return OreVariant.fromId(this.entityData.get(DATA_ORE_VARIANT));
    }

    /** {@inheritDoc} */
    @Override
    public void setOreVariant(OreVariant variant) {
        this.entityData.set(DATA_ORE_VARIANT, variant.toId());
    }

    /** {@inheritDoc} */
    @Override
    public long getPersistentAngerEndTime() {
        return this.persistentAngerEndTime;
    }

    /** {@inheritDoc} */
    @Override
    public void setPersistentAngerEndTime(long time) {
        this.persistentAngerEndTime = time;
    }

    /** {@inheritDoc} */
    @Override
    public @Nullable EntityReference<@NotNull LivingEntity> getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    /** {@inheritDoc} */
    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<@NotNull LivingEntity> entityReference) {
        this.persistentAngerTarget = entityReference;
    }

    /** {@inheritDoc} */
    @Override
    public void startPersistentAngerTimer() {
        this.setPersistentAngerEndTime(PERSISTENT_ANGER_TIME.sample(this.random) + this.tickCount);
    }

    /**
     * Handles player interaction with the Guardian.
     *
     * <p>If the player right-clicks with an item present in {@link #ORE_ITEM_TO_BLOCK}
     * and the Guardian is idle, the Guardian:
     * <ol>
     *   <li>Consumes one item from the held stack (the "payment").</li>
     *   <li>Plays a stone-step acknowledgement sound.</li>
     *   <li>Enters scouting mode via {@link #startScouting(Item, Block)}.</li>
     * </ol>
     * </p>
     */
    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (this.level().isClientSide()) return InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return InteractionResult.PASS;

        Block targetBlock = ORE_ITEM_TO_BLOCK.get(held.getItem());
        if (targetBlock == null) return InteractionResult.PASS;

        if (this.getTarget() != null || this.isScouting() || this.isPointing()) return InteractionResult.PASS;

        held.shrink(1);
        this.playSound(SoundEvents.STONE_STEP, 1.5F, 0.6F);
        this.startScouting(held.getItem(), targetBlock);
        return InteractionResult.CONSUME;
    }

    /** {@inheritDoc} */
    @Override
    public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity target) {
        if (!(target instanceof LivingEntity)) return false;

        this.attackAnimationTicks = 20;
        this.level().broadcastEntityEvent(this, (byte) 4);

        return super.doHurtTarget(level, target);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, @NotNull DamageSource source) {
        if (source.is(DamageTypeTags.IS_FALL)) return true;
        if (source.is(DamageTypeTags.IS_FIRE)) return true;
        return super.isInvulnerableTo(level, source);
    }

    /** {@inheritDoc} */
    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance newEffect) {
        if (!this.isActive()) return false;
        return super.canBeAffected(newEffect);
    }
    

    /** {@inheritDoc} */
    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case EVENT_DEATH -> this.deathAnimationState.start(this.tickCount);
            case EVENT_ATTACK -> {
                this.attackAnimationTicks = 20;
                this.attackAnimationState.start(this.tickCount);
            }
            case EVENT_POINTING -> this.pointAnimationState.start(this.tickCount);
            case EVENT_GROUND_SLAM -> {
                this.groundSlamAnimationTicks = GuardianGroundSlamGoal.WINDUP_TICKS + GuardianGroundSlamGoal.RECOVERY_TICKS;
                this.groundSlamAnimationState.start(this.tickCount);
            }
            case EVENT_PANIC -> {
                this.panicAnimationState.start(this.tickCount);
                this.isPanicAnimPlaying = true;
            }
            default -> super.handleEntityEvent(id);
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.isActive() ? SoundEvents.STONE_STEP : null;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.STONE_HIT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.STONE_BREAK;
    }

    /** {@inheritDoc} */
    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        this.level().broadcastEntityEvent(this, (byte) 3);
    }

    /** {@inheritDoc} */
    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.level().isClientSide()) {
            for (int i = 0; i < 10; i++) {
                double dx = this.random.nextGaussian() * 0.02;
                double dy = this.random.nextGaussian() * 0.02;
                double dz = this.random.nextGaussian() * 0.02;
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.POOF,
                        this.getRandomX(1.0),
                        this.getRandomY(),
                        this.getRandomZ(1.0),
                        dx, dy, dz
                );
            }
        }

        if (this.deathTime >= 30) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.addPersistentAngerSaveData(output);
        output.putByte("OreVariant", getOreVariant().toId());
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readPersistentAngerSaveData(this.level(), input);
        setOreVariant(OreVariant.fromId(input.getByteOr("OreVariant", OreVariant.NONE.toId())));
    }
}