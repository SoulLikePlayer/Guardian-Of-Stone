package net.guardian_of_stone.client.renderer.entity;

import net.guardian_of_stone.client.GuardianOfStoneClient;
import net.guardian_of_stone.client.model.entity.GuardianOfStoneModel;
import net.guardian_of_stone.client.renderer.entity.layer.OreOverlayLayer;
import net.guardian_of_stone.client.renderer.entity.state.GuardianOfStoneEntityRenderState;
import net.guardian_of_stone.core.GuardianOfStone;
import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class GuardianOfStoneEntityRenderer<T extends GuardianOfStoneEntity>
        extends MobRenderer<@NotNull T, @NotNull GuardianOfStoneEntityRenderState, @NotNull GuardianOfStoneModel> {

    private static final Identifier TEXTURE_ACTIVE =
            Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/guardian_of_stone_active.png");

    private static final Identifier TEXTURE_INACTIVE =
            Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/guardian_of_stone_inactive.png");

    private float smoothArmYaw = 0.0F;
    private float smoothArmPitch = 0.0F;
    private float smoothHeadYaw = 0.0F;
    private float smoothHeadPitch = 0.0F;

    public GuardianOfStoneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GuardianOfStoneModel(context.bakeLayer(GuardianOfStoneClient.GUARDIAN_OF_STONE_LAYER)), 0.6F);
        this.addLayer(new OreOverlayLayer(this));
    }

    @Override
    public void extractRenderState(@NotNull T entity,
                                   @NotNull GuardianOfStoneEntityRenderState state,
                                   float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.canMove = entity.isActive();
        state.attackAnimationState.copyFrom(entity.attackAnimationState);
        state.deathAnimationState.copyFrom(entity.deathAnimationState);
        state.pointAnimationState.copyFrom(entity.pointAnimationState);
        state.groundSlamAnimationState.copyFrom(entity.groundSlamAnimationState);
        state.panicAnimationState.copyFrom(entity.panicAnimationState);

        state.deathTime = 0.0F;
        state.hurtTime  = 0;

        state.isPointing  = entity.isPointing();
        state.isScouting  = entity.isScouting();
        state.isPanicking = entity.isPanicking();

        state.oreVariant = entity.getOreVariant();

        if (entity.isPointing()) {
            BlockPos target = entity.getPointingTarget();

            final double SHOULDER_Y_OFFSET = 0.28125;

            double dx = (target.getX() + 0.5) - entity.getX();
            double dy = (target.getY() + 0.5) - (entity.getY() + SHOULDER_Y_OFFSET);
            double dz = (target.getZ() + 0.5) - entity.getZ();

            double hDist = Math.sqrt(dx * dx + dz * dz);

            float worldYaw  = (float) Math.atan2(-dx, dz);
            float entityYaw = entity.yBodyRot * ((float) Math.PI / 180F);
            float rawYaw    = worldYaw - entityYaw;

            while (rawYaw >  Math.PI) rawYaw -= (float)(2 * Math.PI);
            while (rawYaw < -Math.PI) rawYaw += (float)(2 * Math.PI);

            float rawPitch = (float) -Math.atan2(dy, hDist);

            state.pointingYaw   = rawYaw;
            state.pointingPitch = rawPitch;

            float lerpArm  = 0.18F;
            this.smoothArmYaw   = lerpAngle(this.smoothArmYaw,   rawYaw,   lerpArm);
            this.smoothArmPitch = lerpAngle(this.smoothArmPitch, rawPitch, lerpArm);

            float lerpHead = 0.12F;
            this.smoothHeadYaw   = lerpAngle(this.smoothHeadYaw,   rawYaw   * 0.5F, lerpHead);
            this.smoothHeadPitch = lerpAngle(this.smoothHeadPitch, rawPitch * 0.4F, lerpHead);
        } else {
            state.pointingYaw   = 0.0F;
            state.pointingPitch = 0.0F;

            float lerpOut = 0.10F;
            this.smoothArmYaw   = lerpAngle(this.smoothArmYaw,   0.0F, lerpOut);
            this.smoothArmPitch = lerpAngle(this.smoothArmPitch, 0.0F, lerpOut);
            this.smoothHeadYaw  = lerpAngle(this.smoothHeadYaw,  0.0F, lerpOut);
            this.smoothHeadPitch = lerpAngle(this.smoothHeadPitch, 0.0F, lerpOut);
        }

        state.smoothArmYaw   = this.smoothArmYaw;
        state.smoothArmPitch = this.smoothArmPitch;
        state.smoothHeadYaw  = this.smoothHeadYaw;
        state.smoothHeadPitch = this.smoothHeadPitch;
    }

    /**
     * Linearly interpolates between two angles (in radians), always taking
     * the shortest path around the circle to avoid wrap-around snapping.
     */
    private static float lerpAngle(float current, float target, float factor) {
        float delta = target - current;
        while (delta >  Math.PI) delta -= (float)(2 * Math.PI);
        while (delta < -Math.PI) delta += (float)(2 * Math.PI);
        return current + delta * factor;
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