package net.guardian_of_stone.world.item;

import net.guardian_of_stone.core.GuardianOfStone;
import net.guardian_of_stone.world.entitites.ModEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuardianOfStone.MODID);

    public static final DeferredItem<@NotNull SpawnEggItem> GUARDIAN_OF_STONE_SPAWN_EGG = ITEMS.registerItem("guardian_of_stone_spawn_egg",
            properties -> new SpawnEggItem(
                    properties.spawnEgg(ModEntities.GUARDIAN_OF_STONE.get())
            ));
}
