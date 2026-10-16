package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class ResonanceAbility implements IGuardianAbility{
    private static final double RANGE = 16.0;
    private static final int CHECK_INTERVAL = 40;
    private static final int GLOW_DURATION = 60;

    @Override
    public void tick(GuardianOfStoneEntity guardian, ServerLevel level) {
        if(guardian.tickCount % CHECK_INTERVAL != 0) return;


        AABB box = guardian.getBoundingBox().inflate(RANGE);
        var hostiles = level.getEntitiesOfClass(LivingEntity.class, box, e -> e instanceof Enemy);
        if (hostiles.isEmpty()) return;

        for (LivingEntity mob : hostiles) {
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_DURATION, 0, false, false));
        }

        level.playSound(null, guardian.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}
