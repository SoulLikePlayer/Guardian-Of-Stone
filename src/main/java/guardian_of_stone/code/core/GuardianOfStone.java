package guardian_of_stone.code.core;

import guardian_of_stone.code.world.entity.ModEntity;
import guardian_of_stone.code.world.entity.ModEntityDataSerializers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(GuardianOfStone.MODID)
public class GuardianOfStone {
    public static final String MODID = "guardianofstone";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GuardianOfStone(IEventBus modEventBus, ModContainer modContainer) {
        ModEntity.ENTITY_TYPES.register(modEventBus);
        ModEntityDataSerializers.SERIALIZERS.register(modEventBus);
    }
}
