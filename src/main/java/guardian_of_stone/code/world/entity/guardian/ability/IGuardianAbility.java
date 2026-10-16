package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IGuardianAbility {
    default GuardianAction getAction() { return GuardianAction.NONE; }

    default boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) { return false; }

    default int getInputCount(ItemStack held) { return 1; }

    default void onActionTick(GuardianOfStoneEntity guardian, ServerLevel level, int tick) {}

    default ItemStack finish(GuardianOfStoneEntity guardian, ServerLevel level, ItemStack input) { return input; }

    default void tick(GuardianOfStoneEntity guardian, ServerLevel level) {}
    default void onHurt(GuardianOfStoneEntity guardian, ServerLevel level, DamageSource source, float amount) {}
    default void onAttack(GuardianOfStoneEntity guardian, ServerLevel level, Entity target) {}
    default void onStateChange(GuardianOfStoneEntity guardian, GuardianOfStoneState from, GuardianOfStoneState to) {}

    IGuardianAbility NONE = new IGuardianAbility() {};
}