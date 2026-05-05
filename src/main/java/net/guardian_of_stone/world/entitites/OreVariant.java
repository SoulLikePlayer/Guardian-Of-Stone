package net.guardian_of_stone.world.entitites;

import net.guardian_of_stone.core.GuardianOfStone;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public enum OreVariant {
    NONE    (null),
    COAL    ("coals"),
    COPPER  ("coppers"),
    DIAMOND ("diamond"),
    EMERALD ("emerald"),
    GOLD    ("gold"),
    REDSTONE("redstone");

    @Nullable
    private final Identifier overlayTexture;

    OreVariant(@Nullable String suffix) {
        this.overlayTexture = suffix == null ? null :
                Identifier.fromNamespaceAndPath(
                        GuardianOfStone.MODID,
                        "textures/entity/guardian_of_stone/layers/guardian_of_stone_outlayers_" + suffix + ".png"
                );
    }

    @Nullable
    public Identifier getOverlayTexture() {
        return overlayTexture;
    }

    public byte toId() {
        return (byte) this.ordinal();
    }

    public static OreVariant fromId(byte id) {
        OreVariant[] values = values();
        int i = id & 0xFF;
        return (i < values.length) ? values[i] : NONE;
    }

    private static final OreVariant[] WEIGHTED_POOL = buildPool();

    private static OreVariant[] buildPool() {
        return new OreVariant[]{
                NONE, NONE, NONE, NONE,          // 4× → ~40 %
                COAL,                             // 1× → ~10 %
                COPPER,                           // 1× → ~10 %
                GOLD,                             // 1× → ~10 %
                REDSTONE,                         // 1× → ~10 %
                DIAMOND,                          // 1× → ~10 %
                EMERALD,                          // 1× → ~10 %
        };
    }

    public static OreVariant random(net.minecraft.util.RandomSource rng) {
        return WEIGHTED_POOL[rng.nextInt(WEIGHTED_POOL.length)];
    }
}