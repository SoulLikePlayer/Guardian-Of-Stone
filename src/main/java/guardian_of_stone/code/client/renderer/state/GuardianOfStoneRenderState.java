package guardian_of_stone.code.client.renderer.state;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneMossLevel;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneState;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.AnimationState;

public class GuardianOfStoneRenderState extends LivingEntityRenderState {
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState actionAnimationState = new AnimationState();
    public final ItemStackRenderState workItem = new ItemStackRenderState();

    public GuardianOfStoneState guardianOfStoneState;
    public GuardianOfStoneType guardianOfStoneType;
    public GuardianAction action = GuardianAction.NONE;
    public GuardianOfStoneMossLevel mossLevel = GuardianOfStoneMossLevel.NONE;

    public boolean shakingMoss;
    public boolean pointing;
    public boolean terrified;
    public float pointXRot;
    public float pointZRot;
}
