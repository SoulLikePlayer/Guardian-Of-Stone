package net.gardian_of_stone.client;

import net.gardian_of_stone.core.GardianOfStone;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

@Mod(value = GardianOfStone.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GardianOfStone.MODID, value = Dist.CLIENT)
public class GardianOfStoneClient {
    public GardianOfStoneClient(ModContainer container) {}
}
