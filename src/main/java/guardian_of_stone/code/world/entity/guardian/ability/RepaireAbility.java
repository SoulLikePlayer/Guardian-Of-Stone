package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RepaireAbility implements IGuardianAbility {
    private static final float REPAIR_RATIO = 0.25f;

    @Override
    public boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        if (held.isEmpty() || !held.isDamaged()) return false;

        int repair = Math.max(1, (int) (held.getMaxDamage() * REPAIR_RATIO));
        held.setDamageValue(Math.max(0, held.getDamageValue() - repair));

        guardian.playSound(SoundEvents.ANVIL_USE, 0.6F, 1.2F);
        return true;
    }
}
