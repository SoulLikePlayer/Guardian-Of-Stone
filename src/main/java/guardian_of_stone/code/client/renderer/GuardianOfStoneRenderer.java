package guardian_of_stone.code.client.renderer;

import guardian_of_stone.code.client.GuardianOfStoneClient;
import guardian_of_stone.code.client.model.GuardianOfStoneModel;
import guardian_of_stone.code.client.renderer.layers.GuardianOfStoneOuterLayer;
import guardian_of_stone.code.client.renderer.layers.GuardianOfStoneWorkItemLayer;
import guardian_of_stone.code.client.renderer.state.GuardianOfStoneRenderState;
import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class GuardianOfStoneRenderer <T extends GuardianOfStoneEntity> extends MobRenderer<@NotNull T, @NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> {
    private static final Identifier SLEEP_TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/sleep_guardian_of_stone.png");
    private static final Identifier AWAKE_TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/awake_guardian_of_stone.png");

    private final ItemModelResolver itemResolver;

    public GuardianOfStoneRenderer(EntityRendererProvider.Context context) {
        super(context, new GuardianOfStoneModel(context.bakeLayer(GuardianOfStoneClient.GUARDIAN_OF_STONE)), 1f);
        this.itemResolver = context.getItemModelResolver();
        this.addLayer(new GuardianOfStoneOuterLayer(this, context.getModelSet()));
        this.addLayer(new GuardianOfStoneWorkItemLayer(this));
    }

    public @NotNull Identifier getTextureLocation(GuardianOfStoneRenderState state) {
        return !state.guardianOfStoneState.equals(GuardianOfStoneState.SLEEP) ? AWAKE_TEXTURE_LOCATION : SLEEP_TEXTURE_LOCATION;
    }

    @Override
    protected boolean isShaking(@NotNull GuardianOfStoneRenderState state) {
        return super.isShaking(state) || state.terrified;
    }

    public GuardianOfStoneRenderState createRenderState() {
        return new GuardianOfStoneRenderState();
    }

    public void extractRenderState(T entity, GuardianOfStoneRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.attackAnimationState.copyFrom(entity.attackAnimationState);
        state.actionAnimationState.copyFrom(entity.actionAnimationState);
        state.action = entity.getAction();
        state.terrified = entity.isTerrified();
        state.guardianOfStoneState = entity.getState();
        state.guardianOfStoneType = entity.getGuardianType();

        this.itemResolver.updateForTopItem(state.workItem, entity.getWorkItem(),
                ItemDisplayContext.GROUND, entity.level(), entity, entity.getId());

        BlockPos p = entity.getPointingPos();
        state.pointing = p != null && state.action == GuardianAction.NONE;
        if (!state.pointing) return;

        double dx = p.getX() + 0.5D - state.x;
        double dy = p.getY() + 0.5D - state.y;
        double dz = p.getZ() + 0.5D - state.z;

        float yaw = state.bodyRot * Mth.DEG_TO_RAD;
        double fwd  = -dx * Mth.sin(yaw) + dz * Mth.cos(yaw);
        double left =  dx * Mth.cos(yaw) + dz * Mth.sin(yaw);
        double up   = dy;

        left += 0.5D;
        up -= 1.5D + 4.5D / 16.0D;
        fwd  += 1.5D / 16.0D;

        double len = Math.sqrt(fwd * fwd + left * left + up * up);
        if (len < 1.0E-4D) { state.pointing = false; return; }

        state.pointXRot = (float) Math.asin(-fwd / len);
        state.pointZRot = (float) Math.atan2(-left, -up);
    }
}