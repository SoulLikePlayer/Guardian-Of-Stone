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


    /**
     * Death collapse — "L'Effondrement Tectonique". Plays once, never resets.
     * Driven by {@link net.minecraft.client.animation.KeyframeAnimation#apply}.
     *
     * <p>Phase breakdown (30 ticks / 1.5 s):
     * <ol>
     *   <li><b>0.0–0.4 s — Vacillement (stagger)</b>: The Guardian absorbs the
     *       killing blow. Legs buckle outward asymmetrically, the torso recoils
     *       backward, and both arms fly open sideways as if catching a wall that
     *       isn't there. The head snaps back by sheer momentum. The posture reads
     *       "I am still standing — but only just."</li>
     *
     *   <li><b>0.4–0.8 s — Chute (fall)</b>: Structural failure. The torso
     *       pitches 85° forward — the tipping point past which no recovery is
     *       possible. The head whips forward by inertia, overshooting the torso.
     *       The right leg folds under the body while the left leg splays outward.
     *       Arms begin to spread wide for the coming ground impact.</li>
     *
     *   <li><b>0.8–1.1 s — Impact</b>: The body crashes into the ground.
     *       The torso reaches ~90° (fully prone). Arms slam outward and slightly
     *       upward on impact — the "slap" of stone meeting earth. The head clunks
     *       down last, a beat after the torso, because stone heads are heavy.
     *       Position drops sharply to simulate the body meeting the ground.</li>
     *
     *   <li><b>1.1–1.5 s — Repos (settling)</b>: A single shallow rebound —
     *       the body lifts 10% then settles permanently. Arms droop limp.
     *       Everything goes still. The Guardian is stone again.</li>
     * </ol>
     * </p>
     *
     * <p><b>Choreographic notes:</b>
     * <ul>
     *   <li>The asymmetric leg fold (right under, left splayed) avoids the
     *       "ragdoll" look of symmetric collapses.</li>
     *   <li>The head always lags the torso by ~0.1 s to sell the weight of
     *       the skull.</li>
     *   <li>The brief upward arm flick on impact (0.8 s) reads as involuntary
     *       — the arms bounce off the ground like dead weight.</li>
     *   <li>CATMULLROM interpolation on the torso fall gives an ease-in that
     *       makes gravity feel real rather than mechanical.</li>
     * </ul>
     * </p>
     */
    public static final AnimationDefinition GUARDIAN_DEATH = AnimationDefinition.Builder
            .withLength(1.5F)
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.2F,  KeyframeAnimations.degreeVec(-20.0F, 8.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec(-28.0F, 5.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 55.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( 88.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.1F,  KeyframeAnimations.degreeVec( 82.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( 85.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("upper_body",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F,  KeyframeAnimations.posVec(0.0F,  0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.posVec(0.0F,  1.2F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.82F, KeyframeAnimations.posVec(0.0F, -6.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.1F,  KeyframeAnimations.posVec(0.0F, -5.2F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.posVec(0.0F, -5.8F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.15F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec( 25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.92F, KeyframeAnimations.degreeVec( 10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( 12.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("left_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.35F, KeyframeAnimations.degreeVec( 15.0F,  0.0F,-45.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.6F,  KeyframeAnimations.degreeVec( 30.0F,  0.0F,-20.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec(-10.0F,  0.0F,-60.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.05F, KeyframeAnimations.degreeVec( -5.0F,  0.0F,-50.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec(  0.0F,  0.0F,-55.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("right_arm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4F,  KeyframeAnimations.degreeVec( 20.0F, 0.0F, 38.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 35.0F, 0.0F, 18.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( -8.0F, 0.0F, 52.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.05F, KeyframeAnimations.degreeVec( -3.0F, 0.0F, 44.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 48.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("right_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.3F,  KeyframeAnimations.degreeVec( 12.0F, 0.0F, 5.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 55.0F, 0.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( 80.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( 78.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation("left_leg",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F,  KeyframeAnimations.degreeVec(  0.0F, 0.0F,  0.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.35F, KeyframeAnimations.degreeVec(  8.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.65F, KeyframeAnimations.degreeVec( 40.0F, 0.0F,-15.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.82F, KeyframeAnimations.degreeVec( 65.0F, 0.0F,-12.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.05F, KeyframeAnimations.degreeVec( 60.0F, 0.0F,-10.0F), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F,  KeyframeAnimations.degreeVec( 62.0F, 0.0F,-11.0F), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .build();
}