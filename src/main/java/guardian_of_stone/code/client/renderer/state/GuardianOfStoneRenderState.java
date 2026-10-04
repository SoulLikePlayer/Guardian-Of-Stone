package guardian_of_stone.code.client.renderer.state;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class GuardianOfStoneRenderState extends LivingEntityRenderState {
    public final AnimationState attackAnimationState = new AnimationState();

    public GuardianOfStoneState guardianOfStoneState;
    public GuardianOfStoneType guardianOfStoneType;

    public boolean pointing;
    public float pointXRot;
    public float pointZRot;
}
