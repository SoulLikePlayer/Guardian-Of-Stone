package net.guardian_of_stone.world.entitites;

import net.guardian_of_stone.core.GuardianOfStone;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(GuardianOfStone.MODID);

    public static final Supplier<EntityType<@NotNull GuardianOfStoneEntity>> GUARDIAN_OF_STONE = ENTITY_TYPES.register(
            "guardian_of_stone",
                    () -> EntityType.Builder.of(
                            GuardianOfStoneEntity::new,
                            MobCategory.MONSTER
                    )
                    .build(ResourceKey.create(
                    Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "guardian_of_stone")
            ))
    );
}
