package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchanterAbility implements GuardianAbility{
    @Override
    public boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        upgradeRandomEnchant(held, level.getRandom());

        return false;
    }

    public static void upgradeRandomEnchant(ItemStack stack, RandomSource random) {
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            List<Holder<@NotNull Enchantment>> list = new ArrayList<>(mutable.keySet());
            if (list.isEmpty()) return;

            Holder<@NotNull Enchantment> chosen = list.get(random.nextInt(list.size()));
            mutable.set(chosen, mutable.getLevel(chosen) + 1);
        });
    }
}
