package net.guardian_of_stone.world.entitites.interfaces;

/**
 * Manages the Guardian's active/dormant state — the core toggle that determines
 * whether it behaves like a living golem or a stone statue.
 *
 * <h2>State semantics</h2>
 * <ul>
 *   <li><b>Dormant ({@code active = false}):</b> The Guardian stands perfectly still.
 *       Navigation, targets, and horizontal movement are all cleared. Most goals
 *       suppress themselves by checking {@link #isActive()}.</li>
 *   <li><b>Awake ({@code active = true}):</b> Normal AI runs — movement, combat,
 *       scouting. State is synchronized to clients via {@code DATA_ACTIVE} so
 *       the renderer can switch between idle and active animations.</li>
 * </ul>
 *
 * <h2>Who owns the state?</h2>
 * <p>{@code updateActiveState()} (called each server tick) is the normal authority.
 * However {@link net.guardian_of_stone.world.entitites.ai.goals.attack.GuardianWardenFleeGoal}
 * and {@link net.guardian_of_stone.world.entitites.ai.goals.other.GuardianAvoidSculkGoal}
 * may forcibly override the flag when a Warden or sculk is nearby.</p>
 */
public interface IGuardianActiveState {

    /**
     * Returns whether the Guardian is currently awake.
     *
     * @return {@code true} if active
     */
    boolean isActive();

    /**
     * Sets the Guardian's active state and broadcasts the change to clients.
     * <p>Setting {@code false} additionally clears navigation, target, and
     * horizontal velocity.</p>
     *
     * @param active {@code true} to awaken, {@code false} to make dormant
     */
    void setActive(boolean active);

    /**
     * Returns whether the Guardian is currently panicking (fleeing at high speed).
     *
     * @return {@code true} if panicking
     */
    boolean isPanicking();

    /**
     * Sets the panicking flag and, when {@code true}, broadcasts the panic
     * animation event to all tracking clients.
     *
     * @param panicking {@code true} to trigger flee animation/speed
     */
    void setPanicking(boolean panicking);
}