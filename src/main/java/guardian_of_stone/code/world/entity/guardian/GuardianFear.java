package guardian_of_stone.code.world.entity.guardian;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class GuardianFear {
    private GuardianFear() {}

    public static boolean isSculk(BlockState state) {
        return state.is(Blocks.SCULK) || state.is(Blocks.SCULK_VEIN)
                || state.is(Blocks.SCULK_CATALYST) || state.is(Blocks.SCULK_SENSOR)
                || state.is(Blocks.CALIBRATED_SCULK_SENSOR) || state.is(Blocks.SCULK_SHRIEKER);
    }

    public static @Nullable BlockPos findNearestSculk(Level level, BlockPos center, int horizontal, int vertical) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-horizontal, -vertical, -horizontal),
                center.offset(horizontal, vertical, horizontal))) {
            if (!level.hasChunkAt(pos) || !isSculk(level.getBlockState(pos))) continue;
            double d = pos.distSqr(center);
            if (d < bestDist) {
                bestDist = d;
                best = pos.immutable();
            }
        }
        return best;
    }

    public static boolean isNearSculk(Level level, BlockPos pos, int radius) {
        return findNearestSculk(level, pos, radius, radius) != null;
    }

    public static boolean fleeFrom(GuardianOfStoneEntity guardian, Vec3 danger, int distance, double speed) {
        if (!GuardianOfStoneState.PANIC.equals(guardian.getState())) return false;

        for (int i = 0; i < 8; i++) {
            Vec3 dest = DefaultRandomPos.getPosAway(guardian, distance, 7, danger);
            if (dest == null) continue;
            if (isNearSculk(guardian.level(), BlockPos.containing(dest), 3)) continue;
            if (guardian.getNavigation().moveTo(dest.x, dest.y, dest.z, speed)) return true;
        }
        return false;
    }
}