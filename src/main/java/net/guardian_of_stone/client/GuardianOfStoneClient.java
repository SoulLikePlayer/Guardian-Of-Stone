package net.guardian_of_stone.client;

import net.guardian_of_stone.client.renderer.GuardianOfStoneEntityRenderer;
import net.guardian_of_stone.core.GuardianOfStone;
import net.guardian_of_stone.world.entitites.ModEntities;
import net.minecraft.client.renderer.entity.CreakingRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = GuardianOfStone.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GuardianOfStone.MODID, value = Dist.CLIENT)
public class GuardianOfStoneClient {
    public GuardianOfStoneClient(ModContainer container) {}

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ModEntities.GUARDIAN_OF_STONE.get(), GuardianOfStoneEntityRenderer::new);
    }
}
