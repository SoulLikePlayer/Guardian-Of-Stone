package guardian_of_stone.code.client.model;

import guardian_of_stone.code.client.animation.definition.GuardianOfStoneAnimation;
import guardian_of_stone.code.client.renderer.state.GuardianOfStoneRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GuardianOfStoneModel extends EntityModel<@NotNull GuardianOfStoneRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation attackAnimation;

    public GuardianOfStoneModel(ModelPart root) {
        super(root);

        this.walkAnimation = GuardianOfStoneAnimation.GUARDIAN_WALKING.bake(root);
        this.attackAnimation = GuardianOfStoneAnimation.GUARDIAN_ATTACK.bake(root);

        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F),
                PartPose.offset(-4.0F, -6.0F, 0.0F));

        partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(0.0F, -3.0F, -3.0F, 6.0F, 13.0F, 5.0F)
                        .texOffs(24, 0).addBox(-6.0F, -4.0F, -3.0F, 6.0F, 7.0F, 5.0F)
                        .texOffs(24, 43).addBox(-5.0F, 3.0F, -2.0F, 5.0F, 3.0F, 3.0F),
                PartPose.offset(-1.0F, -2.0F, 1.0F));

        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(34, 24).addBox(0.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F)
                        .texOffs(40, 43).addBox(0.0F, 15.0F, -1.5F, 3.0F, 4.0F, 3.0F),
                PartPose.offset(5.0F, -4.0F, 0.5F));

        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(22, 16).addBox(-2.0F, -1.5F, -1.5F, 3.0F, 21.0F, 3.0F)
                        .texOffs(34, 12).addBox(-3.0F, 11.5F, -2.5F, 5.0F, 7.0F, 5.0F),
                PartPose.offset(-8.0F, -4.5F, 1.5F));

        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(12, 40).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F),
                PartPose.offset(1.5F, 8.0F, 0.5F));

        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 34).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 19.0F, 3.0F),
                PartPose.offset(-1.0F, 6.5F, 0.5F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull GuardianOfStoneRenderState state) {
        super.setupAnim(state);

        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;

        float walkScale = state.attackAnimationState.isStarted() ? 0.3F : 1.5F;
        this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1.0F, walkScale);
        this.attackAnimation.apply(state.attackAnimationState, state.ageInTicks);
    }
}