package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.WeakHashMap;

public class OverloadAbility implements IGuardianAbility {
    private static final double RANGE = 16.0;
    private static final int CHECK_INTERVAL = 40;
    private static final int HASTE_DURATION = 60;

    public static final int CHARGE_RANGE_XZ = 6;
    public static final int CHARGE_RANGE_Y = 3;

    private static final Map<GuardianOfStoneEntity, BlockPos> ACTIVE = new WeakHashMap<>();

    @Override
    public void tick(GuardianOfStoneEntity guardian, ServerLevel level) {
        BlockPos pos = guardian.blockPosition();
        BlockPos last = ACTIVE.put(guardian, pos);
        if (!pos.equals(last)) {
            if (last != null) refreshArea(level, last);
            refreshArea(level, pos);
        }

        if (guardian.tickCount % CHECK_INTERVAL == 0) {
            applyHaste(guardian, level);
        }
    }

    public static void onGuardianRemoved(GuardianOfStoneEntity guardian) {
        BlockPos last = ACTIVE.remove(guardian);
        if (last != null && guardian.level() instanceof ServerLevel level) {
            refreshArea(level, last);
        }
    }

    public static boolean isOverloaded(Level level, BlockPos pos) {
        if (ACTIVE.isEmpty()) return false;

        for (Map.Entry<GuardianOfStoneEntity, BlockPos> entry : ACTIVE.entrySet()) {
            GuardianOfStoneEntity guardian = entry.getKey();
            if (guardian.isRemoved() || guardian.level() != level) continue;

            BlockPos c = entry.getValue();
            if (Math.abs(pos.getX() - c.getX()) <= CHARGE_RANGE_XZ
                    && Math.abs(pos.getY() - c.getY()) <= CHARGE_RANGE_Y
                    && Math.abs(pos.getZ() - c.getZ()) <= CHARGE_RANGE_XZ) {
                return true;
            }
        }
        return false;
    }

    private static void refreshArea(ServerLevel level, BlockPos center) {
        BlockPos min = center.offset(-CHARGE_RANGE_XZ, -CHARGE_RANGE_Y, -CHARGE_RANGE_XZ);
        BlockPos max = center.offset(CHARGE_RANGE_XZ, CHARGE_RANGE_Y, CHARGE_RANGE_XZ);

        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            if (!level.hasChunkAt(p)) continue;
            if (level.getBlockState(p).is(Blocks.REDSTONE_WIRE)) {
                level.updateNeighborsAt(p.below(), Blocks.AIR);
            }
        }
    }

    private void applyHaste(GuardianOfStoneEntity guardian, ServerLevel level) {
        AABB box = guardian.getBoundingBox().inflate(RANGE);
        var players = level.getEntitiesOfClass(LivingEntity.class, box, e -> e instanceof Player);
        for (LivingEntity player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.HASTE, HASTE_DURATION, 0, false, false));
        }
    }
}