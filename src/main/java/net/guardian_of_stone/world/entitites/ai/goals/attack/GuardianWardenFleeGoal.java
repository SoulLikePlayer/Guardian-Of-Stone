package net.guardian_of_stone.world.entitites.ai.goals.attack;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.EnumSet;
import java.util.List;

/**
 * Makes the {@link GuardianOfStoneEntity} react with primal fear to any nearby
 * {@link Warden}.
 *
 * <h2>Overview</h2>
 * <p>The Guardian of Stone, despite its power, recognises the Warden as something
 * far beyond a normal threat. Its reaction is two-fold:</p>
 * <ol>
 *   <li><b>Forced dormancy:</b> As soon as a Warden enters {@value #DETECT_RADIUS}
 *       blocks, the Guardian immediately enters a
 *       {@link GuardianOfStoneEntity#setActive(boolean) forced-dormant} state,
 *       ceasing all movement and combat — playing dead like a stone statue.</li>
 *   <li><b>Flee when hunted:</b> If the Warden actively targets the Guardian
 *       (i.e., {@link Warden#getEntityAngryAt()} returns the Guardian), the
 *       dormancy is lifted and the Guardian flees at maximum speed. It keeps
 *       fleeing as long as the Warden is alive, still targeting it, and within
 *       {@value #SAFE_RADIUS} blocks. The flee ends when the Warden loses
 *       interest or the Guardian escapes far enough.</li>
 * </ol>
 *
 * <h2>Priority and flags</h2>
 * <p>This goal must sit at priority 0 — above everything including
 * {@code FloatGoal} — so it can override all other behaviour the instant a
 * Warden is detected. It holds the {@code MOVE}, {@code LOOK}, and
 * {@code JUMP} flags to prevent any other goal from interfering.</p>
 *
 * <h2>Dormancy vs. flee transition</h2>
 * <pre>
 *  Warden enters DETECT_RADIUS
 *        │
 *        ▼
 *  [DORMANT / freeze]  ◄──── Warden present but not targeting Guardian
 *        │
 *  warden.getEntityAngryAt() == Guardian
 *        │
 *        ▼
 *  [FLEE] ──── Warden no longer targeting Guardian OR Warden > SAFE_RADIUS ───► stop
 * </pre>
 */
public class GuardianWardenFleeGoal extends Goal {

    public static final double DETECT_RADIUS = 32.0;
    public static final double SAFE_RADIUS   = 48.0;

    private static final double FLEE_SPEED           = 1.4;
    private static final int    PATH_RECALC_INTERVAL = 20;

    private static final double BASE_SPEED       = 0.18;
    private static final double FLEE_SPEED_BOOST = 0.30;

    private final GuardianOfStoneEntity guardian;

    private Warden  trackedWarden     = null;
    private boolean isFleeing         = false;
    private int     pathRecalcCounter = 0;

    public GuardianWardenFleeGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    // -------------------------------------------------------------------------
    // Goal lifecycle
    // -------------------------------------------------------------------------

    /**
     * Activates whenever at least one living {@link Warden} is within
     * {@value #DETECT_RADIUS} blocks, regardless of the Guardian's active state.
     */
    @Override
    public boolean canUse() {
        Warden nearest = findNearestWarden();
        if (nearest == null) return false;
        this.trackedWarden = nearest;
        return true;
    }

    /**
     * Keeps running while the Warden is alive. Stops when:
     * <ul>
     *   <li>The Warden dies or despawns.</li>
     *   <li>We are fleeing and the Warden is beyond {@value #SAFE_RADIUS} blocks
     *       AND no longer targeting the Guardian.</li>
     *   <li>We are dormant and the Warden has left {@value #DETECT_RADIUS} blocks.</li>
     * </ul>
     */
    @Override
    public boolean canContinueToUse() {
        if (this.trackedWarden == null || !this.trackedWarden.isAlive()) return false;

        double dist = this.guardian.distanceTo(this.trackedWarden);

        if (this.isFleeing) {
            return !(dist >= SAFE_RADIUS) || isWardenTargetingGuardian(this.trackedWarden);
        } else {
            return dist < DETECT_RADIUS;
        }
    }

    @Override
    public void start() {
        this.isFleeing         = false;
        this.pathRecalcCounter = 0;

        // Force dormancy — Guardian plays dead.
        this.guardian.setActive(false);
        this.guardian.setTarget(null);
        this.guardian.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.trackedWarden == null) return;

