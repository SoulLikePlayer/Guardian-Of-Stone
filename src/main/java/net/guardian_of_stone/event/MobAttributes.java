package net.guardian_of_stone.event;

import net.guardian_of_stone.core.GuardianOfStone;
import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.guardian_of_stone.world.entitites.ModEntities;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = GuardianOfStone.MODID)
public class MobAttributes {

    @SubscribeEvent
    public static void createDefaultAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.GUARDIAN_OF_STONE.get(), GuardianOfStoneEntity.createAttributes().build());
    }
}
