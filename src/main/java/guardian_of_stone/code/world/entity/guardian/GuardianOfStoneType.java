package guardian_of_stone.code.world.entity.guardian;

import com.mojang.serialization.Codec;
import guardian_of_stone.code.world.entity.guardian.ability.GuardianAbility;
import guardian_of_stone.code.world.entity.guardian.ability.PolishAbility;
import guardian_of_stone.code.world.entity.guardian.ability.SmeltAbility;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum GuardianOfStoneType implements StringRepresentable {
    COAL("coal", new SmeltAbility()),
    COPPER("copper", new PolishAbility()),
    IRON("iron", GuardianAbility.NONE),
    REDSTONE("redstone", GuardianAbility.NONE),
    LAPIS("lapis", GuardianAbility.NONE),
    GOLD("gold", GuardianAbility.NONE),
    DIAMOND("diamond", GuardianAbility.NONE),
    AMETHYST("amethyst", GuardianAbility.NONE),
    EMERALD("emerald", GuardianAbility.NONE),
    NONE("none", GuardianAbility.NONE);

    public static final Codec<GuardianOfStoneType> CODEC = StringRepresentable.fromEnum(GuardianOfStoneType::values);

    public static final IntFunction<GuardianOfStoneType> BY_ID =
            ByIdMap.continuous(GuardianOfStoneType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<@NotNull ByteBuf, @NotNull GuardianOfStoneType> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, GuardianOfStoneType::ordinal);

    private final String typeName;
    private final GuardianAbility ability;

    GuardianOfStoneType(String typeName, GuardianAbility guardianAbility) {
        this.typeName = typeName;
        this.ability = guardianAbility;
    }

    public static GuardianOfStoneType getRandomized(RandomSource randomSource){
        return values()[randomSource.nextInt(values().length)];    }

    @Override
    public @NotNull String getSerializedName() {
        return this.typeName;
    }

    public GuardianAbility getAbility() {
        return ability;
    }
}

