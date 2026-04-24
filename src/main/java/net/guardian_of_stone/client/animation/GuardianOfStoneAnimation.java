package net.guardian_of_stone.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Hand-crafted animation definitions for the {@code GuardianOfStoneEntity}.
 *
 * <h2>Design intent</h2>
 * <p>These animations are built to feel nothing like the Creaking. The Guardian
 * is a massive stone golem — every movement should feel heavy, deliberate, and
 * slightly mechanical, as if tectonic plates are shifting inside it.</p>
 *
 * <h2>Walk animation</h2>
 * <p>A lumbering, asymmetric gait. The right leg leads with a wide, slow arc
 * while the torso sways against it. Arms swing in full opposition with a slight
 * delay, like pendulums. The head bobs with inertia, always a half-beat behind
 * the body. Total cycle: 1.0s (20 ticks).</p>
 *
 * <h2>Attack animation</h2>
 * <p>A two-phase overhead smash. Phase 1 (wind-up): the right arm rises slowly
 * above the head while the torso coils backward — a catapult arming itself.
 * Phase 2 (release): a sudden, violent downward slam with full torso rotation,
 * landing at tick 14. The arm then drags back to rest over 6 ticks.
 * Total: 1.0s (20 ticks).</p>
 */
public class GuardianOfStoneAnimation {

    /**
     * Heavy, asymmetric walking cycle. To be driven by
     * {@link net.minecraft.client.animation.KeyframeAnimation#applyWalk}
     * so its speed scales automatically with movement speed.
     *
     * <p>Cycle breakdown (20 ticks / 1.0 s):
     * <ul>
     *   <li>Ticks 0–10 : right foot forward, left arm swings forward, torso sways right</li>
     *   <li>Ticks 10–20: left foot forward, right arm swings forward, torso sways left</li>
     * </ul>
     * The head bobs one tick behind the torso to simulate mass inertia.</p>
     */
    public static final AnimationDefinition GUARDIAN_WALK = AnimationDefinition.Builder
            .withLength(1.0F)
            .looping()
            .addAnimation("right_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F),  AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-38.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F),  AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec( 32.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F),  AnimationChannel.Interpolations.LINEAR)
                    )
            )

            .addAnimation("left_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F),  AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec( 30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F),  AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec(-36.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F),  AnimationChannel.Interpolations.LINEAR)
                    )
            )

            .addAnimation("right_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec( 40.0F,  0.0F,  5.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec(-34.0F,  0.0F, -5.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            .addAnimation("left_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-34.0F,  0.0F, -5.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec( 40.0F,  0.0F,  5.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec( 4.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec( 4.0F,  0.0F,  3.5F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F,  KeyframeAnimations.degreeVec( 4.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec( 4.0F,  0.0F, -3.5F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec( 4.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            
            
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F,  KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F,-0.6F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F,  KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F,-0.6F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            
            
            
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.125F,KeyframeAnimations.degreeVec(-2.0F, 0.0F, -2.5F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.375F,KeyframeAnimations.degreeVec( 2.0F, 0.0F,  2.5F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.625F,KeyframeAnimations.degreeVec(-2.0F, 0.0F, -2.5F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.875F,KeyframeAnimations.degreeVec( 2.0F, 0.0F,  2.5F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec( 0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            .build();


    /**
     * Overhead smash attack. Plays once per strike (driven by
     * {@link net.minecraft.client.animation.KeyframeAnimation#apply}).
     *
     * <p>Phase breakdown (20 ticks / 1.0 s):
     * <ol>
     *   <li>Ticks 0–8 (0.0–0.4 s) — <b>Wind-up</b>: right arm rises slowly
     *       overhead, torso coils backward and rotates left, left arm drops
     *       and extends back for counterbalance. The head tilts slightly
     *       upward, tracking the rising arm.</li>
     *   <li>Ticks 8–14 (0.4–0.7 s) — <b>Release</b>: explosive downward slam.
     *       The arm hammers forward, torso snaps forward and right.
     *       This is the damage frame window.</li>
     *   <li>Ticks 14–20 (0.7–1.0 s) — <b>Recovery</b>: everything eases
     *       back to neutral with a slight overshoot on the arm.</li>
     * </ol>
     * </p>
     */
    public static final AnimationDefinition GUARDIAN_ATTACK = AnimationDefinition.Builder
            .withLength(1.0F)

            
            .addAnimation("right_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(   0.0F,  0.0F,  0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec(-155.0F,  0.0F, -8.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.7F,  KeyframeAnimations.degreeVec(  65.0F,  0.0F, 12.0F), AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.85F, KeyframeAnimations.degreeVec(  30.0F,  0.0F,  5.0F), AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(   0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            
            
            
            .addAnimation("left_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F),AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec( 40.0F, 0.0F, 15.0F),AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.7F,  KeyframeAnimations.degreeVec(-25.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec(-18.0F, -22.0F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.7F,  KeyframeAnimations.degreeVec( 20.0F,  18.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(  0.0F,   0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.4F, KeyframeAnimations.posVec(0.0F,  0.8F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, -1.4F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.35F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.7F,  KeyframeAnimations.degreeVec( 18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            
            .addAnimation("right_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.4F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F),AnimationChannel.Interpolations.LINEAR),
                            
                            new Keyframe(0.7F, KeyframeAnimations.degreeVec( 5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F, KeyframeAnimations.degreeVec( 0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )

            .build();
}