        // Prefer the closest Warden that is actively hunting us.
        Warden angry = findWardenTargetingGuardian();
        if (angry != null) {
            this.trackedWarden = angry;
        } else {
            Warden nearest = findNearestWarden();
            if (nearest != null) this.trackedWarden = nearest;
        }

        if (!this.isFleeing) {
            if (isWardenTargetingGuardian(this.trackedWarden)) {
                // Warden just started chasing us — begin flee.
                this.isFleeing = true;
                this.guardian.setActive(true);
                this.guardian.setPanicking(true);
                applyFleeSpeed();
                recalculateFleePath();
            } else {
                // Stay dormant.
                this.guardian.setActive(false);
                this.guardian.getNavigation().stop();
            }
            return;
        }

        // ── Flee phase ──────────────────────────────────────────────────────

        // If the Warden stopped targeting us and is far enough, go back to dormancy
        // (don't stop the goal — the Warden may still be within DETECT_RADIUS).
        if (!isWardenTargetingGuardian(this.trackedWarden)
                && this.guardian.distanceTo(this.trackedWarden) >= SAFE_RADIUS) {
            this.isFleeing = false;
            this.guardian.setActive(false);
            this.guardian.setPanicking(false);
            resetSpeed();
            this.guardian.getNavigation().stop();
            return;
        }

        this.pathRecalcCounter++;
        if (this.pathRecalcCounter >= PATH_RECALC_INTERVAL) {
            this.pathRecalcCounter = 0;
            recalculateFleePath();
        }
    }

    @Override
    public void stop() {
        this.trackedWarden = null;
        this.isFleeing     = false;
        this.guardian.setPanicking(false);
        resetSpeed();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if this Warden's active anger target is the Guardian.
     *
     * <p>{@link Warden#getEntityAngryAt()} returns the entity the Warden is
     * currently hunting — non-empty only when
     * {@link net.minecraft.world.entity.monster.warden.AngerLevel#isAngry()} is
     * {@code true} and the anger management has selected an active target. We
     * compare that target by reference against our Guardian instance.</p>
     */
    private boolean isWardenTargetingGuardian(Warden warden) {
        if (warden == null) return false;
        return warden.getEntityAngryAt()
                .filter(target -> target == this.guardian)
                .isPresent();
    }

    /**
     * Scans for the nearest living {@link Warden} within {@code radius} blocks.
     *
     * @return the nearest Warden, or {@code null} if none found
     */
    private Warden findNearestWarden() {
        List<Warden> wardens = this.guardian.level().getEntitiesOfClass(
                Warden.class,
                this.guardian.getBoundingBox().inflate(GuardianWardenFleeGoal.DETECT_RADIUS),
                LivingEntity::isAlive
        );
        if (wardens.isEmpty()) return null;

        Warden nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Warden w : wardens) {
            double d = this.guardian.distanceToSqr(w);
            if (d < minDist) { minDist = d; nearest = w; }
        }
        return nearest;
    }

    /**
     * Scans for the nearest living {@link Warden} within {@code radius} blocks
     * that is actively targeting this Guardian.
     *
     * @return the closest angry Warden, or {@code null} if none
     */
    private Warden findWardenTargetingGuardian() {
        List<Warden> wardens = this.guardian.level().getEntitiesOfClass(
                Warden.class,
                this.guardian.getBoundingBox().inflate(GuardianWardenFleeGoal.SAFE_RADIUS),
                LivingEntity::isAlive
        );

        Warden nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Warden w : wardens) {
            if (!isWardenTargetingGuardian(w)) continue;
            double d = this.guardian.distanceToSqr(w);
            if (d < minDist) { minDist = d; nearest = w; }
        }
        return nearest;
    }

    /**
     * Asks the pathfinder to move the Guardian away from the Warden using
     * {@link DefaultRandomPos#getPosAway}.
     */
    private void recalculateFleePath() {
        if (this.trackedWarden == null) return;

        Vec3 awayPos = DefaultRandomPos.getPosAway(
                this.guardian,
                16,
                7,
                this.trackedWarden.position()
        );

        if (awayPos != null) {
            this.guardian.getNavigation().moveTo(
                    awayPos.x, awayPos.y, awayPos.z,
                    FLEE_SPEED
            );
        }
    }

    private void applyFleeSpeed() {
        var attr = this.guardian.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) attr.setBaseValue(FLEE_SPEED_BOOST);
    }

    private void resetSpeed() {
        var attr = this.guardian.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) attr.setBaseValue(BASE_SPEED);
    }
}