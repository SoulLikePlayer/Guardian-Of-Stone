package guardian_of_stone.code.client.renderer;

import guardian_of_stone.code.client.GuardianOfStoneClient;
import guardian_of_stone.code.client.model.GuardianOfStoneModel;
import guardian_of_stone.code.client.renderer.state.GuardianOfStoneRenderState;
import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class GuardianOfStoneRenderer <T extends GuardianOfStoneEntity> extends MobRenderer<@NotNull T, @NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> {
    private static final Identifier SLEEP_TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/sleep_guardian_of_stone.png");
    private static final Identifier AWAKE_TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, "textures/entity/guardian_of_stone/awake_guardian_of_stone.png");

    public GuardianOfStoneRenderer(EntityRendererProvider.Context context) {
        super(context, new GuardianOfStoneModel(context.bakeLayer(GuardianOfStoneClient.GUARDIAN_OF_STONE)), 1f);
    }

    public @NotNull Identifier getTextureLocation(GuardianOfStoneRenderState state) {
        return state.guardianOfStoneState.equals(GuardianOfStoneState.AWAKE) ? AWAKE_TEXTURE_LOCATION : SLEEP_TEXTURE_LOCATION;
    }

    public GuardianOfStoneRenderState createRenderState() {
        return new GuardianOfStoneRenderState();
    }

    public void extractRenderState(T entity, GuardianOfStoneRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.attackAnimationState.copyFrom(entity.attackAnimationState);

        state.guardianOfStoneState = entity.getState();
    }
}