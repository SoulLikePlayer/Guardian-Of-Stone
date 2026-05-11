package net.guardian_of_stone.world.entitites.ai.goals.other;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Makes the {@link GuardianOfStoneEntity} actively avoid all sculk-family blocks.
 *
 * <h2>Overview</h2>
 * <p>The Guardian of Stone harbours a deep, instinctive aversion to anything sculk.
 * This goal checks a small area around the Guardian's feet every tick and, when a
 * sculk block is detected nearby, steers the Guardian away from it at high speed —
 * overriding combat, scouting, and wandering. The Guardian will <em>never</em>
 * willingly step onto sculk terrain, and if it finds itself on one (e.g., due to
 * a forced teleport or world-gen overlap) it will flee immediately.</p>
 *
 * <h2>Sculk detection</h2>
 * <p>Any block tagged {@link BlockTags#SCULK_REPLACEABLE} or whose block state
 * matches {@link net.minecraft.tags.BlockTags#SCULK_REPLACEABLE} — i.e., sculk, sculk veins,
 * sculk sensors, sculk shriekers, sculk catalysts — triggers the flee response.
 * Detection uses the Minecraft block-tag system so future sculk additions are
 * covered automatically without code changes.</p>
 *
 * <h2>Detection area</h2>
 * <p>A square of {@value #SCAN_RADIUS} blocks around the Guardian's feet is scanned
 * each tick. The flee direction is the vector <em>away</em> from the average centre
 * of all nearby sculk blocks, ensuring the Guardian moves in the safest direction
 * when surrounded on multiple sides.</p>
 *
 * <h2>Priority</h2>
 * <p>This goal should be registered at priority&nbsp;1 (immediately after the
 * {@code FloatGoal} and the Warden flee goal). The {@code MOVE} and {@code JUMP}
 * flags are claimed so no locomotion goal can interfere while the Guardian is
 * fleeing sculk.</p>
 */
public class GuardianAvoidSculkGoal extends Goal {

    public static final int SCAN_RADIUS   = 4;

    private static final int SCAN_VERTICAL = 2;
    private static final double FLEE_SPEED = 1.3;

    private static final int PATH_RECALC_INTERVAL = 15;

    private final GuardianOfStoneEntity guardian;

    private Vec3 sculkCentre = Vec3.ZERO;

    private int pathRecalcCounter = 0;

    private static final double BASE_SPEED = 0.18;
    private static final double FLEE_SPEED_BOOST = 0.30;

    /**
     * Constructs the goal.
     *
     * @param guardian the Guardian that will execute this goal
     */
    public GuardianAvoidSculkGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Triggers whenever at least one sculk block is found within
     * {@value #SCAN_RADIUS} blocks, regardless of the Guardian's active state.
     * A dormant Guardian will still shuffle away from sculk — the statue illusion
     * is less important than avoiding the corrupted terrain.</p>
     */
    @Override
    public boolean canUse() {
        Vec3 centre = findSculkCentre();
        if (centre == null) return false;
        this.sculkCentre = centre;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Keeps running until no sculk block remains within the scan area.
     * We re-check every tick so the goal stops the moment the Guardian
     * has moved clear of all sculk.</p>
     */
    @Override
    public boolean canContinueToUse() {
        Vec3 centre = findSculkCentre();
        if (centre == null) return false;
        this.sculkCentre = centre;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        this.pathRecalcCounter = 0;
        this.guardian.setTarget(null);
        this.guardian.setPanicking(true);
        applyFleeSpeed();
        recalculateFleePath();
    }

    /** {@inheritDoc} */
    @Override
    public void tick() {
        this.pathRecalcCounter++;
        if (this.pathRecalcCounter >= PATH_RECALC_INTERVAL) {
            this.pathRecalcCounter = 0;
            recalculateFleePath();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        this.pathRecalcCounter = 0;
        this.guardian.setPanicking(false);
        resetSpeed();
    }

    /**
     * Returns {@code true} if at least one sculk block is within {@value #SCAN_RADIUS}
     * blocks of {@code guardian}.
     *
     * <p>Called by {@link GuardianOfStoneEntity#updateActiveState()}
     * so the entity can include sculk proximity in its active-state calculation without
     * coupling itself to the goal instance.</p>
     */
    public static boolean hasSculkNearby(GuardianOfStoneEntity guardian) {
        Level level    = guardian.level();
        BlockPos origin = guardian.blockPosition();

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                for (int dy = -SCAN_VERTICAL; dy <= SCAN_VERTICAL; dy++) {
                    if (isSculkBlock(level.getBlockState(origin.offset(dx, dy, dz)))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Scans the world in concentric horizontal rings around the Guardian's feet,
     * returning the closest block position whose {@link BlockState} matches
     * {@link GuardianOfStoneEntity#getScoutBlock()}.
     *
     * @return average sculk position, or {@code null} if the area is sculk-free
     */
    private Vec3 findSculkCentre() {
        Level level    = this.guardian.level();
        BlockPos origin = this.guardian.blockPosition();

        double sumX = 0, sumY = 0, sumZ = 0;
        int    count = 0;

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                for (int dy = -SCAN_VERTICAL; dy <= SCAN_VERTICAL; dy++) {
                    BlockPos pos  = origin.offset(dx, dy, dz);
                    BlockState bs = level.getBlockState(pos);
                    if (isSculkBlock(bs)) {
                        sumX += pos.getX();
                        sumY += pos.getY();
                        sumZ += pos.getZ();
                        count++;
                    }
                }
            }
        }

        if (count == 0) return null;
        return new Vec3(sumX / count, sumY / count, sumZ / count);
    }

    /**
     * Returns {@code true} if the given {@link BlockState} belongs to any sculk
     * family block (sculk, sculk vein, sculk sensor, sculk shrieker, sculk
     * catalyst).
     *
     * <p>Detection relies on the {@link BlockTags#SCULK_REPLACEABLE} tag (which
     * covers sculk and sculk veins) and the more specific sculk-sensor/shrieker/
     * catalyst tags so that <em>all</em> Deep Dark blocks are included.</p>
     */
    private static boolean isSculkBlock(BlockState state) {
        return state.is(Blocks.SCULK)
                || state.is(Blocks.SCULK_VEIN)
                || state.is(Blocks.SCULK_SENSOR)
                || state.is(Blocks.SCULK_CATALYST)
                || state.is(Blocks.SCULK_SHRIEKER);
    }

    /**
     * Issues a new pathfinding command directed away from the sculk centroid.
     */
    private void recalculateFleePath() {
        Vec3 awayPos = DefaultRandomPos.getPosAway(
                this.guardian,
                14,
                5,
                this.sculkCentre
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