package net.guardian_of_stone.client.renderer.entity.state;

import net.guardian_of_stone.world.entitites.OreVariant;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

/**
 * Holds all per-frame data needed to render the {@code GuardianOfStoneEntity}.
 *
 * <p>This class is populated each frame by the renderer's
 * {@code extractRenderState} method and consumed by
 * {@code GuardianOfStoneModel#setupAnim}. It intentionally contains no logic —
 * it is a plain data carrier (a "render snapshot") to keep rendering fully
 * decoupled from gameplay logic.</p>
 *
 * <h2>Fields</h2>
 * <dl>
 *   <dt>{@link #attackAnimationState}</dt>
 *   <dd>Drives the arm-swing animation. Started/stopped by the renderer
 *       based on the entity's swinging flag.</dd>
 *
 *   <dt>{@link #deathAnimationState}</dt>
 *   <dd>Drives the collapse/crumble animation. Started once on death and
 *       never reset (the entity is removed from the world shortly after).</dd>
 *
 *   <dt>{@link #canMove}</dt>
 *   <dd>{@code true} when the Guardian is active (awake). The model uses this
 *       to decide whether to play the walk cycle.</dd>
 * </dl>
 */
public class GuardianOfStoneEntityRenderState extends LivingEntityRenderState {

    /**
     * Animation state for the melee-attack swing.
     *
     * <p>Mirrors the entity-side {@code attackAnimationState} field; the
     * renderer copies the reference each frame.</p>
     */
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState groundSlamAnimationState = new AnimationState();
    public final AnimationState panicAnimationState = new AnimationState();

    /**
     * Animation state for the death sequence.
     *
     * <p>Mirrors the entity-side {@code deathAnimationState} field.</p>
     */
    public final AnimationState deathAnimationState = new AnimationState();

    /**
     * Whether the Guardian is currently active (awake and able to move).
     *
     * <p>When {@code false} the walk cycle is suppressed and the model
     * freezes in its default T-pose, giving the appearance of a stone statue.</p>
     */
    public boolean canMove;
    public int hurtTime;
    public final AnimationState pointAnimationState = new AnimationState();

    /** Raw target yaw/pitch toward the ore block (world-space, radians). */
    public float pointingYaw   = 0.0F;
    public float pointingPitch = 0.0F;
    public boolean isPointing  = false;
    public boolean isScouting = false;
    public boolean isPanicking = false;

    /**
     * Smoothed arm yaw/pitch, updated every frame with a lerp so the arm
     * glides toward the target instead of snapping.
     */
    public float smoothArmYaw   = 0.0F;
    public float smoothArmPitch = 0.0F;

    /**
     * Smoothed head yaw/pitch for the same reason.
     */
    public float smoothHeadYaw   = 0.0F;
    public float smoothHeadPitch = 0.0F;
    public OreVariant oreVariant = OreVariant.NONE;
}