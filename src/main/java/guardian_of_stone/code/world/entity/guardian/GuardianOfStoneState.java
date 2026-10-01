package guardian_of_stone.code.world.entity.guardian;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum GuardianOfStoneState implements StringRepresentable {
    SLEEP("sleep"),
    AWAKE("awake");

    public static final Codec<GuardianOfStoneState> CODEC = StringRepresentable.fromEnum(GuardianOfStoneState::values);

    public static final IntFunction<GuardianOfStoneState> BY_ID =
            ByIdMap.continuous(GuardianOfStoneState::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<@NotNull ByteBuf, @NotNull GuardianOfStoneState> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, GuardianOfStoneState::ordinal);

    private final String stateName;

    GuardianOfStoneState(String stateName){
        this.stateName = stateName;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.stateName;
    }


}
