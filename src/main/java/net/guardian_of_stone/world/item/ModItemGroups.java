package net.guardian_of_stone.world.item;

import net.guardian_of_stone.core.GuardianOfStone;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModItemGroups {
    public static final DeferredRegister<@NotNull CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GuardianOfStone.MODID);

    public static final Supplier<@NotNull CreativeModeTab> GUARDIAN_OF_STONE_TAB =
            CREATIVE_MODE_TABS.register("guardian_of_stone_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.guardian_of_stone"))
                            .icon(() -> ModItems.GUARDIAN_OF_STONE_SPAWN_EGG.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.GUARDIAN_OF_STONE_SPAWN_EGG.get());
                            })
                            .build()
            );
}
