package net.guardian_of_stone.world.entitites.ai.goals.attack;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Makes the {@link GuardianOfStoneEntity} react with primal fear to any nearby
 * {@link Warden}.
 *
 * <h2>Overview</h2>
 * <p>The Guardian of Stone, despite its power, recognises the Warden as something
 * far beyond a normal threat — an entity of the Deep Dark that it has no business
 * fighting. Its reaction is two-fold:</p>
 * <ol>
 *   <li><b>Forced dormancy:</b> As soon as a Warden enters {@value #DETECT_RADIUS}
 *       blocks, the Guardian immediately enters a {@link GuardianOfStoneEntity#setActive(boolean)
 *       forced-dormant} state, ceasing all movement and combat. This mimics the
 *       instinct of "playing dead" — becoming indistinguishable from a stone statue.</li>
 *   <li><b>Flee on attack:</b> If the Warden actually strikes the Guardian
 *       (detected via {@link GuardianOfStoneEntity#isCurrentlyFleingWarden()}), the
 *       forced dormancy is lifted and this goal steers the Guardian away at maximum
 *       speed until the Warden is beyond {@value #SAFE_RADIUS} blocks or the flee
 *       timer expires.</li>
 * </ol>
 *
 * <h2>Priority and flags</h2>
 * <p>This goal must sit at priority&nbsp;0 — above everything including
 * {@code FloatGoal} — so it can override all other behaviour the instant a
 * Warden is detected. It holds the {@code MOVE}, {@code LOOK}, and
 * {@code JUMP} flags to prevent any other goal from interfering during the flee
 * phase.</p>
 *
 * <h2>Dormancy vs. flee transition</h2>
 * <pre>
 *  Warden enters radius
 *        │
 *        ▼
 *  [DORMANT / freeze]  ◄──── Warden stays in range but hasn't hit
 *        │
 *  Guardian is hit by Warden
 *        │
 *        ▼
 *  [FLEE] ──── Warden > SAFE_RADIUS OR flee timer ends ───► goal stops
 * </pre>
 *
 * <h2>Interaction with other goals</h2>
 * <p>While this goal is active, {@link GuardianOfStoneEntity#isActive()} returns
 * {@code false} during the dormant phase (so all combat goals self-suppress) and
 * {@code true} only briefly during the flee phase so the navigation system works.
 * The scouting goal is also suspended while the Guardian is fleeing or dormant.</p>
 */
public class GuardianWardenFleeGoal extends Goal {

    public static final double DETECT_RADIUS = 32.0;
    public static final double SAFE_RADIUS = 48.0;

    private static final int MAX_FLEE_TICKS = 20 * 15; // 15 seconds
    private static final double FLEE_SPEED = 1.4;
    private static final int PATH_RECALC_INTERVAL = 20;

    private final GuardianOfStoneEntity guardian;

    private Warden trackedWarden = null;
    private boolean isFleeing = false;

    private int fleeTicksRemaining = 0;
    private int pathRecalcCounter  = 0;

    /**
     * Constructs the goal.
     *
     * @param guardian the Guardian that will execute this goal
     */
    public GuardianWardenFleeGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Activates whenever at least one {@link Warden} is within
     * {@value #DETECT_RADIUS} blocks. This deliberately ignores the
     * {@link GuardianOfStoneEntity#isActive()} flag so the goal starts even
     * while the Guardian is dormant.</p>
     */
    @Override
    public boolean canUse() {
        Warden nearest = findNearestWarden(DETECT_RADIUS);
        if (nearest == null) return false;
        this.trackedWarden = nearest;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Keeps running as long as the tracked Warden is still alive and within
     * {@value #SAFE_RADIUS} blocks (or we are still in the flee-timer window).
     * Stops immediately if the Warden dies or despawns.</p>
     */
    @Override
    public boolean canContinueToUse() {
        if (this.trackedWarden == null || !this.trackedWarden.isAlive()) return false;
        double dist = this.guardian.distanceTo(this.trackedWarden);
        if (this.isFleeing) {
            return dist < SAFE_RADIUS && this.fleeTicksRemaining > 0;
        } else {
            return dist < DETECT_RADIUS;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        this.isFleeing = false;
        this.fleeTicksRemaining = 0;
        this.pathRecalcCounter  = 0;

        this.guardian.setActive(false);
        this.guardian.setTarget(null);
        this.guardian.getNavigation().stop();
    }

    /** {@inheritDoc} */
    @Override
    public void tick() {
        if (this.trackedWarden == null) return;

        if (!this.isFleeing) {
            if (this.guardian.isCurrentlyFleingWarden()) {
                this.isFleeing = true;
                this.fleeTicksRemaining = MAX_FLEE_TICKS;
                this.guardian.setActive(true);
                this.guardian.clearWardenFleeFlag();
                this.guardian.setPanicking(true);
                recalculateFleePath();
            } else {
                this.guardian.setActive(false);
                this.guardian.getNavigation().stop();
            }
            return;
        }

        this.fleeTicksRemaining--;
        this.pathRecalcCounter++;

        Warden closer = findNearestWarden(SAFE_RADIUS);
        if (closer != null) this.trackedWarden = closer;

        if (this.pathRecalcCounter >= PATH_RECALC_INTERVAL) {
            this.pathRecalcCounter = 0;
            recalculateFleePath();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        this.trackedWarden = null;
        this.isFleeing = false;
        this.guardian.setPanicking(false);
    }
    /**
     * Scans for the nearest living {@link Warden} within {@code radius} blocks.
     *
     * @param radius search radius in blocks
     * @return the nearest Warden, or {@code null} if none found
     */
    private Warden findNearestWarden(double radius) {
        List<Warden> wardens = this.guardian.level().getEntitiesOfClass(
                Warden.class,
                this.guardian.getBoundingBox().inflate(radius),
                LivingEntity::isAlive
        );
        if (wardens.isEmpty()) return null;

        Warden nearest = null;
        double  minDist = Double.MAX_VALUE;
        for (Warden w : wardens) {
            double d = this.guardian.distanceToSqr(w);
            if (d < minDist) {
                minDist = d;
                nearest = w;
            }
        }
        return nearest;
    }

    /**
     * Asks the pathfinder to move the Guardian away from the Warden.
     *
     * <p>We use {@link DefaultRandomPos#getPosAway} to pick a random position in
     * the direction opposite the Warden, then issue a standard moveTo command.</p>
     */
    private void recalculateFleePath() {
        if (this.trackedWarden == null) return;

        Vec3 wardenPos = this.trackedWarden.position();
        Vec3 awayPos   = DefaultRandomPos.getPosAway(
                this.guardian,
                16,
                7,
                wardenPos
        );

        if (awayPos != null) {
            this.guardian.getNavigation().moveTo(
                    awayPos.x, awayPos.y, awayPos.z,
                    FLEE_SPEED
            );
        }
    }
}