package guardian_of_stone.code.world.entity.ai.goal;

import guardian_of_stone.code.world.entity.guardian.GuardianFear;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;

public class GuardianFleeWardenGoal extends Goal {
    private static final double DETECTION_RANGE = 32.0D; // > portée de détection du Warden (16)
    private static final double SPEED = 1.7D;            // plus rapide que le Warden
    private static final int LINGER_TICKS = 60;          // continue de fuir 3 s après l'avoir perdu de vue
    private static final int REPATH_INTERVAL = 10;

    private final GuardianOfStoneEntity guardian;
    private @Nullable Warden threat;
    private int lingerTicks;

    public GuardianFleeWardenGoal(GuardianOfStoneEntity guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        this.threat = findNearestWarden();
        return this.threat != null;
    }

    @Override
    public boolean canContinueToUse() {
        Warden w = findNearestWarden();
        if (w != null) {
            this.threat = w;
            this.lingerTicks = LINGER_TICKS;
            return true;
        }
        return --this.lingerTicks > 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void start() {
        this.lingerTicks = LINGER_TICKS;
        this.guardian.cancelAction();
        this.guardian.setTarget(null);
        this.guardian.setPointingPos(null);
        this.guardian.setState(GuardianOfStoneState.PANIC);
        this.guardian.setTerrified(true);
        flee();
    }

    @Override
    public void stop() {
        this.threat = null;
        this.guardian.getNavigation().stop();
        this.guardian.setTerrified(false);
        this.guardian.setState(GuardianOfStoneState.SLEEP);
    }

    @Override
    public void tick() {
        Warden w = this.threat;
        if (w != null && w.isAlive()) {
            hideFrom(w);
            this.guardian.getLookControl().setLookAt(w, 30.0F, 30.0F);
        }
        if (this.guardian.getNavigation().isDone() || this.guardian.tickCount % REPATH_INTERVAL == 0) {
            flee();
        }
    }

    private void flee() {
        Warden w = this.threat;
        if (w != null) GuardianFear.fleeFrom(this.guardian, w.position(), 16, SPEED);
    }

    private void hideFrom(Warden warden) {
        warden.clearAnger(this.guardian);
        warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                .filter(t -> t == this.guardian)
                .ifPresent(t -> warden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET));
    }

    private @Nullable Warden findNearestWarden() {
        return this.guardian.level()
                .getEntitiesOfClass(Warden.class,
                        this.guardian.getBoundingBox().inflate(DETECTION_RANGE),
                        Warden::isAlive)
                .stream()
                .min(Comparator.comparingDouble(this.guardian::distanceToSqr))
                .orElse(null);
    }
}