package guardian_of_stone.code.world.item;

import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.ModEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuardianOfStone.MODID);

    public static final DeferredItem<@NotNull SpawnEggItem> GUARDIAN_OF_STONE_SPAWN_EGG =
            ITEMS.registerItem("guardian_of_stone_spawn_egg",
                    props -> new SpawnEggItem(props.spawnEgg(ModEntity.GUARDIAN_OF_STONE.get())));


    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(GUARDIAN_OF_STONE_SPAWN_EGG);
        }
    }
}