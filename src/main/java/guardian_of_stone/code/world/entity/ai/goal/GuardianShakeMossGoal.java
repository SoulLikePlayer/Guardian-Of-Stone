package guardian_of_stone.code.world.entity.ai.goal;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneMossLevel;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class GuardianShakeMossGoal extends Goal {
    private static final GuardianOfStoneMossLevel MIN_LEVEL = GuardianOfStoneMossLevel.LARGE;
    private static final int SCAN_INTERVAL = 20;
    private static final float START_CHANCE = 0.25F;

    private static final int SHAKE_DURATION = 40;
    private static final int CARPET_INTERVAL = 4;
    private static final int ATTEMPTS_PER_BURST = 2;
    private static final int RADIUS = 3;

    private final GuardianOfStoneEntity guardian;
    private int shakeTicks;

    public GuardianShakeMossGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (guardian.tickCount % SCAN_INTERVAL != 0) return false;
        if (guardian.getMossLevel().ordinal() < MIN_LEVEL.ordinal()) return false;
        if (guardian.getAction() != GuardianAction.NONE) return false;
        if (guardian.getTarget() != null) return false;
        if (guardian.isTerrified() || guardian.getState() == GuardianOfStoneState.PANIC) return false;
        if (!guardian.onGround()) return false;
        return guardian.getRandom().nextFloat() < START_CHANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return shakeTicks < SHAKE_DURATION
                && guardian.getTarget() == null
                && guardian.getAction() == GuardianAction.NONE
                && !guardian.isTerrified()
                && guardian.getState() != GuardianOfStoneState.PANIC;
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void start() {
        shakeTicks = 0;
        guardian.getNavigation().stop();
        guardian.setPointingPos(null);
        guardian.setShakingMoss(true);
    }

    @Override
    public void stop() {
        shakeTicks = 0;
        guardian.setShakingMoss(false);
    }

    @Override
    public void tick() {
        shakeTicks++;
        guardian.getNavigation().stop();

        if (!(guardian.level() instanceof ServerLevel level)) return;

        if (shakeTicks % CARPET_INTERVAL == 0) {
            for (int i = 0; i < ATTEMPTS_PER_BURST; i++) tryPlaceCarpet(level);
            guardian.playSound(SoundEvents.MOSS_PLACE, 0.8F, 0.8F + guardian.getRandom().nextFloat() * 0.4F);
        }

        if (shakeTicks >= SHAKE_DURATION) guardian.shakeOffMoss();
    }

    private void tryPlaceCarpet(ServerLevel level) {
        BlockPos origin = guardian.blockPosition();
        int dx = guardian.getRandom().nextInt(-RADIUS, RADIUS + 1);
        int dz = guardian.getRandom().nextInt(-RADIUS, RADIUS + 1);
        BlockState carpet = Blocks.MOSS_CARPET.defaultBlockState();

        for (int dy = 1; dy >= -1; dy--) {
            BlockPos pos = origin.offset(dx, dy, dz);
            if (!level.getBlockState(pos).isAir()) continue;

            BlockPos below = pos.below();
            if (!level.getBlockState(below).isCollisionShapeFullBlock(level, below)) continue;

            level.setBlock(pos, carpet, 3);
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MOSS_BLOCK.defaultBlockState()),
                    pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, 4, 0.25D, 0.1D, 0.25D, 0.02D);
            return;
        }
    }
}