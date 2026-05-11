package net.guardian_of_stone.world.entitites.ai.goals.attack;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * An area-of-effect ground-slam goal for the {@link GuardianOfStoneEntity}.
 *
 * <h2>Overview</h2>
 * <p>When the Guardian finds itself surrounded by several enemies, it winds up and
 * drives both fists into the ground, sending a shockwave that launches all nearby
 * mobs away and deals reduced damage. The hit is purely horizontal — entities are
 * thrown radially outward, never straight up, so the effect looks like a ground-
 * pulse rather than a simple AoE explosion.</p>
 *
 * <h2>Trigger conditions</h2>
 * <ul>
 *   <li>The Guardian must be {@link GuardianOfStoneEntity#isActive() active}.</li>
 *   <li>At least {@value #MIN_ENEMIES_TO_TRIGGER} hostile entities must be within
 *       {@value #DETECT_RADIUS} blocks (the "surrounded" check).</li>
 *   <li>The ability must be off cooldown
 *       ({@value #COOLDOWN_TICKS} ticks between uses).</li>
 * </ul>
 *
 * <h2>Timing (ticks)</h2>
 * <pre>
 *   0 ── WINDUP_TICKS (10) ── impact ──────────── RECOVERY_TICKS (35) ──── goal ends
 * </pre>
 * <p>The impact (knockback + damage) fires at tick {@value #WINDUP_TICKS} (0.5 s), the exact moment both fists hit the ground in the animation.
 * The goal holds control for {@code WINDUP_TICKS + RECOVERY_TICKS} total before
 * returning. Movement is locked throughout via the {@code Flag.MOVE} flag.</p>
 *
 * <h2>Animation</h2>
 * <p>At {@code start()}, event byte {@value GuardianOfStoneEntity#EVENT_GROUND_SLAM}
 * is broadcast so the client can fire the
 * {@link net.guardian_of_stone.client.animation.GuardianOfStoneAnimation#GUARDIAN_GROUND_SLAM}
 * animation. The animation length matches {@code WINDUP_TICKS + RECOVERY_TICKS}
 * (45 ticks = 2.25 s) precisely. The visual impact keyframe sits at 0.50 s,
 * aligned with {@code WINDUP_TICKS = 10}.</p>
 */
public class GuardianGroundSlamGoal extends Goal {
    public static final int MIN_ENEMIES_TO_TRIGGER = 3;
    public static final double DETECT_RADIUS = 2.0;
    public static final int COOLDOWN_TICKS = 200;
    public static final int WINDUP_TICKS = 10;
    public static final int RECOVERY_TICKS = 35;
    private static final double KNOCKBACK_STRENGTH = 2.2;
    private static final double KNOCKBACK_UP = 0.45;
    private static final float SLAM_DAMAGE = 8.0F;

    private final GuardianOfStoneEntity guardian;

    private int ticksRemaining;
    private int cooldownRemaining;
    private boolean impactApplied;

    /**
     * Constructs the goal.
     *
     * @param guardian the Guardian that will execute this goal
     */
    public GuardianGroundSlamGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }


    /** {@inheritDoc} */
    @Override
    public boolean canUse() {
        if (!this.guardian.isActive()) return false;
        if (this.cooldownRemaining > 0) return false;
        if (this.guardian.isScouting()) return false;
        if (this.guardian.isPointing()) return false;
        return countNearbyEnemies() >= MIN_ENEMIES_TO_TRIGGER;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canContinueToUse() {
        return this.ticksRemaining > 0 && this.guardian.isActive();
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        this.ticksRemaining  = WINDUP_TICKS + RECOVERY_TICKS;
        this.impactApplied   = false;
        this.cooldownRemaining = 0;

        this.guardian.getNavigation().stop();

        this.guardian.level().broadcastEntityEvent(
                this.guardian,
                GuardianOfStoneEntity.EVENT_GROUND_SLAM
        );
    }

    /** {@inheritDoc} */
    @Override
    public void tick() {
        this.ticksRemaining--;

        if (!this.impactApplied && this.ticksRemaining <= RECOVERY_TICKS) {
            this.impactApplied = true;
            applyShockwave();

            this.cooldownRemaining = COOLDOWN_TICKS;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (this.cooldownRemaining <= 0) {
            this.cooldownRemaining = COOLDOWN_TICKS;
        }
        this.ticksRemaining = 0;
    }


    /**
     * Must be called every server tick from
     * {@link GuardianOfStoneEntity#tick()} so the cooldown drains even while
     * other goals are running.
     */
    public void tickCooldown() {
        if (this.cooldownRemaining > 0) {
            this.cooldownRemaining--;
        }
    }


    /**
     * Counts living entities that qualify as threats within {@value #DETECT_RADIUS}
     * blocks of the Guardian.
     */
    private int countNearbyEnemies() {
        return (int) this.guardian.level()
                .getEntitiesOfClass(
                        LivingEntity.class,
                        this.guardian.getBoundingBox().inflate(DETECT_RADIUS),
                        e -> e != this.guardian && this.guardian.isThreat(e, this.guardian)
                )
                .size();
    }

    /**
     * Applies radial knockback and damage to all nearby threats, then spawns
     * ground-crack particles and plays a seismic impact sound.
     */
    private void applyShockwave() {
        if (!(this.guardian.level() instanceof ServerLevel serverLevel)) return;

        Vec3 origin = this.guardian.position();

        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                this.guardian.getBoundingBox().inflate(DETECT_RADIUS),
                e -> e != this.guardian && this.guardian.isThreat(e, this.guardian)
        );

        for (LivingEntity target : targets) {
            double dx = target.getX() - origin.x;
            double dz = target.getZ() - origin.z;
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > 0.01) {
                double nx = dx / dist;
                double nz = dz / dist;

                double falloff = 1.0 - (dist / DETECT_RADIUS) * 0.5;
                double impulse = KNOCKBACK_STRENGTH * falloff;

                target.setDeltaMovement(
                        nx * impulse,
                        KNOCKBACK_UP,
                        nz * impulse
                );
                target.hurtMarked = true;
            }

            target.hurt(
                    serverLevel.damageSources().mobAttack(this.guardian),
                    SLAM_DAMAGE
            );
        }

        double px = origin.x;
        double py = origin.y + 0.1;
        double pz = origin.z;

        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                px, py, pz,
                80,
                2.5, 0.05, 2.5,
                0.3
        );

        serverLevel.playSound(
                null,
                this.guardian.blockPosition(),
                SoundEvents.STONE_BREAK,
                this.guardian.getSoundSource(),
                3.0F,
                0.5F
        );
        serverLevel.playSound(
                null,
                this.guardian.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(),
                this.guardian.getSoundSource(),
                1.2F,
                0.7F
        );
    }
}