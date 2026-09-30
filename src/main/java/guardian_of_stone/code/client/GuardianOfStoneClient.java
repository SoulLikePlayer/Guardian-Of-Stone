package guardian_of_stone.code.client;

import guardian_of_stone.code.client.model.GuardianOfStoneModel;
import guardian_of_stone.code.client.renderer.GuardianOfStoneRenderer;
import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.ModEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = GuardianOfStone.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GuardianOfStone.MODID, value = Dist.CLIENT)
public class GuardianOfStoneClient {
    public static final ModelLayerLocation GUARDIAN_OF_STONE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "guardian_of_stone"),
            "main"
    );

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(GUARDIAN_OF_STONE, GuardianOfStoneModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ModEntity.GUARDIAN_OF_STONE.get(), GuardianOfStoneRenderer::new);
    }
}
