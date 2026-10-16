package guardian_of_stone.code.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import guardian_of_stone.code.client.GuardianOfStoneClient;
import guardian_of_stone.code.client.model.GuardianOfStoneModel;
import guardian_of_stone.code.client.renderer.state.GuardianOfStoneRenderState;
import guardian_of_stone.code.core.GuardianOfStone;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneMossLevel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class GuardianOfStoneMossLayer extends RenderLayer<@NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> {
    private static final Map<GuardianOfStoneMossLevel, Identifier> TEXTURES = new EnumMap<>(GuardianOfStoneMossLevel.class);

    static {
        for (GuardianOfStoneMossLevel level : GuardianOfStoneMossLevel.values()) {
            if (level.getTexture() == null) continue;
            TEXTURES.put(level, Identifier.fromNamespaceAndPath(GuardianOfStone.MODID,
                    "textures/entity/guardian_of_stone/moss/" + level.getTexture() + ".png"));
        }
    }

    private final GuardianOfStoneModel model;

    public GuardianOfStoneMossLayer(RenderLayerParent<@NotNull GuardianOfStoneRenderState, @NotNull GuardianOfStoneModel> renderer,
                                    EntityModelSet modelSet) {
        super(renderer);
        this.model = new GuardianOfStoneModel(modelSet.bakeLayer(GuardianOfStoneClient.GUARDIAN_OF_STONE_MOSS));
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, int lightCoords,
                       GuardianOfStoneRenderState state, float yRot, float xRot) {
        Identifier texture = TEXTURES.get(state.mossLevel);
        if (texture == null) return;
        coloredCutoutModelCopyLayerRender(model, texture, poseStack, collector, lightCoords, state, -1, 2);
    }
}