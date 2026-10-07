package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchanterAbility implements GuardianAbility {
    private static final String ENCHANTED_KEY = "guardian_enchanted";

    @Override
    public boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        if (held.isEmpty() || !held.isEnchanted() || hasBeenEnchanted(held)) {
            return false;
        }

        if (upgradeRandomEnchant(held, level.getRandom())) {
            markAsEnchanted(held);
            return true;
        }

        guardian.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
        return false;
    }

    public static boolean hasBeenEnchanted(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains(ENCHANTED_KEY);
    }

    public static void markAsEnchanted(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(ENCHANTED_KEY, true));
    }

    public static boolean upgradeRandomEnchant(ItemStack stack, RandomSource random) {
        boolean[] changed = {false};

        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            List<Holder<@NotNull Enchantment>> list = new ArrayList<>(mutable.keySet());
            if (list.isEmpty()) return;

            Holder<@NotNull Enchantment> chosen = list.get(random.nextInt(list.size()));
            mutable.set(chosen, mutable.getLevel(chosen) + 1);
            changed[0] = true;
        });

        return changed[0];
    }
}