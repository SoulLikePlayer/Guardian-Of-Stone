package guardian_of_stone.code.world.entity.ai.goal;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianFear;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class GuardianAvoidSculkGoal extends Goal {
    private static final int TRIGGER_RADIUS = 4;
    private static final int RELEASE_RADIUS = 7;
    private static final int VERTICAL = 3;
    private static final int SCAN_INTERVAL = 10;

    private final GuardianOfStoneEntity guardian;
    private @Nullable BlockPos sculkPos;

    public GuardianAvoidSculkGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (guardian.getAction() != GuardianAction.NONE) return false;
        if (guardian.getState() == GuardianOfStoneState.PANIC) return false; // le Warden prime
        if (guardian.tickCount % SCAN_INTERVAL != 0) return false;
        sculkPos = GuardianFear.findNearestSculk(guardian.level(), guardian.blockPosition(), TRIGGER_RADIUS, VERTICAL);
        return sculkPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (guardian.getAction() != GuardianAction.NONE) return false;
        if (guardian.getState() == GuardianOfStoneState.PANIC) return false;
        if (guardian.tickCount % SCAN_INTERVAL == 0) {
            sculkPos = GuardianFear.findNearestSculk(guardian.level(), guardian.blockPosition(), RELEASE_RADIUS, VERTICAL);
        }
        return sculkPos != null;
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void start() {
        guardian.setTarget(null);
        guardian.setPointingPos(null);
        guardian.getNavigation().stop();
        guardian.setTerrified(true);
    }

    @Override
    public void stop() {
        sculkPos = null;
        if (guardian.getState() != GuardianOfStoneState.PANIC) {
            guardian.setTerrified(false);
        }
    }
}