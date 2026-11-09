package guardian_of_stone.code.event;

import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.ModEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = GuardianOfStone.MODID)
public class ModSpawnPlacements {
    private static final int MAX_SPAWN_Y = 40;

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntity.GUARDIAN_OF_STONE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ModSpawnPlacements::checkGuardianSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }

    public static boolean checkGuardianSpawnRules(EntityType<@NotNull GuardianOfStoneEntity> type,
                                                  ServerLevelAccessor level,
                                                  EntitySpawnReason reason,
                                                  BlockPos pos,
                                                  RandomSource random) {
        return pos.getY() <= MAX_SPAWN_Y
                && !level.canSeeSky(pos)
                && !level.getBiome(pos).is(Biomes.DEEP_DARK);
    }
}