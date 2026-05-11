package net.guardian_of_stone.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.guardian_of_stone.client.model.entity.GuardianOfStoneModel;
import net.guardian_of_stone.client.renderer.entity.state.GuardianOfStoneEntityRenderState;
import net.guardian_of_stone.world.entitites.OreVariant;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class OreOverlayLayer extends RenderLayer<@NotNull GuardianOfStoneEntityRenderState, @NotNull GuardianOfStoneModel> {

    public OreOverlayLayer(RenderLayerParent<@NotNull GuardianOfStoneEntityRenderState, @NotNull GuardianOfStoneModel> parent) {
        super(parent);
    }

    @Override
    public void submit(@NotNull PoseStack poseStack,
                       @NotNull SubmitNodeCollector collector,
                       int packedLight,
                       @NotNull GuardianOfStoneEntityRenderState state,
                       float yRot,
                       float xRot) {

        OreVariant variant = state.oreVariant;
        Identifier texture = variant.getOverlayTexture();

        if (texture == null) return;

        collector.order(1)
                .submitModel(
                        this.getParentModel(),
                        state,
                        poseStack,
                        texture,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        0,
                        null
                );
    }
}