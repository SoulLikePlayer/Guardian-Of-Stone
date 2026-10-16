package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchanterAbility implements IGuardianAbility {
    private static final String ENCHANTED_KEY = "guardian_enchanted";

    @Override public GuardianAction getAction() { return GuardianAction.ENCHANT; }

    @Override
    public boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        return !held.isEmpty() && held.isEnchanted() && !hasBeenEnchanted(held);
    }

    @Override
    public void onActionTick(GuardianOfStoneEntity guardian, ServerLevel level, int tick) {
        if (tick == 1) guardian.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
        if (tick % 2 != 0) return;
        Vec3 p = guardian.getWorkPos();
        level.sendParticles(ParticleTypes.ENCHANT, p.x, p.y + 0.3, p.z, 3, 0.4, 0.4, 0.4, 0.5);
    }

    @Override
    public ItemStack finish(GuardianOfStoneEntity guardian, ServerLevel level, ItemStack input) {
        if (upgradeRandomEnchant(input, level.getRandom())) {
            markAsEnchanted(input);
            Vec3 p = guardian.getWorkPos();
            level.sendParticles(ParticleTypes.ENCHANTED_HIT, p.x, p.y, p.z, 20, 0.3, 0.3, 0.3, 0.3);
        }
        return input;
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