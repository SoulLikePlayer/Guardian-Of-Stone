package guardian_of_stone.code.world.entity.guardian;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum GuardianOfStoneType implements StringRepresentable {
    COAL("coal"),
    COPPER("copper"),
    IRON("iron"),
    REDSTONE("redstone"),
    LAPIS("lapis"),
    GOLD("gold"),
    DIAMOND("diamond"),
    AMETHYST("amethyst"),
    EMERALD("emerald");

    public static final Codec<GuardianOfStoneType> CODEC = StringRepresentable.fromEnum(GuardianOfStoneType::values);

    public static final IntFunction<GuardianOfStoneType> BY_ID =
            ByIdMap.continuous(GuardianOfStoneType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<@NotNull ByteBuf, @NotNull GuardianOfStoneType> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, GuardianOfStoneType::ordinal);

    private final String typeName;

    GuardianOfStoneType(String stateName) {
        this.typeName = stateName;
    }

    public static GuardianOfStoneType getRandomized(RandomSource randomSource){
        return values()[randomSource.nextInt(values().length)];    }

    @Override
    public @NotNull String getSerializedName() {
        return this.typeName;
    }
}

