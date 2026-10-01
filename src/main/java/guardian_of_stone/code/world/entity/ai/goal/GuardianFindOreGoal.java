package guardian_of_stone.code.world.entity.ai.goal;

import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;

public class GuardianFindOreGoal extends Goal {
    private static final double SPEED = 1.0D;
    private static final double STOP_DISTANCE_SQR = 3.0D * 3.0D;
    private static final int REPATH_INTERVAL = 10;

    private final GuardianOfStoneEntity guardian;
    private final int radius;

    private @Nullable ArrayList<Block> pendingBlocks = null;
    private @Nullable ArrayList<Block> activeBlocks = null;
    private @Nullable BlockPos targetPos = null;

    public GuardianFindOreGoal(GuardianOfStoneEntity guardian, int radius) {
        this.guardian = guardian;
        this.radius = radius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (pendingBlocks == null) return false;

        ArrayList<Block> blocks = pendingBlocks;
        pendingBlocks = null;

        BlockPos found = findNearestOre(blocks);
        if (found == null) return false;

        this.activeBlocks = blocks;
        this.targetPos = found;
        GuardianOfStone.LOGGER.info("Block found in : {}", found);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        // Une nouvelle demande arrive -> on arrête pour relancer une recherche
        if (pendingBlocks != null) return false;
        return isTargetStillValid();
    }

    @Override
    public void start() {
        this.guardian.setState(GuardianOfStoneState.AWAKE);
        moveToTarget();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true; // pour que le regard soit fluide
    }

    @Override
    public void tick() {
        if (targetPos == null) return;

        // Regarde le minerai
        this.guardian.getLookControl().setLookAt(
                targetPos.getX() + 0.5D,
                targetPos.getY() + 0.5D,
                targetPos.getZ() + 0.5D
        );

        // Se déplace jusqu'à être proche, puis reste sur place
        double distSqr = this.guardian.distanceToSqr(
                targetPos.getX() + 0.5D, targetPos.getY() + 0.5D, targetPos.getZ() + 0.5D);

        if (distSqr <= STOP_DISTANCE_SQR) {
            this.guardian.getNavigation().stop();
        } else if (this.guardian.getNavigation().isDone()
                || this.guardian.tickCount % REPATH_INTERVAL == 0) {
            moveToTarget();
        }
    }

    @Override
    public void stop() {
        this.activeBlocks = null;
        this.targetPos = null;
        this.guardian.getNavigation().stop();
        this.guardian.setState(GuardianOfStoneState.SLEEP);
    }

    // ---------- utilitaires ----------

    private void moveToTarget() {
        if (targetPos == null) return;
        this.guardian.getNavigation().moveTo(
                targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, SPEED);
    }

    /** Vrai tant que le bloc ciblé est toujours un des minerais recherchés. */
    private boolean isTargetStillValid() {
        if (targetPos == null || activeBlocks == null) return false;

        Level level = this.guardian.level();
        if (!level.hasChunkAt(targetPos)) return false;

        for (Block block : activeBlocks) {
            if (level.getBlockState(targetPos).is(block)) return true;
        }
        return false; // le joueur l'a miné
    }

    /** withinManhattan parcourt les positions de la plus proche à la plus lointaine. */
    private @Nullable BlockPos findNearestOre(ArrayList<Block> blocks) {
        Level level = this.guardian.level();
        BlockPos origin = this.guardian.blockPosition();

        for (BlockPos pos : BlockPos.withinManhattan(origin, radius)) {
            if (!level.hasChunkAt(pos)) continue;

            for (Block block : blocks) {
                if (level.getBlockState(pos).is(block)) {
                    return pos.immutable();
                }
            }
        }
        return null;
    }

    public void setSearchedBlock(@Nullable ArrayList<Block> searchedBlock) {
        this.pendingBlocks = searchedBlock;
    }
}