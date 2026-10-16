package guardian_of_stone.code.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import guardian_of_stone.code.client.model.GuardianOfStoneModel;
import guardian_of_stone.code.client.renderer.state.GuardianOfStoneRenderState;
import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GuardianOfStoneWorkItemLayer extends RenderLayer<@NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> {

    public GuardianOfStoneWorkItemLayer(RenderLayerParent<@NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> parent) {
        super(parent);
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, int lightCoords,
                       GuardianOfStoneRenderState state, float yRot, float xRot) {
        if (state.action == GuardianAction.NONE || state.workItem.isEmpty()) return;

        poseStack.pushPose();

        if (state.action == GuardianAction.ENCHANT) {
            float bob = Mth.sin(state.ageInTicks * 0.15F) * 0.06F;
            poseStack.translate(0.0F, 0.1F + bob, -0.8F);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.rotate(Axis.YP.rotationDegrees(state.ageInTicks * 4.0F));
            poseStack.scale(0.7F, 0.7F, 0.7F);
        } else {
            getParentModel().translateToRightHand(poseStack);
            poseStack.rotate(Axis.XP.rotationDegrees(-90.0F));
            poseStack.rotate(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate(1.0F / 16.0F, 2.0F / 16.0F, -17.0F / 16.0F);
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }

        state.workItem.submit(poseStack, collector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}