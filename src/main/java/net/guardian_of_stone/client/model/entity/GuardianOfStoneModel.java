package net.guardian_of_stone.client.model.entity;

import net.guardian_of_stone.client.animation.GuardianOfStoneAnimation;
import net.guardian_of_stone.client.renderer.entity.state.GuardianOfStoneEntityRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

import static net.guardian_of_stone.client.animation.GuardianOfStoneAnimation.GUARDIAN_PANIC;
import static net.guardian_of_stone.client.animation.GuardianOfStoneAnimation.GUARDIAN_WALK;

public class GuardianOfStoneModel extends EntityModel<@NotNull GuardianOfStoneEntityRenderState> {

    private final ModelPart head;
    private final ModelPart upperBody;
    private final ModelPart rightArm;

    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation panicAnimation;
    private final KeyframeAnimation attackAnimation;
    private final KeyframeAnimation deathAnimation;
    private final KeyframeAnimation pointAnimation;
    private final KeyframeAnimation groundSlamAnimation;

    public GuardianOfStoneModel(ModelPart roots) {
        super(roots);
        ModelPart root = roots.getChild("root");
        this.upperBody = root.getChild("upper_body");
        this.head = this.upperBody.getChild("head");
        this.rightArm = this.upperBody.getChild("right_arm");

        this.walkAnimation   = GUARDIAN_WALK.bake(root);
        this.panicAnimation  = GUARDIAN_PANIC.bake(root);
        this.attackAnimation = GuardianOfStoneAnimation.GUARDIAN_ATTACK.bake(root);
        this.deathAnimation  = GuardianOfStoneAnimation.GUARDIAN_DEATH.bake(root);
        this.pointAnimation = GuardianOfStoneAnimation.GUARDIAN_POINT.bake(root);
        this.groundSlamAnimation = GuardianOfStoneAnimation.GUARDIAN_GROUND_SLAM.bake(root);
    }


    private static MeshDefinition createMesh() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild(
                "root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition upperBody = root.addOrReplaceChild(
                "upper_body", CubeListBuilder.create(), PartPose.offset(0.0F, -19.0F, 0.0F));

        upperBody.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 19).addBox(-2.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F),
                PartPose.offset(-4.0F, -11.0F, 0.0F));

        upperBody.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox( 0.0F, -4.0F, -3.0F, 6.0F, 14.0F, 5.0F)
                        .texOffs(24, 24).addBox(-6.0F, -4.0F, -3.0F, 6.0F,  7.0F, 5.0F)
                        .texOffs(24, 36).addBox(-4.0F,  3.0F, -2.5F, 4.0F,  4.0F, 4.0F),
                PartPose.offset(-1.0F, -7.0F, 1.0F));

        upperBody.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(12, 35).addBox(0.0F, -2.0F, -1.5F, 3.0F, 16.0F, 3.0F),
                PartPose.offset(5.0F, -9.0F, 0.5F));

        upperBody.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(24, 0).addBox(-2.0F,  -1.5F, -1.5F, 3.0F, 21.0F, 3.0F)
                        .texOffs(40,  36).addBox(-2.5F,  15.5F, -2.0F, 4.0F,  4.0F, 4.0F)
                        .texOffs(24,  44).addBox(-2.5F,  10.5F, -2.0F, 4.0F,  4.0F, 4.0F)
                        .texOffs(40,  44).addBox(-2.5F,   5.5F, -2.0F, 4.0F,  4.0F, 4.0F),
                PartPose.offset(-8.0F, -9.5F, 1.5F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(36, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F),
                PartPose.offset(1.5F, -16.0F, 0.5F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 35).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 19.0F, 3.0F),
                PartPose.offset(-1.0F, -17.5F, 0.5F));

        return meshDefinition;
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createMesh(), 64, 64);
    }

    @Override
    public void setupAnim(@NotNull GuardianOfStoneEntityRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * ((float) Math.PI / 180F);
        this.head.yRot = state.yRot * ((float) Math.PI / 180F);

        if (state.isPanicking) {
            this.panicAnimation.applyWalk(
                    state.walkAnimationPos,
                    state.walkAnimationSpeed,
                    1.0F,
                    1.0F
            );
        } else if (state.canMove) {
            this.walkAnimation.applyWalk(
                    state.walkAnimationPos,
                    state.walkAnimationSpeed,
                    1.0F,
                    1.0F
            );
        }

        float epsilon = 0.001F;
        boolean hasArmMotion  = Math.abs(state.smoothArmYaw)   > epsilon
                || Math.abs(state.smoothArmPitch)  > epsilon;
        boolean hasHeadMotion = Math.abs(state.smoothHeadYaw)  > epsilon
                || Math.abs(state.smoothHeadPitch) > epsilon;

        if (state.isPointing || hasArmMotion) {
            this.rightArm.yRot = state.smoothArmYaw;
            this.rightArm.xRot = state.smoothArmPitch - (float)(Math.PI / 2.0);
        }

        if (state.isPointing || hasHeadMotion) {
            this.head.yRot = state.yRot * ((float) Math.PI / 180F) + state.smoothHeadYaw;
            this.head.xRot = state.xRot * ((float) Math.PI / 180F) + state.smoothHeadPitch;
        }

        this.attackAnimation.apply(state.attackAnimationState, state.ageInTicks);
        this.deathAnimation.apply(state.deathAnimationState, state.ageInTicks);
        this.pointAnimation.apply(state.pointAnimationState, state.ageInTicks);
        this.groundSlamAnimation.apply(state.groundSlamAnimationState, state.ageInTicks);
    }
}