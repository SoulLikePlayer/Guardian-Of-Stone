package guardian_of_stone.code.event;

import guardian_of_stone.code.core.GuardianOfStone;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import static guardian_of_stone.code.world.entity.ModEntity.GUARDIAN_OF_STONE;

@EventBusSubscriber(modid = GuardianOfStone.MODID)
public class ModEntityAttributeCreationEvent {

    @SubscribeEvent
    public static void createDefaultAttribute(EntityAttributeCreationEvent event){
        event.put(
                GUARDIAN_OF_STONE.get(),
                IronGolem.createAttributes()
                        .build()
        );
    }
}
