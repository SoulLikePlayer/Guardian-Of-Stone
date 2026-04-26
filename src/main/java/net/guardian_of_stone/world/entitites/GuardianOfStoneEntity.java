package net.guardian_of_stone.world.entitites;

import net.guardian_of_stone.world.entitites.ai.goals.*;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

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
 * </ul>
 *
 * <h2>Network Synchronization</h2>
 * The {@code ACTIVE} flag is synchronized to the client so the renderer can toggle
 * between the "statue" idle pose and the walking animation.
 *
 * <h2>Inspiration</h2>
 * The model and animation set are borrowed from {@link Creaking},
 * but the behavior is entirely different.
 */
public class GuardianOfStoneEntity extends PathfinderMob implements NeutralMob {

    public static final double DETECTION_RADIUS = 16.0;
    public static final float BASE_ATTACK_DAMAGE = 8.0F;

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private static final EntityDataAccessor<@NotNull Boolean> DATA_ACTIVE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    private long persistentAngerEndTime;
    @Nullable private EntityReference<@NotNull LivingEntity> persistentAngerTarget;
    private int attackAnimationTicks;
    private boolean wasActive = false;
    private int deathTime;


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
     *   <li><b>Max health:</b> 80 HP (40 hearts) — a sturdy, hard-to-kill protector.</li>
     *   <li><b>Movement speed:</b> 0.23 — slightly slower than a player.</li>
     *   <li><b>Attack damage:</b> {@value BASE_ATTACK_DAMAGE} HP per hit.</li>
     *   <li><b>Knockback resistance:</b> 0.8 — nearly immune to knockback, fitting for a stone golem.</li>
     *   <li><b>Follow range:</b> 32 blocks — wide patrol area.</li>
     * </ul>
     *
     * @return a fully-configured {@link AttributeSupplier.Builder}
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Registers the {@link #DATA_ACTIVE} flag so it is automatically
     * synchronized between server and client.</p>
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTIVE, false);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Goal priority list (lower index = higher priority):
     * <ol>
     *   <li>Float on water — prevents drowning.</li>
     *   <li>Melee attack — only executes while {@link #isActive()}.</li>
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

            boolean nowActive = this.isActive();
            if (nowActive && !wasActive) {
                this.targetSelector.tick();
            }
            wasActive = nowActive;
        }

