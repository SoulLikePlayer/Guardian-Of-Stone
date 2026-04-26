package net.guardian_of_stone.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.guardian_of_stone.client.GuardianOfStoneClient;
import net.guardian_of_stone.client.model.entity.GuardianOfStoneModel;
import net.guardian_of_stone.client.renderer.entity.state.GuardianOfStoneEntityRenderState;
import net.guardian_of_stone.core.GuardianOfStone;
import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class GuardianOfStoneEntityRenderer<T extends GuardianOfStoneEntity>
        extends MobRenderer<@NotNull T, @NotNull GuardianOfStoneEntityRenderState, @NotNull GuardianOfStoneModel> {

    private static final Identifier TEXTURE_ACTIVE =
            Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/guardian_of_stone_active.png");

    private static final Identifier TEXTURE_INACTIVE =
            Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/guardian_of_stone_inactive.png");

    public GuardianOfStoneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GuardianOfStoneModel(context.bakeLayer(GuardianOfStoneClient.GUARDIAN_OF_STONE_LAYER)), 0.6F);
    }

    @Override
    public void extractRenderState(@NotNull T entity,
                                   @NotNull GuardianOfStoneEntityRenderState state,
                                   float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.canMove = entity.isActive();
        state.attackAnimationState.copyFrom(entity.attackAnimationState);
        state.deathAnimationState.copyFrom(entity.deathAnimationState);

        state.deathTime = 0.0F;
        state.hurtTime = 0;
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull GuardianOfStoneEntityRenderState state) {
        return state.canMove ? TEXTURE_ACTIVE : TEXTURE_INACTIVE;
    }

    @Override
    public @NotNull GuardianOfStoneEntityRenderState createRenderState() {
        return new GuardianOfStoneEntityRenderState();
    }
}