package guardian_of_stone.code.world.entity;

import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModEntity {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(GuardianOfStone.MODID);

    public static final Supplier<EntityType<@NotNull GuardianOfStoneEntity>> GUARDIAN_OF_STONE =
            ENTITY_TYPES.registerEntityType("guardian_of_stone", GuardianOfStoneEntity::new, MobCategory.CREATURE);
}
