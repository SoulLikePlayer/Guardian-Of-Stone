package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class RepaireAbility implements IGuardianAbility {
    private static final float REPAIR_RATIO = 0.25f;

    @Override public GuardianAction getAction() { return GuardianAction.REPAIR; }

    @Override
    public boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        return !held.isEmpty() && held.isDamaged();
    }

    @Override
    public void onActionTick(GuardianOfStoneEntity guardian, ServerLevel level, int tick) {
        if (tick % 12 != 6) return;   // un "frottement" toutes les ~12 ticks
        Vec3 p = guardian.getWorkPos();
        level.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 5, 0.15, 0.15, 0.15, 0.1);
        guardian.playSound(SoundEvents.ANVIL_USE, 0.6F, 1.2F);
    }

    @Override
    public ItemStack finish(GuardianOfStoneEntity guardian, ServerLevel level, ItemStack input) {
        int repair = Math.max(1, (int) (input.getMaxDamage() * REPAIR_RATIO));
        input.setDamageValue(Math.max(0, input.getDamageValue() - repair));
        return input;
    }
}