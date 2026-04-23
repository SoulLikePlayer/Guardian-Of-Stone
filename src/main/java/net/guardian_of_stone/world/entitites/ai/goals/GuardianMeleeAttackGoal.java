package net.guardian_of_stone.world.entitites.ai.goals;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * A melee-attack goal tailored for the {@link GuardianOfStoneEntity}.
 *
 * <p>This goal extends the vanilla {@link MeleeAttackGoal} with one additional
 * constraint: it will not start (or will immediately stop) if the Guardian is
 * currently dormant, i.e. {@link GuardianOfStoneEntity#isActive()} returns
 * {@code false}.</p>
 *
 * <p>This ensures the Guardian never lunges at a target while visually frozen
 * as a statue — the attack begins only once the awakening logic has flipped
 * the active flag.</p>
 *
 * <h2>Attack characteristics</h2>
 * <ul>
 *   <li>Speed modifier while chasing: {@code 1.2} (20 % faster than idle walk)</li>
 *   <li>Must have line-of-sight: {@code true}</li>
 * </ul>
 */
public class GuardianMeleeAttackGoal extends MeleeAttackGoal {

    /** The owning Guardian, kept for active-state checks. */
    private final GuardianOfStoneEntity guardian;

    /**
     * Constructs the goal and binds it to its owner.
     *
     * @param guardian the Guardian that will execute this goal
     */
    public GuardianMeleeAttackGoal(GuardianOfStoneEntity guardian) {
        super(guardian, 1.2, true);
        this.guardian = guardian;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Prevents the goal from starting while the Guardian is dormant.</p>
     */
    @Override
    public boolean canUse() {
        return this.guardian.isActive() && super.canUse();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Stops the goal immediately if the Guardian has gone back to dormancy
     * mid-combat (e.g., all threats have fled the detection radius).</p>
     */
    @Override
    public boolean canContinueToUse() {
        return this.guardian.isActive() && super.canContinueToUse();
    }
}