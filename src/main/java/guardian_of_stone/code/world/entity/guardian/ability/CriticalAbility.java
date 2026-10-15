package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CriticalAbility implements IGuardianAbility {
    private static final float CRIT_CHANCE = 0.20F;
    private static final float CRIT_MULTIPLIER = 1.0F;

    @Override
    public boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        return false;
    }

    @Override
    public void onAttack(GuardianOfStoneEntity guardian, ServerLevel level, Entity target) {
        if (!(target instanceof LivingEntity living)) return;
        if (guardian.getRandom().nextFloat() >= CRIT_CHANCE) return;

        float bonus = (float) guardian.getAttributeValue(Attributes.ATTACK_DAMAGE) * CRIT_MULTIPLIER;

        living.setInvulnerableTime(0);
        living.hurtServer(level, guardian.damageSources().mobAttack(guardian), bonus);

        level.sendParticles(ParticleTypes.CRIT,
                target.getX(), target.getY(0.5D), target.getZ(),
                15, 0.3D, 0.3D, 0.3D, 0.2D);
        level.playSound(null, target.blockPosition(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}