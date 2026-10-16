package guardian_of_stone.code.world.entity.ai.goal;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class GuardianUseAbilityGoal extends Goal {
    private final GuardianOfStoneEntity guardian;

    public GuardianUseAbilityGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override public boolean canUse() { return guardian.getAction() != GuardianAction.NONE; }
    @Override public boolean canContinueToUse() { return canUse(); }
    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public void start() { guardian.getNavigation().stop(); }

    @Override
    public void tick() {
        Player player = guardian.getActionPlayer();
        if (player != null) guardian.getLookControl().setLookAt(player, 30.0F, 30.0F);
    }
}