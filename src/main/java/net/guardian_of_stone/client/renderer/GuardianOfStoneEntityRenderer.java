package net.guardian_of_stone.client.renderer;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.creaking.CreakingModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.creaking.Creaking;
import org.jetbrains.annotations.NotNull;

public class GuardianOfStoneEntityRenderer<T extends GuardianOfStoneEntity> extends MobRenderer<@NotNull T, @NotNull CreakingRenderState, @NotNull CreakingModel> {
    private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/creaking/creaking.png");
    private static final Identifier EYES_TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/creaking/creaking_eyes.png");

    public GuardianOfStoneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new CreakingModel(context.bakeLayer(ModelLayers.CREAKING)), 0.6F);
    }

    public Identifier getTextureLocation(CreakingRenderState state) {
        return TEXTURE_LOCATION;
    }

    public CreakingRenderState createRenderState() {
        return new CreakingRenderState();
    }
}
