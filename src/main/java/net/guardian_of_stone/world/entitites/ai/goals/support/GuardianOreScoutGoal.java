package net.guardian_of_stone.world.entitites.ai.goals.support;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Goal that makes the {@link GuardianOfStoneEntity} locate and guide the player
 * toward an ore vein after being handed a matching ore item.
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>{@link GuardianOfStoneEntity#mobInteract} detects a right-click with an ore item,
 *       calls {@link GuardianOfStoneEntity#startScouting(Item, Block)}, which sets the
 *       scouting state and stores the target block type.</li>
 *   <li>On the next goal tick, {@link #canUse()} returns {@code true}, and
 *       {@link #start()} begins a spiral search around the Guardian's feet.</li>
 *   <li>Each tick of {@link #tick()} walks the Guardian toward the found vein at full
 *       speed ({@value #ESCORT_SPEED}), emitting block-break particles so the player
 *       can follow. Combat threats are intentionally ignored during this phase.</li>
 *   <li>Upon arrival the goal calls {@link GuardianOfStoneEntity#startPointing(BlockPos)}
 *       which freezes the Guardian facing the vein and plays the arm-pointing animation
 *       for {@link GuardianOfStoneEntity#POINTING_DURATION_TICKS} ticks.</li>
 *   <li>The goal then ends and {@code stopScouting(true)} is called by the entity's own
 *       pointing-countdown logic once the pose is over.</li>
 * </ol>
 *
 * <h2>Search parameters</h2>
 * <ul>
 *   <li>Horizontal radius: {@value #SEARCH_RADIUS} blocks</li>
 *   <li>Vertical range: {@value #SEARCH_DEPTH} blocks below and {@value #SEARCH_HEIGHT}
 *       blocks above the Guardian</li>
 *   <li>Particle burst: every {@value #PARTICLE_INTERVAL} ticks while en route</li>
 * </ul>
 */
public class GuardianOreScoutGoal extends Goal {

    public static final int SEARCH_RADIUS = 48;
    private static final int SEARCH_DEPTH  = 32;
    private static final int SEARCH_HEIGHT = 8;

    /** Full movement speed during scouting — no penalty compared to combat movement. */
    private static final double ESCORT_SPEED = 1.0;

    /**
     * Arrival threshold in XZ-only distance squared (4 blocks).
     * We compare only horizontal distance so the Guardian stops above the vein
     * even when the ore is deep underground.
     */
    private static final double ARRIVE_DIST_XZ_SQ = 16.0;

    private static final int PARTICLE_INTERVAL = 15;
    private static final int PARTICLE_COUNT    = 6;

    private static final int MAX_ESCORT_TICKS = 20 * 60;

    private final GuardianOfStoneEntity guardian;
    @Nullable private BlockPos targetVein;

    private int escortTicksRemaining;
    private int tickCounter;

    /**
     * Constructs the goal.
     *
     * @param guardian the Guardian that will execute this goal
     */
    public GuardianOreScoutGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Active only when the Guardian is in scouting mode. Combat threats do NOT
     * prevent this goal from starting — targeting goals suppress themselves while
     * {@link GuardianOfStoneEntity#isScouting()} is {@code true}.</p>
     */
    @Override
    public boolean canUse() {
        return this.guardian.isScouting();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Stop if scouting was cancelled, we ran out of escort time, or the vein
     * was never found. Combat no longer interrupts this goal.</p>
     */
    @Override
    public boolean canContinueToUse() {
        return this.guardian.isScouting()
                && this.targetVein != null
                && this.escortTicksRemaining > 0
                && !this.guardian.isPointing();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Performs the spiral ore search synchronously on {@code start()}.</p>
     */
    @Override
    public void start() {
        this.tickCounter          = 0;
        this.escortTicksRemaining = MAX_ESCORT_TICKS;
        this.targetVein           = findNearestVein();

        if (this.targetVein == null) {
            this.guardian.stopScouting(false);
        } else {
            navigateTo(this.targetVein);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Each tick: emits guiding particles toward the vein and checks arrival.</p>
     */
    @Override
    public void tick() {
        if (this.targetVein == null) return;

        this.escortTicksRemaining--;
        this.tickCounter++;

        if (this.tickCounter % PARTICLE_INTERVAL == 0) {
            spawnGuideParticles();
        }

        BlockPos gPos = this.guardian.blockPosition();
        double dxArrival = gPos.getX() - this.targetVein.getX();
        double dzArrival = gPos.getZ() - this.targetVein.getZ();
        double distXZSq  = dxArrival * dxArrival + dzArrival * dzArrival;
        if (distXZSq <= ARRIVE_DIST_XZ_SQ) {
            spawnArrivalParticles();
            this.guardian.startPointing(this.targetVein);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        this.targetVein = null;
        this.guardian.getNavigation().stop();
    }


    /**
     * Scans the world in concentric horizontal rings around the Guardian's feet,
     * returning the closest block position whose {@link BlockState} matches
     * {@link GuardianOfStoneEntity#getScoutBlock()}.
     *
     * @return the nearest matching {@link BlockPos}, or {@code null} if nothing found
     */
    @Nullable
    private BlockPos findNearestVein() {
        Block target = this.guardian.getScoutBlock();
        if (target == null) return null;

        BlockPos origin  = this.guardian.blockPosition();
        BlockPos bestPos = null;
        double   bestDist = Double.MAX_VALUE;

        for (int r = 0; r <= SEARCH_RADIUS; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    for (int dy = -SEARCH_DEPTH; dy <= SEARCH_HEIGHT; dy++) {
                        BlockPos candidate = origin.offset(dx, dy, dz);
                        BlockState state   = this.guardian.level().getBlockState(candidate);

                        if (state.is(target)) {
                            double dist = origin.distSqr(candidate);
                            if (dist < bestDist) {
                                bestDist = dist;
                                bestPos  = candidate.immutable();
                            }
                        }
                    }
                }
            }

            if (bestPos != null && r > Math.sqrt(bestDist) + 1) break;
        }

        return bestPos;
    }


    /**
     * Sends the Guardian's pathfinder toward the surface directly above the vein.
     *
     * <p>The pathfinder needs a walkable Y position — passing the ore's actual Y
     * would send it underground. We find the first open (non-solid) block column
     * above the vein and use that Y as the navigation target.</p>
     */
    private void navigateTo(BlockPos vein) {
        BlockPos walkTarget = vein;
        for (int i = 0; i < 96; i++) {
            BlockPos above = walkTarget.above();
            if (!this.guardian.level().getBlockState(above).isSolid()) {
                walkTarget = above;
                break;
            }
            walkTarget = above;
        }

        this.guardian.getNavigation().moveTo(
                vein.getX() + 0.5,
                walkTarget.getY(),
                vein.getZ() + 0.5,
                ESCORT_SPEED
        );
    }


    /** Emits a small burst of block-break particles above the target vein. */
    private void spawnGuideParticles() {
        if (!(this.guardian.level() instanceof ServerLevel serverLevel)) return;
        if (this.targetVein == null) return;

        Block      target = this.guardian.getScoutBlock();
        if (target == null) return;
        BlockState state  = target.defaultBlockState();

        double px = this.targetVein.getX() + 0.5;
        double py = this.targetVein.getY() + 1.2;
        double pz = this.targetVein.getZ() + 0.5;

        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                px, py, pz,
                PARTICLE_COUNT,
                0.3, 0.3, 0.3,
                0.05
        );
    }

    /** Emits a large celebratory burst when the Guardian arrives at the vein. */
    private void spawnArrivalParticles() {
        if (!(this.guardian.level() instanceof ServerLevel serverLevel)) return;
        if (this.targetVein == null) return;

        Block      target = this.guardian.getScoutBlock();
        if (target == null) return;
        BlockState state  = target.defaultBlockState();

        double px = this.targetVein.getX() + 0.5;
        double py = this.targetVein.getY() + 1.0;
        double pz = this.targetVein.getZ() + 0.5;

        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                px, py, pz,
                40,
                0.6, 0.6, 0.6,
                0.15
        );
    }
}