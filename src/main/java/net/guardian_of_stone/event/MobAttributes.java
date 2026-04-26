package net.guardian_of_stone.event;

import net.guardian_of_stone.core.GuardianOfStone;
import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.guardian_of_stone.world.entitites.ModEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = GuardianOfStone.MODID)
public class MobAttributes {

    @SubscribeEvent
    public static void createDefaultAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.GUARDIAN_OF_STONE.get(), GuardianOfStoneEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.GUARDIAN_OF_STONE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GuardianOfStoneEntity::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR
        );
    }
}
