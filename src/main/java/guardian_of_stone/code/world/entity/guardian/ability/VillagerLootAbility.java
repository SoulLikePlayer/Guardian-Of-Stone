package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Supplier;

public class VillagerLootAbility implements IGuardianAbility {

    private static final List<Supplier<ItemStack>> POOL = List.of(
            () -> new ItemStack(Items.BREAD, 4),
            () -> new ItemStack(Items.PUMPKIN_PIE, 2),
            () -> new ItemStack(Items.APPLE, 4),
            () -> new ItemStack(Items.COOKIE, 6),
            () -> new ItemStack(Items.GOLDEN_CARROT, 3),
            () -> new ItemStack(Items.COOKED_PORKCHOP, 5),

            () -> new ItemStack(Items.FISHING_ROD),
            () -> new ItemStack(Items.ARROW, 16),
            () -> new ItemStack(Items.CROSSBOW),
            () -> new ItemStack(Items.SHEARS),
            () -> new ItemStack(Items.WOOL.white(), 4),

            () -> new ItemStack(Items.IRON_PICKAXE),
            () -> new ItemStack(Items.DIAMOND_AXE),
            () -> new ItemStack(Items.CHAINMAIL_CHESTPLATE),
            () -> new ItemStack(Items.BELL),

            () -> new ItemStack(Items.BRICK, 10),
            () -> new ItemStack(Items.TERRACOTTA, 4),
            () -> new ItemStack(Items.PAINTING, 2),
            () -> new ItemStack(Items.COMPASS),
            () -> new ItemStack(Items.NAME_TAG),
            () -> new ItemStack(Items.BOOKSHELF),
            // Clerc
            () -> new ItemStack(Items.ENDER_PEARL),
            () -> new ItemStack(Items.EXPERIENCE_BOTTLE),
            () -> new ItemStack(Items.REDSTONE, 2),
            () -> new ItemStack(Items.LAPIS_LAZULI, 2),
            () -> new ItemStack(Items.GLOWSTONE),
            () -> new ItemStack(Items.RABBIT_FOOT),

            () -> new ItemStack(Items.SADDLE),
            () -> new ItemStack(Items.LEATHER_HORSE_ARMOR),

            () -> new ItemStack(Items.STICK, 2),
            () -> new ItemStack(Items.FLINT, 3),
            () -> new ItemStack(Items.GLASS_BOTTLE)
    );

    @Override
    public boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        if (!held.is(Items.EMERALD)) return false;

        ItemStack reward = POOL.get(level.getRandom().nextInt(POOL.size())).get();

        if (!player.hasInfiniteMaterials()) held.shrink(1);
        if (!player.getInventory().add(reward)) player.addItem(reward);

        guardian.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);
        return true;
    }
}