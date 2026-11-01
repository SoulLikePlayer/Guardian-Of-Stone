package guardian_of_stone.code.world.entity.guardian;

import com.mojang.serialization.Codec;
import guardian_of_stone.code.world.entity.guardian.ability.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum GuardianOfStoneType implements StringRepresentable {
    COAL("coal", new SmeltAbility(), 50),
    COPPER("copper", new PolishAbility(), 45),
    IRON("iron", new RepaireAbility(), 40),
    REDSTONE("redstone", new OverloadAbility(), 25),
    LAPIS("lapis", new EnchanterAbility(), 15),
    GOLD("gold", new MidasTouchAbility(), 15),
    AMETHYST("amethyst", new ResonanceAbility(), 12),
    DIAMOND("diamond", new CriticalAbility(), 8),
    EMERALD("emerald", new VillagerLootAbility(), 4),
    NONE("none", IGuardianAbility.NONE, 60);

    public static final Codec<GuardianOfStoneType> CODEC = StringRepresentable.fromEnum(GuardianOfStoneType::values);

    public static final IntFunction<GuardianOfStoneType> BY_ID =
            ByIdMap.continuous(GuardianOfStoneType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<@NotNull ByteBuf, @NotNull GuardianOfStoneType> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, GuardianOfStoneType::ordinal);

    private final String typeName;
    private final IGuardianAbility ability;
    private final int weight;

    private static final int TOTAL_WEIGHT;

    static {
        int total = 0;
        for(GuardianOfStoneType g : values()){
            total += g.weight;
        }

        TOTAL_WEIGHT = total;
    }

    GuardianOfStoneType(String typeName, IGuardianAbility guardianAbility, int weight) {
        this.typeName = typeName;
        this.ability = guardianAbility;
        this.weight = weight;
    }

    public static GuardianOfStoneType getRandomized(RandomSource randomSource){
        int draw = randomSource.nextInt(TOTAL_WEIGHT);
        int accumulation = 0;
        for (GuardianOfStoneType g : values()){
            accumulation += g.weight;
            if (draw < accumulation){
                return g;
            }
        }

        return GuardianOfStoneType.NONE;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.typeName;
    }

    public IGuardianAbility getAbility() {
        return ability;
    }
}

