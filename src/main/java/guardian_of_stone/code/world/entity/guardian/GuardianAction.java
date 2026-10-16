package guardian_of_stone.code.world.entity.guardian;

import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum GuardianAction {
    NONE(0),
    ENCHANT(70),
    SMELT(60),
    REPAIR(50),
    POLISH(40),
    MIDAS(50),
    TRADE(40);

    public static final IntFunction<GuardianAction> BY_ID =
            ByIdMap.continuous(GuardianAction::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int durationTicks;

    GuardianAction(int durationTicks) {
        this.durationTicks = durationTicks;
    }

    public int getDurationTicks() {
        return durationTicks;
    }
}