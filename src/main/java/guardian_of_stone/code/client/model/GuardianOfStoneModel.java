package guardian_of_stone.code.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import guardian_of_stone.code.client.animation.definition.GuardianOfStoneAnimation;
import guardian_of_stone.code.client.renderer.state.GuardianOfStoneRenderState;
import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class GuardianOfStoneModel extends EntityModel<@NotNull GuardianOfStoneRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation attackAnimation;
    private final KeyframeAnimation panicAnimation;

    private static final float FEAR_INTENSITY = 0.3F;
    private static final float MOSS_SHAKE_INTENSITY = 2.5F;

    private final Map<GuardianAction, KeyframeAnimation> actionAnimations = new EnumMap<>(GuardianAction.class);

    public GuardianOfStoneModel(ModelPart root) {
        super(root);

        this.walkAnimation = GuardianOfStoneAnimation.GUARDIAN_WALKING.bake(root);
        this.attackAnimation = GuardianOfStoneAnimation.GUARDIAN_ATTACK.bake(root);
        this.panicAnimation = GuardianOfStoneAnimation.GUARDIAN_PANIC_RUN.bake(root);

        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");

        actionAnimations.put(GuardianAction.ENCHANT, GuardianOfStoneAnimation.GUARDIAN_ENCHANT.bake(root));
        actionAnimations.put(GuardianAction.SMELT,   GuardianOfStoneAnimation.GUARDIAN_SMELT.bake(root));
        actionAnimations.put(GuardianAction.REPAIR,  GuardianOfStoneAnimation.GUARDIAN_REPAIR.bake(root));
        actionAnimations.put(GuardianAction.POLISH,  GuardianOfStoneAnimation.GUARDIAN_POLISH.bake(root));
        actionAnimations.put(GuardianAction.MIDAS,   GuardianOfStoneAnimation.GUARDIAN_MIDAS.bake(root));
        actionAnimations.put(GuardianAction.TRADE,   GuardianOfStoneAnimation.GUARDIAN_TRADE.bake(root));
    }

    public static LayerDefinition createBodyLayer() {
        return createBodyLayer(CubeDeformation.NONE);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation d) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F, d),
                PartPose.offset(-4.0F, -6.0F, 0.0F));

        partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(0.0F, -3.0F, -3.0F, 6.0F, 13.0F, 5.0F, d)
                        .texOffs(24, 0).addBox(-6.0F, -4.0F, -3.0F, 6.0F, 7.0F, 5.0F, d)
                        .texOffs(24, 43).addBox(-5.0F, 3.0F, -2.0F, 5.0F, 3.0F, 3.0F, d),
                PartPose.offset(-1.0F, -2.0F, 1.0F));

        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(34, 24).addBox(0.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F, d)
                        .texOffs(40, 43).addBox(0.0F, 15.0F, -1.5F, 3.0F, 4.0F, 3.0F, d),
                PartPose.offset(5.0F, -4.0F, 0.5F));

        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(22, 16).addBox(-2.0F, -1.5F, -1.5F, 3.0F, 21.0F, 3.0F, d)
                        .texOffs(34, 12).addBox(-3.0F, 11.5F, -2.5F, 5.0F, 7.0F, 5.0F, d),
                PartPose.offset(-8.0F, -4.5F, 1.5F));

        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(12, 40).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F, d),
                PartPose.offset(1.5F, 8.0F, 0.5F));

        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 34).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 19.0F, 3.0F, d),
                PartPose.offset(-1.0F, 6.5F, 0.5F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull GuardianOfStoneRenderState state) {
        super.setupAnim(state);

        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;

        if (state.guardianOfStoneState.equals(GuardianOfStoneState.PANIC)) {
            this.panicAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1.0F, 3.0F);
        } else {
            float walkScale = state.attackAnimationState.isStarted() ? 0.3F : 1.5F;
            this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1.0F, walkScale);
        }

        this.attackAnimation.apply(state.attackAnimationState, state.ageInTicks);

        KeyframeAnimation actionAnim = actionAnimations.get(state.action);
        if (actionAnim != null) actionAnim.apply(state.actionAnimationState, state.ageInTicks);


        if (state.pointing) {
            this.rightArm.xRot = state.pointXRot;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = state.pointZRot;
        }

        if (state.shakingMoss) {
            applyShake(state.ageInTicks, MOSS_SHAKE_INTENSITY);
        } else if (state.terrified) {
            applyShake(state.ageInTicks, FEAR_INTENSITY);
        }
    }

    private void applyShake(float t, float k) {
        float a = Mth.sin(t * 3.7F) + 0.5F * Mth.sin(t * 6.1F + 1.3F);
        float b = Mth.sin(t * 4.3F + 2.1F) + 0.5F * Mth.sin(t * 7.3F);

        this.root.yRot += a * 0.03F * k;
        this.root.zRot += b * 0.02F * k;

        shiver(this.head, t, 0.0F, 0.6F * k, 0.06F * k);
        shiver(this.body, t, 1.1F, 0.5F * k, 0.04F * k);
        shiver(this.leftArm, t, 2.2F, 0.8F * k, 0.10F * k);
        shiver(this.rightArm, t, 3.3F, 0.8F * k, 0.10F * k);
        shiver(this.leftLeg, t, 4.4F, 0.5F * k, 0.06F * k);
        shiver(this.rightLeg, t, 5.5F, 0.5F * k, 0.06F * k);
    }

    private static void shiver(ModelPart part, float t, float phase, float posAmp, float rotAmp) {
        float s1 = Mth.sin(t * 3.9F + phase);
        float s2 = Mth.sin(t * 5.7F + phase * 1.7F);
        part.x += s1 * posAmp;
        part.z += s2 * posAmp * 0.6F;
        part.xRot += s2 * rotAmp;
        part.zRot += s1 * rotAmp;
    }

    public void translateToRightHand(PoseStack poseStack) {
        this.rightArm.translateAndRotate(poseStack);
    }

    public void translateToLeftHand(PoseStack poseStack) {
        this.leftArm.translateAndRotate(poseStack);
    }
}