        if (this.level().isClientSide()) {
            this.attackAnimationState.animateWhen(this.attackAnimationTicks > 0, this.tickCount);
        }
    }

    /**
     * Evaluates whether the Guardian should currently be active and updates its state.
     *
     * <p>The Guardian wakes up when at least one hostile entity (excluding spiders)
     * is within {@link #DETECTION_RADIUS} blocks, or when it already has a current
     * attack target. It returns to dormancy once all threats have left the area
     * and its anger has expired.</p>
     *
     * <p>When transitioning from dormant to active without a pre-existing target,
     * the nearest threat is automatically assigned as the initial target.</p>
     */
    private void updateActiveState() {
        boolean shouldBeActive = this.getTarget() != null || this.hasNearbyThreat();

        if (shouldBeActive != this.isActive()) {
            this.setActive(shouldBeActive);

            if (shouldBeActive && this.getTarget() == null) {
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
     * @see #isThreat(LivingEntity, GuardianOfStoneEntity)
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
     * <p>An entity is a threat if it is:
     * <ul>
     *   <li>a {@link Monster} (hostile mob), <b>excluding</b> {@link Spider},</li>
     *   <li>or a {@link Player} that the Guardian is currently angry at.</li>
     * </ul>
     * The Guardian itself is never considered its own threat.</p>
     *
     * @param entity   the entity to evaluate
     * @param guardian the Guardian performing the check (needed for anger-state lookup)
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
     * {@inheritDoc}
     *
     * <p>Triggers the attack animation on both server (broadcast) and client,
     * then delegates actual damage to the superclass. Returns {@code false}
     * immediately if the target is not a {@link LivingEntity}.</p>
     */
    @Override
    public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity target) {
        if (!(target instanceof LivingEntity)) return false;

        this.attackAnimationTicks = 20;
        this.level().broadcastEntityEvent(this, (byte) 4);

        return super.doHurtTarget(level, target);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Handles the {@code 4} entity event sent by {@link #doHurtTarget} to
     * synchronize the attack animation on the client side.</p>
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationTicks = 20;
        } else {
            super.handleEntityEvent(id);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Starts the death {@link AnimationState} on the client so the renderer
     * can play the collapse sequence before the entity is removed from the world.</p>
     */
    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        if (this.level().isClientSide()) {
            this.deathAnimationState.start(this.tickCount);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>The Guardian is immune to fall damage and fire damage, reflecting its
     * nature as a creature of stone and earth.</p>
     */
    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, @NotNull DamageSource source) {
        if (source.is(DamageTypeTags.IS_FALL)) return true;
        if (source.is(DamageTypeTags.IS_FIRE)) return true;
        return super.isInvulnerableTo(level, source);
    }

    // -------------------------------------------------------------------------
    // Status effects
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Prevents any status effect from being applied while the Guardian is dormant,
     * avoiding visual particles that would break the "stone statue" illusion.</p>
     */
    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance newEffect) {
        if (!this.isActive()) return false;
        return super.canBeAffected(newEffect);
    }

    // -------------------------------------------------------------------------
    // Sounds
    // -------------------------------------------------------------------------

    /**
     * Returns the ambient idle sound.
     *
     * <p>While active the Guardian emits a quiet grinding-stone sound; while dormant
     * it makes no noise at all to preserve the statue illusion.</p>
     *
     * @return {@link SoundEvents#STONE_STEP} when active, {@code null} otherwise
     */
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.isActive() ? SoundEvents.STONE_STEP : null;
    }

    /**
     * Returns the sound played when the Guardian takes damage.
     *
     * @param source the source of the damage (unused)
     * @return {@link SoundEvents#STONE_HIT}
     */
    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.STONE_HIT;
    }

    /**
     * Returns the sound played when the Guardian dies.
     *
     * @return {@link SoundEvents#STONE_BREAK}
     */
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.STONE_BREAK;
    }

    // -------------------------------------------------------------------------
    // Persistence (save / load)
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Saves persistent anger data so the Guardian remembers which player
     * provoked it across chunk unloads and game restarts.</p>
     */
    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.addPersistentAngerSaveData(output);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Restores persistent anger data saved by {@link #addAdditionalSaveData}.</p>
     */
    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readPersistentAngerSaveData(this.level(), input);
    }

    // -------------------------------------------------------------------------
    // Active state
    // -------------------------------------------------------------------------

    /**
     * Returns whether the Guardian is currently in its active (awakened) state.
     *
     * <p>When {@code true} the Guardian moves, attacks, and plays walking animations.
     * When {@code false} it stands frozen like a statue.</p>
     *
     * @return {@code true} if active
     */
    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    /**
     * Sets the Guardian's active state and broadcasts the change to all tracking clients.
     *
     * <p>Transitioning to dormant ({@code false}) also clears all active status effects
     * to avoid visual artifacts on the frozen statue model.</p>
     *
     * @param active {@code true} to awaken the Guardian, {@code false} to make it dormant
     */
    public void setActive(boolean active) {
        this.entityData.set(DATA_ACTIVE, active);
        if (!active) {
            this.removeAllEffects();
        }
    }

    // -------------------------------------------------------------------------
    // NeutralMob — persistent anger
    // -------------------------------------------------------------------------

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

    /**
     * {@inheritDoc}
     *
     * <p>Samples a random duration from {@link #PERSISTENT_ANGER_TIME} and sets the
     * expiry tick accordingly.</p>
     */
    @Override
    public void startPersistentAngerTimer() {
        this.setPersistentAngerEndTime(PERSISTENT_ANGER_TIME.sample(this.random) + this.tickCount);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if(this.deathTime >= 30){
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }
}