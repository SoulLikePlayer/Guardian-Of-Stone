package net.guardian_of_stone.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.world.entity.AnimationState;

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
 *
 * <h2>Death animation — "L'Effondrement Tectonique"</h2>
 * <p>A three-phase collapse designed to convey enormous mass. The Guardian does
 * not simply fall — it resists, fractures, then surrenders to gravity like a
 * crumbling cliff face.</p>
 * <ol>
 *   <li><b>0.0–0.4s (vacillement)</b>: The legs buckle outward, the torso
 *       pitches backward and sways — the golem fights to stay upright. Arms
 *       fly open in a desperate bid for balance. The head snaps back.</li>
 *   <li><b>0.4–0.8s (chute)</b>: Structural failure. The torso lurches
 *       violently forward while the head whips forward by inertia —
 *       like a stone wall tipping over its own centre of gravity. Legs
 *       fold beneath it.</li>
 *   <li><b>0.8–1.5s (impact & repos)</b>: The body crashes flat and bounces
 *       once with a shallow rebound before settling into dead stillness.
 *       The arms slap outward on impact then go limp. Total: 1.5 s (30 ticks).</li>
 * </ol>
 *
 */
public class GuardianOfStoneAnimation {

    /**
     * Heavy, asymmetric walking cycle. To be driven by
     * {@link net.minecraft.client.animation.KeyframeAnimation#applyWalk}
     * so its speed scales automatically with movement speed.
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
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec( 2.0F,  4.0F,  3.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F,  KeyframeAnimations.degreeVec( 4.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec( 2.0F, -4.0F, -3.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec( 4.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec( 0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.3F,  KeyframeAnimations.degreeVec( 3.0F,  2.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.6F,  KeyframeAnimations.degreeVec( 0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.8F,  KeyframeAnimations.degreeVec( 3.0F, -2.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec( 0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .build();


    public static final AnimationDefinition GUARDIAN_ATTACK = AnimationDefinition.Builder
            .withLength(1.0F)
            .addAnimation("right_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(   0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.3F,  KeyframeAnimations.degreeVec(-160.0F,  0.0F, 10.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.7F,  KeyframeAnimations.degreeVec(  90.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(   0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-15.0F, 20.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.7F,  KeyframeAnimations.degreeVec( 20.0F,-20.0F,  0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.3F,  KeyframeAnimations.degreeVec(-10.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.7F,  KeyframeAnimations.degreeVec( 15.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .build();


    /**
     * "L'Effondrement Tectonique" — a three-phase collapse. Total: 1.5 s (30 ticks).
     */
    public static final AnimationDefinition GUARDIAN_DEATH = AnimationDefinition.Builder
            .withLength(1.5F)
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.2F,  KeyframeAnimations.degreeVec(-20.0F, 8.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec(-25.0F, 5.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 60.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( 90.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.1F,  KeyframeAnimations.degreeVec( 88.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( 90.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F,  KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.posVec(0.0F,  1.2F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.82F, KeyframeAnimations.posVec(0.0F,-13.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.1F,  KeyframeAnimations.posVec(0.0F,-12.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.posVec(0.0F,-13.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.15F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec( 20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.95F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( -5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("left_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.35F, KeyframeAnimations.degreeVec( 15.0F,  0.0F,-45.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.6F,  KeyframeAnimations.degreeVec( 30.0F,  0.0F,-20.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( -5.0F,  0.0F,-70.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.05F, KeyframeAnimations.degreeVec(  0.0F,  0.0F,-62.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,-65.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("right_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec( 20.0F, 0.0F, 38.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 35.0F, 0.0F, 18.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( -5.0F, 0.0F, 60.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.05F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 52.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 56.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("right_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.3F,  KeyframeAnimations.degreeVec( 12.0F, 0.0F, 5.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 58.0F, 0.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( 90.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( 90.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("left_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.35F, KeyframeAnimations.degreeVec(  8.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 45.0F, 0.0F,-15.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( 90.0F, 0.0F,-12.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.05F, KeyframeAnimations.degreeVec( 88.0F, 0.0F,-10.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( 90.0F, 0.0F,-11.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("right_leg",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F,  KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.posVec(0.0F, -13.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.1F,  KeyframeAnimations.posVec(0.0F,-12.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.posVec(0.0F, -13.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("left_leg",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F,  KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.posVec(0.0F, -13.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.1F,  KeyframeAnimations.posVec(0.0F,-12.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.posVec(0.0F, -13.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .build();

    public static final AnimationDefinition GUARDIAN_POINT = AnimationDefinition.Builder
            .withLength(0.0F)
            .build();

    public static final AnimationDefinition GUARDIAN_GROUND_SLAM = AnimationDefinition.Builder
            .withLength(2.25F)
            .addAnimation("right_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(   0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.35F, KeyframeAnimations.degreeVec(-120.0F,  0.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.55F, KeyframeAnimations.degreeVec(-170.0F,  0.0F, 18.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.80F, KeyframeAnimations.degreeVec(-175.0F,  0.0F, 20.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.50F, KeyframeAnimations.degreeVec( 110.0F,  0.0F,  8.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.68F, KeyframeAnimations.degreeVec( 100.0F,  0.0F,  6.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(2.25F, KeyframeAnimations.degreeVec(   0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("left_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(   0.0F,  0.0F,   0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.35F, KeyframeAnimations.degreeVec(-120.0F,  0.0F, -12.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.55F, KeyframeAnimations.degreeVec(-170.0F,  0.0F, -18.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.80F, KeyframeAnimations.degreeVec(-175.0F,  0.0F, -20.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.50F, KeyframeAnimations.degreeVec( 110.0F,  0.0F,  -8.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.68F, KeyframeAnimations.degreeVec( 100.0F,  0.0F,  -6.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(2.25F, KeyframeAnimations.degreeVec(   0.0F,  0.0F,   0.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.55F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.80F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.50F, KeyframeAnimations.degreeVec( 55.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.68F, KeyframeAnimations.degreeVec( 50.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(2.25F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.00F, KeyframeAnimations.posVec(0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.55F, KeyframeAnimations.posVec(0.0F,  2.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.80F, KeyframeAnimations.posVec(0.0F,  2.0F, -1.5F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.50F, KeyframeAnimations.posVec(0.0F, -2.0F,  3.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.68F, KeyframeAnimations.posVec(0.0F, -1.0F,  2.5F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(2.25F, KeyframeAnimations.posVec(0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.55F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.80F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.50F, KeyframeAnimations.degreeVec( 35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.68F, KeyframeAnimations.degreeVec( 30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(2.25F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("right_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.55F, KeyframeAnimations.degreeVec( 30.0F, 0.0F, 4.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.80F, KeyframeAnimations.degreeVec( 32.0F, 0.0F, 4.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.50F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.68F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(2.25F, KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("left_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.55F, KeyframeAnimations.degreeVec( 30.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.80F, KeyframeAnimations.degreeVec( 32.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.50F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.68F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(2.25F, KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .build();

    /**
     * Frantic panic run — the Guardian bolts away in blind fear.
     *
     * A fast (0.5 s cycle), chaotic sprint: torso pitched far forward,
     * legs making wide desperate strides, arms flailing outward and back
     * with no combat elegance — pure survival instinct carved in stone.
     */
    public static final AnimationDefinition GUARDIAN_PANIC = AnimationDefinition.Builder
            .withLength(0.5F)
            .looping()
            .addAnimation("right_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(-55.0F, 0.0F,  3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec( 50.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.50F, KeyframeAnimations.degreeVec(-55.0F, 0.0F,  3.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("left_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec( 50.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-55.0F, 0.0F,  3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.50F, KeyframeAnimations.degreeVec( 50.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(22.0F,  4.0F,  2.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(22.0F, -4.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.50F, KeyframeAnimations.degreeVec(22.0F,  4.0F,  2.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(-10.0F,  6.0F,  3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec( -8.0F, -6.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.50F, KeyframeAnimations.degreeVec(-10.0F,  6.0F,  3.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("right_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec( 60.0F,  0.0F,  30.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-40.0F,  0.0F,  15.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.50F, KeyframeAnimations.degreeVec( 60.0F,  0.0F,  30.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .addAnimation("left_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.00F, KeyframeAnimations.degreeVec(-40.0F,  0.0F, -15.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec( 60.0F,  0.0F, -30.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.50F, KeyframeAnimations.degreeVec(-40.0F,  0.0F, -15.0F), AnimationChannel.Interpolations.CATMULLROM)
                    )
            )
            .build();
}