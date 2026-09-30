package guardian_of_stone.code.client;

import guardian_of_stone.code.core.GuardianOfStone;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = GuardianOfStone.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GuardianOfStone.MODID, value = Dist.CLIENT)
public class GuardianOfStoneClient {
    public GuardianOfStoneClient(ModContainer container) {
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }
}
