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

public class OverloadAbility implements IGuardianAbility {
    private static final double RANGE = 16.0;
    private static final int CHECK_INTERVAL = 40;
    private static final int HAST_DURATION = 60;

    @Override
    public boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        return false;
    }

    @Override
    public void tick(GuardianOfStoneEntity guardian, ServerLevel level) {
        if(guardian.tickCount % CHECK_INTERVAL != 0) return;


        AABB box = guardian.getBoundingBox().inflate(RANGE);
        var player = level.getEntitiesOfClass(LivingEntity.class, box, e -> e instanceof Player);
        if (player.isEmpty()) return;

        for (LivingEntity mob : player) {
            mob.addEffect(new MobEffectInstance(MobEffects.HASTE, HAST_DURATION, 0, false, false));
        }
    }
}
