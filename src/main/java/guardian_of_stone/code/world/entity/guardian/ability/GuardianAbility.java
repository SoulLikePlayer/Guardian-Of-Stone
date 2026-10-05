package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface GuardianAbility {
    boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held);

    GuardianAbility NONE = (g, l, p, h) -> false;
}