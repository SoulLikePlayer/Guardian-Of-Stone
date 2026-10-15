package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface GuardianAbility {
    boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held);

    default void tick(GuardianOfStoneEntity guardian, ServerLevel level) {}

    default void onHurt(GuardianOfStoneEntity guardian, ServerLevel level, DamageSource source, float amount) {}

    default void onAttack(GuardianOfStoneEntity guardian, ServerLevel level, Entity target) {}

    default void onStateChange(GuardianOfStoneEntity guardian, GuardianOfStoneState from, GuardianOfStoneState to) {}

    GuardianAbility NONE = (g, l, p, h) -> false;
}