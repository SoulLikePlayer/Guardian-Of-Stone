package net.guardian_of_stone.world.entitites;

import net.guardian_of_stone.world.entitites.ai.goals.attack.*;
import net.guardian_of_stone.world.entitites.ai.goals.support.*;
import net.guardian_of_stone.world.entitites.ai.goals.other.*;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
 *   <li><b>Spider neutrality:</b> The Guardian ignores {@link Spider} mobs entirely,
 *       treating them as non-threats regardless of circumstances.</li>
 *   <li><b>Warden deference:</b> The Guardian never attacks a {@link Warden}.
 *       Upon detecting one within range it immediately enters a forced-dormant
 *       "statue" state and plays dead. If the Warden strikes it, it flees at high
 *       speed until safely out of range. See
 *       {@link GuardianWardenFleeGoal}.</li>
 *   <li><b>Sculk aversion:</b> The Guardian refuses to walk on or near any sculk
 *       family block (sculk, sculk vein, sensor, shrieker, catalyst). It will
 *       reroute around sculk even mid-combat or mid-scout. See
 *       {@link GuardianAvoidSculkGoal}.</li>
 *   <li><b>Neutral toward players:</b> The Guardian implements {@link NeutralMob} and
 *       will only attack players if provoked (i.e., attacked first).</li>
 *   <li><b>Monster aggression:</b> The Guardian actively hunts all hostile mobs
 *       (subclasses of {@link Monster}) except spiders.</li>
 *   <li><b>Ore scouting:</b> When a player right-clicks the Guardian with an ore item,
 *       the Guardian enters scouting mode and guides the player to the nearest matching
 *       vein. Scouting is <em>never</em> interrupted by combat — the Guardian simply
 *       ignores threats until it has finished pointing. See {@link GuardianOreScoutGoal}
 *       for details.</li>
 * </ul>
 *
 * <h2>Ore Scouting</h2>
 * <p>The Guardian recognizes any item registered in {@link #ORE_ITEM_TO_BLOCK}. When
 * such an item is offered, the Guardian consumes one unit from the stack, enters the
 * {@code SCOUTING} state, and begins pathfinding to the nearest matching ore block
 * within a configurable radius (see {@link GuardianOreScoutGoal#SEARCH_RADIUS}).
 * Combat threats do <em>not</em> interrupt scouting; target-selection goals are
 * suppressed while the {@code SCOUTING} flag is active. Once arrived, the Guardian
 * turns to face the vein and raises its arm for {@link #POINTING_DURATION_TICKS} ticks
 * before returning to its normal state.</p>
 *
 * <h2>Network Synchronization</h2>
 * The {@code ACTIVE}, {@code SCOUTING}, {@code POINTING} flags and the
 * {@code POINTING_TARGET} position are synchronized to the client so the renderer
 * can drive the correct animation and compute the pointing direction.
 *
 * <h2>Inspiration</h2>
 * The model and animation set are borrowed from {@link Creaking},
 * but the behavior is entirely different.
 */
public class GuardianOfStoneEntity extends PathfinderMob implements NeutralMob {

    public static final double DETECTION_RADIUS = 28.0;
    public static final float BASE_ATTACK_DAMAGE = 20.0F;
    public static final int POINTING_DURATION_TICKS = 60;
    public static final byte EVENT_GROUND_SLAM = 6;
    public static final byte EVENT_PANIC = 7;

    public final AnimationState attackAnimationState  = new AnimationState();
    public final AnimationState deathAnimationState   = new AnimationState();
    public final AnimationState pointAnimationState   = new AnimationState();
    public final AnimationState groundSlamAnimationState = new AnimationState();
    public final AnimationState panicAnimationState = new AnimationState();

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

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

    private static final EntityDataAccessor<Boolean> DATA_PANICKING =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * Maps ore items (held by the player) to the block type the Guardian will search for.
     */
    private static final Map<Item, Block> ORE_ITEM_TO_BLOCK = Map.ofEntries(
            Map.entry(Items.COAL,         Blocks.COAL_ORE),
            Map.entry(Items.RAW_IRON,     Blocks.IRON_ORE),
            Map.entry(Items.RAW_COPPER,   Blocks.COPPER_ORE),
            Map.entry(Items.RAW_GOLD,     Blocks.GOLD_ORE),
            Map.entry(Items.REDSTONE,     Blocks.REDSTONE_ORE),
            Map.entry(Items.LAPIS_LAZULI, Blocks.LAPIS_ORE),
            Map.entry(Items.DIAMOND,      Blocks.DIAMOND_ORE),
            Map.entry(Items.EMERALD,      Blocks.EMERALD_ORE)
    );

    private long persistentAngerEndTime;
    private int  attackAnimationTicks;
    private boolean wasActive = false;
    private boolean isPanicAnimPlaying = false;
    private int  deathTime;
    private int groundSlamAnimationTicks;

    /** Server-side countdown (ticks) while the Guardian is pointing. */
    private int pointingTicksRemaining = 0;

    /** Position of the vein being pointed at, used to orient the Guardian. */
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
     * <p>Registers {@link #DATA_ACTIVE}, {@link #DATA_SCOUTING}, {@link #DATA_POINTING},
     * and {@link #DATA_POINTING_TARGET} for automatic client synchronization.</p>
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTIVE, false);
        builder.define(DATA_SCOUTING, false);
        builder.define(DATA_POINTING,false);
        builder.define(DATA_POINTING_TARGET, BlockPos.ZERO);
        builder.define(DATA_ORE_VARIANT, OreVariant.NONE.toId());
        builder.define(DATA_PANICKING, false);
    }

    public static boolean checkSpawnRules(
            EntityType<@NotNull GuardianOfStoneEntity> type,
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
     *   <li>Float on water — prevents drowning.</li>
     *   <li>Warden flee — forces dormancy near a Warden; overrides everything.</li>
     *   <li>Avoid sculk — steers the Guardian away from all sculk-family blocks.</li>
     *   <li>Melee attack — only executes while {@link #isActive()} and not scouting.</li>
     *   <li>Ore scouting — guides the player toward an ore vein when in scouting mode.</li>
     *   <li>Wander within home territory — only while active.</li>
     * </ol>
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
        this.goalSelector.addGoal(1, groundSlamGoal);

        this.goalSelector.addGoal(2, new GuardianMeleeAttackGoal(this));
        this.goalSelector.addGoal(3, new GuardianOreScoutGoal(this));
        this.goalSelector.addGoal(5, new GuardianWanderGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override public boolean canUse()           { return !GuardianOfStoneEntity.this.isScouting() && super.canUse(); }
            @Override public boolean canContinueToUse() { return !GuardianOfStoneEntity.this.isScouting() && super.canContinueToUse(); }
        }.setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                this::isAngryAt) {
            @Override public boolean canUse()           { return !GuardianOfStoneEntity.this.isScouting() && super.canUse(); }
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
     *   <li><i>(Server only)</i> Ticks the pointing countdown when pointing.</li>
     *   <li><i>(Server only)</i> Advances the persistent-anger timer.</li>
     *   <li><i>(Server only)</i> Forces a target-selector tick immediately on wake-up.</li>
     *   <li><i>(Client only)</i> Drives the attack {@link AnimationState}.</li>
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
            this.updateActiveState();
            this.updatePersistentAnger((ServerLevel) this.level(), true);

            if (this.groundSlamGoal != null){
                this.groundSlamGoal.tickCooldown();
            }

            if (this.isPointing()) {
                this.pointingTicksRemaining--;

                if (this.pointingTarget != null) {
                    double dx = this.pointingTarget.getX() + 0.5 - this.getX();
                    assert this.pointingTarget != null;
                    double dz = this.pointingTarget.getZ() + 0.5 - this.getZ();
                    float desiredYRot = (float)(Math.atan2(-dx, dz) * (180.0 / Math.PI));
                    this.setYRot(desiredYRot);
                    this.yBodyRot = desiredYRot;
                    this.yHeadRot = desiredYRot;
                }

                if (this.pointingTicksRemaining <= 0) {
                    this.stopPointing();
                    this.stopScouting(true);
                }
            }

            boolean nowActive = this.isActive();
            if (nowActive && !wasActive) {
                this.targetSelector.tick();
            }
            wasActive = nowActive;
        }

        if (this.level().isClientSide()) {
            if (this.attackAnimationTicks <= 0) {
                this.attackAnimationState.stop();
            }

            if (!this.isPanicking() && this.isPanicAnimPlaying) {
                this.panicAnimationState.stop();
                this.isPanicAnimPlaying = false;
            }
        }
    }

    /**
     * Evaluates whether the Guardian should currently be active and updates its state.
     *
     * <p>The Guardian wakes up when at least one hostile entity (excluding spiders)
     * is within {@link #DETECTION_RADIUS} blocks, or when it already has a current
     * attack target, or when it is in scouting / pointing mode, or when sculk
     * blocks are nearby (so {@link GuardianAvoidSculkGoal} can move it away).</p>
     *
     * <p>If a {@link Warden} is within {@link GuardianWardenFleeGoal#DETECT_RADIUS}
     * blocks, this method does nothing — {@link GuardianWardenFleeGoal} owns the
     * active state in that situation and must not be overridden.</p>
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
     * Returns {@code true} if at least one living {@link Warden} is within
     * {@link GuardianWardenFleeGoal#DETECT_RADIUS} blocks.
     *
     * <p>Used by {@link #updateActiveState()} to yield control of the active flag
     * entirely to {@link GuardianWardenFleeGoal} whenever a Warden is present.</p>
     */
    private boolean hasNearbyWarden() {
        return !this.level().getEntitiesOfClass(
                Warden.class,
                this.getBoundingBox().inflate(GuardianWardenFleeGoal.DETECT_RADIUS),
                LivingEntity::isAlive
        ).isEmpty();
    }

    /**
     * Scans the surrounding area for any living entity that qualifies as a threat.
     *
     * @return {@code true} if at least one threat is within {@link #DETECTION_RADIUS}
     */
    private boolean hasNearbyThreat() {
        return !this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(DETECTION_RADIUS),
                e -> isThreat(e, this)
        ).isEmpty();
    }

    /**
     * Determines whether the given entity qualifies as a threat to the Guardian.
     *
     * @param entity   the entity to evaluate
     * @param guardian the Guardian performing the check
     * @return {@code true} if the entity should wake the Guardian
     */
    public boolean isThreat(@NotNull LivingEntity entity, @NotNull GuardianOfStoneEntity guardian) {
        if (entity == guardian) return false;
        if (entity instanceof Spider) return false;
        if (entity instanceof Warden) return false;
        return switch (entity) {
            case Monster monster -> true;
            case Player player -> this.isAngryAt(player, (ServerLevel) this.level());
            default -> false;
        };
    }


    /**
     * Handles player interaction with the Guardian.
     *
     * <p>If the player right-clicks with an item present in {@link #ORE_ITEM_TO_BLOCK}
     * and the Guardian is not currently scouting or pointing, the Guardian:
     * <ol>
     *   <li>Consumes one item from the held stack (the "payment").</li>
     *   <li>Plays a stone-step acknowledgement sound.</li>
     *   <li>Sends the player a brief chat message indicating it understood.</li>
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

    /**
     * Enters scouting mode for the given ore type.
     *
     * @param item        the ore item that was handed to the Guardian
     * @param targetBlock the ore block the Guardian should search for
     */
    public void startScouting(@NotNull Item item, @NotNull Block targetBlock) {
        this.scoutBlock = targetBlock;
        this.entityData.set(DATA_SCOUTING, true);
    }

    /**
     * Exits scouting mode.
     *
     * <p>Called by {@link GuardianOreScoutGoal} upon arrival at the vein ({@code success
     * = true}) or on failure/interruption ({@code success = false}).</p>
     *
     * @param success {@code true} if the Guardian found and reached the vein
     */
    public void stopScouting(boolean success) {
        this.scoutBlock = null;
        this.entityData.set(DATA_SCOUTING, false);

        if (success && this.getTarget() == null && !this.hasNearbyThreat()) {
            this.setActive(false);
        }
    }

    /**
     * Enters the pointing pose: the Guardian freezes, faces the vein, and raises
     * its arm for {@link #POINTING_DURATION_TICKS} ticks.
     *
     * <p>Called by {@link GuardianOreScoutGoal} when it arrives at the vein —
     * <em>before</em> {@link #stopScouting(boolean)} so the {@code SCOUTING} flag
     * remains active (and therefore keeps combat goals suppressed) until the pose
     * is over.</p>
     *
     * @param veinPos the block position of the found ore vein
     */
    public void startPointing(@NotNull BlockPos veinPos) {
        this.pointingTarget         = veinPos;
        this.pointingTicksRemaining = POINTING_DURATION_TICKS;
        this.entityData.set(DATA_POINTING,        true);
        this.entityData.set(DATA_POINTING_TARGET, veinPos);
        this.getNavigation().stop();
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    /**
     * Exits the pointing pose and clears the stored vein position.
     */
    public void stopPointing() {
        this.pointingTarget = null;
        this.entityData.set(DATA_POINTING,        false);
        this.entityData.set(DATA_POINTING_TARGET, BlockPos.ZERO);
    }

    /** @return {@code true} if the Guardian is currently in the pointing pose */
    public boolean isPointing() {
        return this.entityData.get(DATA_POINTING);
    }

    /**
     * Returns the synchronized position of the ore vein the Guardian is pointing at.
     * Safe to call on both server and client.
     *
     * @return the vein {@link BlockPos}, or {@link BlockPos#ZERO} if not pointing
     */
    public @NotNull BlockPos getPointingTarget() {
        return this.entityData.get(DATA_POINTING_TARGET);
    }

    /** @return {@code true} if the Guardian is currently in scouting mode */
    public boolean isScouting() {
        return this.entityData.get(DATA_SCOUTING);
    }

    /**
     * Returns the ore block the Guardian is currently scouting for.
     *
     * @return the target {@link Block}, or {@code null} if not scouting
     */
    @Nullable
    public Block getScoutBlock() {
        return this.scoutBlock;
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
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationTicks = 20;
            this.attackAnimationState.start(this.tickCount);
        } else if (id == 3) {
            this.deathAnimationState.start(this.tickCount);
        } else if (id == 5) {
            this.pointAnimationState.start(this.tickCount);
        } else if (id == EVENT_GROUND_SLAM) {
            this.groundSlamAnimationTicks = GuardianGroundSlamGoal.WINDUP_TICKS + GuardianGroundSlamGoal.RECOVERY_TICKS;
            this.groundSlamAnimationState.start(this.tickCount);
        } else if (id == EVENT_PANIC) {
            this.panicAnimationState.start(this.tickCount);
            this.isPanicAnimPlaying = true;
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        this.level().broadcastEntityEvent(this, (byte) 3);
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


    /**
     * Returns whether the Guardian is currently in its active (awakened) state.
     *
     * @return {@code true} if active
     */
    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    /**
     * Sets the Guardian's active state and broadcasts the change to all tracking clients.
     *
     * @param active {@code true} to awaken the Guardian, {@code false} to make it dormant
     */
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


    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime >= 30) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    public OreVariant getOreVariant() {
        return OreVariant.fromId(this.entityData.get(DATA_ORE_VARIANT));
    }

    public void setOreVariant(OreVariant variant) {
        this.entityData.set(DATA_ORE_VARIANT, variant.toId());
    }

    public boolean isPanicking() {
        return this.entityData.get(DATA_PANICKING);
    }

    public void setPanicking(boolean panicking) {
        this.entityData.set(DATA_PANICKING, panicking);
        if (panicking) {
            this.level().broadcastEntityEvent(this, EVENT_PANIC);
        }
    }
}