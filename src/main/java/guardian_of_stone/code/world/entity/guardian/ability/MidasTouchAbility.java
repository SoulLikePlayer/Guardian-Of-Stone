package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MidasTouchAbility implements IGuardianAbility{
    private static final HashMap<Item, Item> GOLDEN_ITEM_MAP = new HashMap<>();

    static {
        GOLDEN_ITEM_MAP.put(Items.CARROT, Items.GOLDEN_CARROT);
        GOLDEN_ITEM_MAP.put(Items.APPLE, Items.GOLDEN_APPLE);
    }

    @Override
    public boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        if(held.isEmpty() || !GOLDEN_ITEM_MAP.containsKey(held.getItem())) return false;

        Holder<@NotNull Item> result = GOLDEN_ITEM_MAP.get(held.getItem()).builtInRegistryHolder();

        ItemStack polished = new ItemStack(result, held.getCount());
        if (!player.hasInfiniteMaterials()) held.setCount(0);

        if (!player.getInventory().add(polished)) player.addItem(polished);

        return true;
    }
}
