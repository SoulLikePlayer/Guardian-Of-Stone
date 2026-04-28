package net.guardian_of_stone.core;

import net.guardian_of_stone.world.entitites.ModEntities;
import net.guardian_of_stone.world.item.ModItemGroups;
import net.guardian_of_stone.world.item.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(GuardianOfStone.MODID)
public class GuardianOfStone {
    public static final String MODID = "guardian_of_stone";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GuardianOfStone(IEventBus modEventBus, ModContainer modContainer) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItemGroups.CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}