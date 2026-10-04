package guardian_of_stone.code.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import guardian_of_stone.code.client.GuardianOfStoneClient;
import guardian_of_stone.code.client.model.GuardianOfStoneModel;
import guardian_of_stone.code.client.renderer.state.GuardianOfStoneRenderState;
import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneType;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.zombie.DrownedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GuardianOfStoneOuterLayer extends RenderLayer<@NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> {
    private final GuardianOfStoneModel model;
    private static final Map<GuardianOfStoneType, Identifier> GUARDIAN_OUTER_LOCATION = new HashMap<>();

    static {
        for (GuardianOfStoneType type : GuardianOfStoneType.values()) {
            GUARDIAN_OUTER_LOCATION.put(type, Identifier.fromNamespaceAndPath(
                    GuardianOfStone.MODID,
                    "textures/entity/guardian_of_stone/type/" + type.getSerializedName() + ".png"));
        }
    }

    public GuardianOfStoneOuterLayer(RenderLayerParent<@NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new GuardianOfStoneModel(modelSet.bakeLayer(GuardianOfStoneClient.GUARDIAN_OF_STONE_OUTER));
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int lightCoords, GuardianOfStoneRenderState state, float v, float v1) {
        Identifier layerLocation = GUARDIAN_OUTER_LOCATION.get(state.guardianOfStoneType);
        if (layerLocation == null) return;
        coloredCutoutModelCopyLayerRender(model, layerLocation, poseStack, submitNodeCollector, lightCoords, state, -1, 1);
    }
}
