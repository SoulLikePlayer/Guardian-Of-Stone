package guardian_of_stone.code.world.entity.ai.goal;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GuardianMeleeAttackGoal extends MeleeAttackGoal {
    private final GuardianOfStoneEntity guardian;

    public GuardianMeleeAttackGoal(GuardianOfStoneEntity guardian, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(guardian, speedModifier, followingTargetEvenIfNotSeen);
        this.guardian = guardian;
    }

    @Override
    public void start() {
        if(this.guardian.getState().equals(GuardianOfStoneState.SLEEP)){
            this.guardian.setState(GuardianOfStoneState.AWAKE);
        }

        super.start();
    }

    @Override
    public void stop() {
        if(this.guardian.getState().equals(GuardianOfStoneState.AWAKE)){
            this.guardian.setState(GuardianOfStoneState.SLEEP);
        }

        super.stop();
    }
}
