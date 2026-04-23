package net.gardian_of_stone.core;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(GardianOfStone.MODID)
public class GardianOfStone {
    public static final String MODID = "gardian_of_stone";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GardianOfStone(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
