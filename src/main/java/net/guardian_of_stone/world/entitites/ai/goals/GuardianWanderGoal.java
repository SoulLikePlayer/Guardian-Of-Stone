package net.guardian_of_stone.world.entitites.ai.goals;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

/**
 * A random-wander goal for the {@link GuardianOfStoneEntity}.
 *
 * <p>Wraps the vanilla {@link RandomStrollGoal} and suppresses it while the
 * Guardian is dormant. A dormant Guardian must remain perfectly still —
 * any movement would break the "stone statue" illusion.</p>
 *
 * <h2>Wander characteristics</h2>
 * <ul>
 *   <li>Speed modifier: {@code 0.8} — the Guardian walks slowly and deliberately.</li>
 *   <li>Interval: every {@code 120} ticks (6 seconds) a new random destination
 *       is chosen, keeping movement infrequent.</li>
 * </ul>
 */
public class GuardianWanderGoal extends RandomStrollGoal {

    /** The owning Guardian, kept for active-state checks. */
    private final GuardianOfStoneEntity guardian;

    /**
     * Constructs the goal and binds it to its owner.
     *
     * @param guardian the Guardian that will execute this goal
     */
    public GuardianWanderGoal(GuardianOfStoneEntity guardian) {
        super(guardian, 0.8, 120);
        this.guardian = guardian;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Suppresses all wandering while the Guardian is dormant.</p>
     */
    @Override
    public boolean canUse() {
        return this.guardian.isActive() && super.canUse();
    }
}