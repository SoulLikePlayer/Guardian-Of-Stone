package net.guardian_of_stone.world.entitites.ai.goals;

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
 *   <li>Each tick of {@link #tick()} walks the Guardian toward the found vein,
 *       emitting block-break particles so the player can follow.</li>
 *   <li>The goal ends when the Guardian arrives at the vein ({@link #ARRIVE_DIST_SQ}),
 *       the scouting flag is cleared, or a combat threat interrupts.</li>
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

    private static final double ESCORT_SPEED = 0.6;
    private static final double ARRIVE_DIST_SQ = 9.0; // 3 blocks

    private static final int PARTICLE_INTERVAL = 15;
    private static final int PARTICLE_COUNT = 6;

    private static final int MAX_ESCORT_TICKS = 20 * 60; // 60 s


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
     * <p>Active only when the Guardian is in scouting mode <em>and</em> not
     * currently fighting a threat (target takes priority).</p>
     */
    @Override
    public boolean canUse() {
        return this.guardian.isScouting()
                && this.guardian.getTarget() == null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Stop if scouting was cancelled (e.g. combat interrupted), if we ran out
     * of escort time, or if the vein was never found.</p>
     */
    @Override
    public boolean canContinueToUse() {
        return this.guardian.isScouting()
                && this.guardian.getTarget() == null
                && this.targetVein != null
                && this.escortTicksRemaining > 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Performs the spiral ore search synchronously on {@code start()}. Searching
     * once up-front is acceptable because the search is bounded and runs only when
     * the player explicitly hands an item — not every tick.</p>
     */
    @Override
    public void start() {
        this.tickCounter           = 0;
        this.escortTicksRemaining  = MAX_ESCORT_TICKS;
        this.targetVein            = findNearestVein();

        if (this.targetVein == null) {
            // Nothing found — signal failure immediately so the flag gets cleared.
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

        // Periodically burst particles above the target vein so the player can follow.
        if (this.tickCounter % PARTICLE_INTERVAL == 0) {
            spawnGuideParticles();
        }

        // Check arrival.
        double distSq = this.guardian.blockPosition().distSqr(this.targetVein);
        if (distSq <= ARRIVE_DIST_SQ) {
            // Arrived — do a final large particle burst then finish.
            spawnArrivalParticles();
            this.guardian.stopScouting(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        this.targetVein = null;
        this.guardian.getNavigation().stop();
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Scans the world in concentric horizontal rings around the Guardian's feet,
     * returning the closest block position whose {@link BlockState} matches
     * {@link GuardianOfStoneEntity#getScoutBlock()}.
     *
     * @return the nearest matching {@link BlockPos}, or {@code null} if nothing found
     */
    @Nullable
    private BlockPos findNearestVein() {
        Block target    = this.guardian.getScoutBlock();
        if (target == null) return null;

        BlockPos origin  = this.guardian.blockPosition();
        BlockPos bestPos = null;
        double   bestDist = Double.MAX_VALUE;

        for (int r = 0; r <= SEARCH_RADIUS; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    // Only process the shell of this ring.
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

            // Early exit: if we already found a block in a previous ring and are now
            // entering a ring whose minimum possible distance exceeds our best, stop.
            if (bestPos != null && r > Math.sqrt(bestDist) + 1) break;
        }

        return bestPos;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /** Sends the Guardian's pathfinder toward the given block position. */
    private void navigateTo(BlockPos pos) {
        this.guardian.getNavigation().moveTo(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                ESCORT_SPEED
        );
    }

    // ── Particles ─────────────────────────────────────────────────────────────

    /**
     * Emits a small burst of block-break particles above the target vein so
     * the player can follow the trail.
     */
    private void spawnGuideParticles() {
        if (!(this.guardian.level() instanceof ServerLevel serverLevel)) return;
        if (this.targetVein == null) return;

        Block      target   = this.guardian.getScoutBlock();
        if (target == null) return;
        BlockState state    = target.defaultBlockState();

        // Particles float just above the vein surface.
        double px = this.targetVein.getX() + 0.5;
        double py = this.targetVein.getY() + 1.2;
        double pz = this.targetVein.getZ() + 0.5;

        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                px, py, pz,
                PARTICLE_COUNT,
                0.3, 0.3, 0.3, // spread
                0.05             // speed
        );
    }

    /**
     * Emits a large celebratory burst when the Guardian arrives, clearly
     * marking the vein position for the player.
     */
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
                40,             // big burst
                0.6, 0.6, 0.6,
                0.15
        );
    }
}