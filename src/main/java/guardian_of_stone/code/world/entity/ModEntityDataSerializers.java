package guardian_of_stone.code.world.entity;

import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ModEntityDataSerializers {
    public static final DeferredRegister<@NotNull EntityDataSerializer<?>> SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, GuardianOfStone.MODID);

    public static final EntityDataSerializer<@NotNull GuardianOfStoneState> STATE_SERIALIZER =
            EntityDataSerializer.forValueType(GuardianOfStoneState.STREAM_CODEC);

    static {
        SERIALIZERS.register("state", () -> STATE_SERIALIZER);
    }
}
