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
    COAL("coal", new SmeltAbility()),
    COPPER("copper", new PolishAbility()),
    IRON("iron", IGuardianAbility.NONE),
    REDSTONE("redstone", IGuardianAbility.NONE),
    LAPIS("lapis", new EnchanterAbility()),
    GOLD("gold", IGuardianAbility.NONE),
    DIAMOND("diamond", IGuardianAbility.NONE),
    AMETHYST("amethyst", new ResonanceAbility()),
    EMERALD("emerald", IGuardianAbility.NONE),
    NONE("none", IGuardianAbility.NONE);

    public static final Codec<GuardianOfStoneType> CODEC = StringRepresentable.fromEnum(GuardianOfStoneType::values);

    public static final IntFunction<GuardianOfStoneType> BY_ID =
            ByIdMap.continuous(GuardianOfStoneType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<@NotNull ByteBuf, @NotNull GuardianOfStoneType> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, GuardianOfStoneType::ordinal);

    private final String typeName;
    private final IGuardianAbility ability;

    GuardianOfStoneType(String typeName, IGuardianAbility guardianAbility) {
        this.typeName = typeName;
        this.ability = guardianAbility;
    }

    public static GuardianOfStoneType getRandomized(RandomSource randomSource){
        return values()[randomSource.nextInt(values().length)];    }

    @Override
    public @NotNull String getSerializedName() {
        return this.typeName;
    }

    public IGuardianAbility getAbility() {
        return ability;
    }
}

