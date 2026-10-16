package guardian_of_stone.code.world.entity.guardian;

import org.jetbrains.annotations.Nullable;

public enum GuardianOfStoneMossLevel {
    NONE(0, null),
    SMALL(2_400, "small_moss"),
    MEDIUM(6_000, "medium_moss"),
    LARGE(12_000, "large_moss");

    private static final GuardianOfStoneMossLevel[] VALUES = values();

    private final int thresholdTicks;
    private final @Nullable String texture;

    GuardianOfStoneMossLevel(int thresholdTicks, @Nullable String texture) {
        this.thresholdTicks = thresholdTicks;
        this.texture = texture;
    }

    public int getThresholdTicks() { return thresholdTicks; }

    public @Nullable String getTexture() { return texture; }

    public static GuardianOfStoneMossLevel fromTicks(int ticks) {
        GuardianOfStoneMossLevel result = NONE;
        for (GuardianOfStoneMossLevel level : VALUES) {
            if (ticks >= level.thresholdTicks) result = level;
        }
        return result;
    }

    public static GuardianOfStoneMossLevel byId(int id) {
        return VALUES[Math.max(0, Math.min(id, VALUES.length - 1))];
    }
}