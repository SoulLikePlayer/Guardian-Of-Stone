package net.guardian_of_stone.world.entitites;

import net.guardian_of_stone.world.entitites.ai.goals.*;

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
 *   <li><b>Neutral toward players:</b> The Guardian implements {@link NeutralMob} and
 *       will only attack players if provoked (i.e., attacked first).</li>
 *   <li><b>Monster aggression:</b> The Guardian actively hunts all hostile mobs
 *       (subclasses of {@link Monster}) except spiders.</li>
 *   <li><b>Ore scouting:</b> When a player right-clicks the Guardian with an ore item,
 *       the Guardian enters scouting mode and guides the player to the nearest matching
 *       vein. See {@link GuardianOreScoutGoal} for details.</li>
 * </ul>
 *
 * <h2>Ore Scouting</h2>
 * <p>The Guardian recognizes any item registered in {@link #ORE_ITEM_TO_BLOCK}. When
 * such an item is offered, the Guardian consumes one unit from the stack, enters the
 * {@code SCOUTING} state, and begins pathfinding to the nearest matching ore block
 * within a configurable radius (see {@link GuardianOreScoutGoal#SEARCH_RADIUS}).
 * If a combat threat interrupts scouting, the goal stops immediately and the Guardian
 * reverts to combat behavior; the scouting state is cancelled.</p>
 *
 * <h2>Network Synchronization</h2>
 * The {@code ACTIVE} and {@code SCOUTING} flags are synchronized to the client so the
 * renderer can toggle between the "statue" idle pose and the walking animation.
 *
 * <h2>Inspiration</h2>
 * The model and animation set are borrowed from {@link Creaking},
 * but the behavior is entirely different.
 */
public class GuardianOfStoneEntity extends PathfinderMob implements NeutralMob {

    public static final double DETECTION_RADIUS = 28.0;
    public static final float BASE_ATTACK_DAMAGE = 20.0F;

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private static final EntityDataAccessor<@NotNull Boolean> DATA_ACTIVE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<@NotNull Boolean> DATA_SCOUTING =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * Maps ore items (held by the player) to the block type the Guardian will search for.
     *
     * <p>Populate this map with every ore/raw-material item you want to support.
     * Example entries (replace with your actual registry references):</p>
     * <pre>{@code
     *   Items.COAL          -> Blocks.COAL_ORE,
     *   Items.RAW_IRON      -> Blocks.IRON_ORE,
     *   Items.RAW_GOLD      -> Blocks.GOLD_ORE,
     *   Items.DIAMOND       -> Blocks.DIAMOND_ORE,
     *   ...
     * }</pre>
     */
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

    private long persistentAngerEndTime;
    private int attackAnimationTicks;
    private boolean wasActive = false;
    private int deathTime;

    @Nullable private EntityReference<@NotNull LivingEntity> persistentAngerTarget;
    @Nullable private Block scoutBlock = null;

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
     * <p>These values are tuned to make the Guardian feel like an immovable
     * force of nature — a living mountain rather than a simple golem.</p>
     *
     * <ul>
     *   <li><b>Max health:</b> 220 HP (110 hearts)</li>
     *   <li><b>Movement speed:</b> 0.18 — deliberately slower than before</li>
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
     * <p>Registers the {@link #DATA_ACTIVE} and {@link #DATA_SCOUTING} flags so they
     * are automatically synchronized between server and client.</p>
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTIVE, false);
        builder.define(DATA_SCOUTING, false);
    }

    public static boolean checkSpawnRules(
            EntityType<@NotNull GuardianOfStoneEntity> type,
            ServerLevelAccessor level,
            EntitySpawnReason reason,
            BlockPos pos,
            RandomSource random) {

        return pos.getY() <= 40 && checkMobSpawnRules(type, level, reason, pos, random);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Goal priority list (lower index = higher priority):
     * <ol>
     *   <li>Float on water — prevents drowning.</li>
     *   <li>Melee attack — only executes while {@link #isActive()}.</li>
     *   <li>Ore scouting — guides the player toward an ore vein when in scouting mode.</li>
     *   <li>Wander within home territory — only while active.</li>
     * </ol>
     * Target selector priority list:
     * <ol>
     *   <li>Retaliate against whoever hurt the Guardian.</li>
     *   <li>Attack an angry-at player.</li>
     *   <li>Hunt the nearest non-spider hostile mob.</li>
     * </ol>
     * </p>
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GuardianMeleeAttackGoal(this));
        this.goalSelector.addGoal(3, new GuardianOreScoutGoal(this));
        this.goalSelector.addGoal(5, new GuardianWanderGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                this::isAngryAt));
        this.targetSelector.addGoal(3, new GuardianNearestMonsterGoal(this));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Each tick the Guardian:
     * <ol>
     *   <li>Decrements the attack-animation counter.</li>
     *   <li><i>(Server only)</i> Refreshes its active state by scanning for nearby threats.</li>
     *   <li><i>(Server only)</i> If a combat threat arises during scouting, cancels scouting.</li>
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

            if (this.isScouting() && this.getTarget() != null) {
                this.stopScouting(false);
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
        }
    }

    /**
     * Evaluates whether the Guardian should currently be active and updates its state.
     *
     * <p>The Guardian wakes up when at least one hostile entity (excluding spiders)
     * is within {@link #DETECTION_RADIUS} blocks, or when it already has a current
     * attack target, or when it is in scouting mode (so it can move toward the vein).
     * It returns to dormancy once all threats have left the area and its anger has
     * expired — and it is not scouting.</p>
     */
    private void updateActiveState() {
        boolean shouldBeActive = this.getTarget() != null
                || this.hasNearbyThreat()
                || this.isScouting();

        if (shouldBeActive != this.isActive()) {
            this.setActive(shouldBeActive);

            if (shouldBeActive && this.getTarget() == null && !this.isScouting()) {
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
     * and the Guardian is not currently scouting or fighting, the Guardian:
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

        if (this.getTarget() != null || this.isScouting()) return InteractionResult.PASS;

        held.shrink(1);

        this.playSound(SoundEvents.STONE_STEP, 1.5F, 0.6F);
        player.sendSystemMessage(Component.translatable("entity.guardian_of_stone.scout_start"));

        this.startScouting(held.getItem(), targetBlock);
        return InteractionResult.CONSUME;
    }

    /**
     * Enters scouting mode for the given ore type.
     *
     * <p>Sets the synced {@code SCOUTING} flag to {@code true} and stores the target
     * block so {@link GuardianOreScoutGoal} can retrieve it.</p>
     *
     * @param item        the ore item that was handed to the Guardian (used for future
     *                    reference if needed)
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
     * @param success {@code true} if the Guardian found and reached the vein;
     *                {@code false} if it gave up or was interrupted
     */
    public void stopScouting(boolean success) {
        this.scoutBlock = null;
        this.entityData.set(DATA_SCOUTING, false);

        if (success && this.getTarget() == null && !this.hasNearbyThreat()) {
            this.setActive(false);
        }
    }

    /**
     * Returns whether the Guardian is currently in scouting mode.
     *
     * @return {@code true} if scouting
     */
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
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readPersistentAngerSaveData(this.level(), input);
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
}