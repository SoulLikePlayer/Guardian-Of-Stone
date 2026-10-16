package guardian_of_stone.code.client.animation.definition;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class GuardianOfStoneAnimation {
    public static final AnimationDefinition GUARDIAN_WALKING;
    public static final AnimationDefinition GUARDIAN_ATTACK;
    public static final AnimationDefinition GUARDIAN_ENCHANT;
    public static final AnimationDefinition GUARDIAN_SMELT;
    public static final AnimationDefinition GUARDIAN_REPAIR;
    public static final AnimationDefinition GUARDIAN_POLISH;
    public static final AnimationDefinition GUARDIAN_MIDAS;
    public static final AnimationDefinition GUARDIAN_TRADE;

    private static Keyframe rot(float t, float x, float y, float z) {
        return new Keyframe(t, KeyframeAnimations.degreeVec(x, y, z), AnimationChannel.Interpolations.CATMULLROM);
    }

    private static AnimationChannel rotation(Keyframe... k) {
        return new AnimationChannel(AnimationChannel.Targets.ROTATION, k);
    }

    static {
        GUARDIAN_WALKING = AnimationDefinition.Builder.withLength(1.0F).looping()
                .addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(2.0F, 4.0F, 2.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.degreeVec(2.0F, -4.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, -3.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 3.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .build();

        GUARDIAN_ATTACK = AnimationDefinition.Builder.withLength(1.5F)
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.35F, KeyframeAnimations.degreeVec(-160.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.42F, KeyframeAnimations.degreeVec(-168.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.6F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.39F, KeyframeAnimations.degreeVec(-155.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.46F, KeyframeAnimations.degreeVec(-163.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.56F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.64F, KeyframeAnimations.degreeVec(-40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.35F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.42F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.6F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.35F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.42F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.6F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.42F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, -1.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, -1.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.42F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, -1.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, -1.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.42F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, -1.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, -1.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.42F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, -1.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, -1.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .build();

        GUARDIAN_ENCHANT = AnimationDefinition.Builder.withLength(3.5F)
                .addAnimation("right_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.7F, -80, 25, 0),
                        rot(1.4F, -85, 20, 0),
                        rot(2.1F, -80, 28, 0),
                        rot(2.8F, -92, 22, 0),
                        rot(3.1F, -115, 12, 0),
                        rot(3.5F, 0, 0, 0)))
                .addAnimation("left_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.7F, -80, -25, 0),
                        rot(1.4F, -85, -20, 0),
                        rot(2.1F, -80, -28, 0),
                        rot(2.8F, -92, -22, 0),
                        rot(3.1F, -115, -12, 0),
                        rot(3.5F, 0, 0, 0)))
                .addAnimation("body", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.7F, -6, 0, 0),
                        rot(2.8F, -8, 0, 0),
                        rot(3.1F, 5, 0, 0),
                        rot(3.5F, 0, 0, 0)))
                .addAnimation("head", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.7F, -14, 0, 0),
                        rot(2.8F, -14, 0, 0),
                        rot(3.1F, -4, 0, 0),
                        rot(3.5F, 0, 0, 0)))
                .build();

        GUARDIAN_SMELT = AnimationDefinition.Builder.withLength(3.0F)
                .addAnimation("right_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.5F, -80, -12, 0),
                        rot(1.0F, -82, -10, 2),
                        rot(1.5F, -80, -12, -2),
                        rot(2.0F, -83, -10, 3),
                        rot(2.25F, -81, -13, -3),
                        rot(2.5F, -83, -10, 3),
                        rot(2.7F, -80, -12, 0),
                        rot(3.0F, 0, 0, 0)))
                .addAnimation("left_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.5F, -45, 30, 0),
                        rot(2.7F, -45, 30, 0),
                        rot(3.0F, 0, 0, 0)))
                .addAnimation("body", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.5F, 8, -5, 0),
                        rot(2.0F, 8, -5, 0),
                        rot(2.25F, 9, -3, 1),
                        rot(2.5F, 8, -6, -1),
                        rot(2.7F, 8, -5, 0),
                        rot(3.0F, 0, 0, 0)))
                .addAnimation("head", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.5F, 20, 0, 0),
                        rot(2.7F, 20, 0, 0),
                        rot(3.0F, 0, 0, 0)))
                .build();

        GUARDIAN_REPAIR = AnimationDefinition.Builder.withLength(2.5F)
                .addAnimation("right_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, -55, -35, 0),
                        rot(2.1F, -55, -35, 0),
                        rot(2.5F, 0, 0, 0)))
                .addAnimation("left_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, -45, 55, 0),
                        rot(0.6F, -80, 40, 0),
                        rot(0.9F, -45, 55, 0),
                        rot(1.2F, -80, 40, 0),
                        rot(1.5F, -45, 55, 0),
                        rot(1.8F, -80, 40, 0),
                        rot(2.1F, -45, 55, 0),
                        rot(2.5F, 0, 0, 0)))
                .addAnimation("body", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, 6, -8, 0),
                        rot(0.6F, 8, -4, 0),
                        rot(0.9F, 6, -8, 0),
                        rot(1.2F, 8, -4, 0),
                        rot(1.5F, 6, -8, 0),
                        rot(1.8F, 8, -4, 0),
                        rot(2.1F, 6, -8, 0),
                        rot(2.5F, 0, 0, 0)))
                .addAnimation("head", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, 22, -10, 0),
                        rot(2.1F, 22, -10, 0),
                        rot(2.5F, 0, 0, 0)))
                .build();

        GUARDIAN_POLISH = AnimationDefinition.Builder.withLength(2.0F)
                .addAnimation("right_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, -55, 25, 0),
                        rot(0.55F, -55, -30, 0),
                        rot(1.05F, -55, 25, 0),
                        rot(1.55F, -55, -30, 0),
                        rot(1.8F, -55, 0, 0),
                        rot(2.0F, 0, 0, 0)))
                .addAnimation("left_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, -30, 25, 0),
                        rot(1.8F, -30, 25, 0),
                        rot(2.0F, 0, 0, 0)))
                .addAnimation("body", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, 5, 5, 0),
                        rot(0.55F, 5, -6, 0),
                        rot(1.05F, 5, 5, 0),
                        rot(1.55F, 5, -6, 0),
                        rot(1.8F, 3, 0, 0),
                        rot(2.0F, 0, 0, 0)))
                .addAnimation("head", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.3F, 15, 0, 0),
                        rot(1.8F, 15, 0, 0),
                        rot(2.0F, 0, 0, 0)))
                .build();

        GUARDIAN_MIDAS = AnimationDefinition.Builder.withLength(2.5F)
                .addAnimation("right_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.5F, -95, -8, 0),
                        rot(1.0F, -95, -8, 3),
                        rot(1.3F, -95, -8, -3),
                        rot(1.6F, -95, -8, 3),
                        rot(1.9F, -100, -8, 0),
                        rot(2.15F, -125, 0, 0),
                        rot(2.5F, 0, 0, 0)))
                .addAnimation("left_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.6F, -75, 20, 0),
                        rot(1.0F, -75, 20, 3),
                        rot(1.3F, -75, 20, -3),
                        rot(1.6F, -75, 20, 3),
                        rot(1.9F, -75, 20, 0),
                        rot(2.15F, -40, 30, 0),
                        rot(2.5F, 0, 0, 0)))
                .addAnimation("body", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.5F, 6, 0, 0),
                        rot(1.9F, 6, 0, 0),
                        rot(2.15F, -10, 0, 0),
                        rot(2.5F, 0, 0, 0)))
                .addAnimation("head", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.5F, 22, 0, 0),
                        rot(1.9F, 22, 0, 0),
                        rot(2.15F, -15, 0, 0),
                        rot(2.5F, 0, 0, 0)))
                .build();

        GUARDIAN_TRADE = AnimationDefinition.Builder.withLength(2.0F)
                .addAnimation("right_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.35F, -50, -35, 0),
                        rot(1.0F, -50, -35, 0),
                        rot(1.2F, -80, 0, 0),
                        rot(1.75F, -80, 0, 0),
                        rot(2.0F, 0, 0, 0)))
                .addAnimation("left_arm", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.35F, -50, 35, 0),
                        rot(1.0F, -50, 35, 0),
                        rot(1.3F, -20, 15, 0),
                        rot(1.75F, -20, 15, 0),
                        rot(2.0F, 0, 0, 0)))
                .addAnimation("body", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.35F, 4, 0, 0),
                        rot(1.0F, 4, 0, 0),
                        rot(1.3F, 8, 0, 0),
                        rot(1.75F, 8, 0, 0),
                        rot(2.0F, 0, 0, 0)))
                .addAnimation("head", rotation(
                        rot(0.0F, 0, 0, 0),
                        rot(0.4F, 15, 0, 0),
                        rot(0.6F, 15, -12, 0),
                        rot(0.9F, 15, 12, 0),
                        rot(1.2F, 10, 0, 0),
                        rot(1.3F, 18, 0, 0),
                        rot(1.55F, 0, 0, 0),
                        rot(1.8F, 18, 0, 0),
                        rot(2.0F, 0, 0, 0)))
                .build();
    }
}