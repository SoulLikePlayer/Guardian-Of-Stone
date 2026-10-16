package guardian_of_stone.code.world.entity.ai.goal;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneMossLevel;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.pathfinder.Path;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class GuardianShakeOffMossGoal extends Goal {
    private static final int SHAKE_DURATION_TICKS = 50;
    private static final int SEARCH_COOLDOWN_TICKS = 100;
    private static final int SEARCH_ATTEMPTS = 16;

    private final GuardianOfStoneEntity guardian;
    private @Nullable BlockPos exit;
    private int searchCooldown;
    private int shakeTicks;
    private boolean shaking;

    public GuardianShakeOffMossGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean basicConditions() {
        return guardian.getMossLevel() == GuardianOfStoneMossLevel.LARGE
                && guardian.getState() == GuardianOfStoneState.AWAKE
                && guardian.getAction() == GuardianAction.NONE
                && !guardian.isTerrified()
                && guardian.getTarget() == null;
    }

    @Override
    public boolean canUse() {
        if (!basicConditions()) return false;
        if (!guardian.isInLushCave()) {
            exit = null;
            return true;
        }
        if (searchCooldown > 0) {
            searchCooldown--;
            return false;
        }
        exit = findExit();
        if (exit == null) {
            searchCooldown = SEARCH_COOLDOWN_TICKS;
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!basicConditions()) return false;
        return shaking || exit != null;
    }

    @Override
    public void start() {
        shaking = false;
        shakeTicks = 0;
        if (exit != null) moveToExit();
        else startShaking();
    }

    @Override
    public void tick() {
        if (shaking) {
            shakeTicks++;
            if (shakeTicks >= SHAKE_DURATION_TICKS) {
                guardian.shakeOffMoss();
                shaking = false;
                exit = null;
            }
            return;
        }

        if (!guardian.isInLushCave()) {
            startShaking();
        } else if (guardian.getNavigation().isDone()) {
            exit = findExit();
            if (exit != null) moveToExit();
            else searchCooldown = SEARCH_COOLDOWN_TICKS;
        }
    }

    @Override
    public void stop() {
        guardian.setShakingMoss(false);
        guardian.getNavigation().stop();
        shaking = false;
        exit = null;
    }

    private void startShaking() {
        guardian.getNavigation().stop();
        guardian.setShakingMoss(true);
        shaking = true;
        shakeTicks = 0;
    }

    private void moveToExit() {
        if (exit != null) guardian.getNavigation().moveTo(exit.getX() + 0.5D, exit.getY(), exit.getZ() + 0.5D, 1.1D);
    }

    private @Nullable BlockPos findExit() {
        BlockPos origin = guardian.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        for (int i = 0; i < SEARCH_ATTEMPTS; i++) {
            BlockPos c = origin.offset(
                    guardian.getRandom().nextInt(33) - 16,
                    guardian.getRandom().nextInt(13) - 6,
                    guardian.getRandom().nextInt(33) - 16);
            if (guardian.level().getBiome(c).is(Biomes.LUSH_CAVES)) continue;

            Path path = guardian.getNavigation().createPath(c, 0);
            if (path == null || !path.canReach()) continue;

            double dist = c.distSqr(origin);
            if (dist < bestDist) { bestDist = dist; best = c; }
        }
        return best;
    }
}