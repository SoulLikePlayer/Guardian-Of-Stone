package guardian_of_stone.code.mixin;

import guardian_of_stone.code.world.entity.guardian.ability.OverloadAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireEvaluator.class)
public abstract class RedstoneWireEvaluatorMixin {

    @Inject(method = "getBlockSignal", at = @At("RETURN"), cancellable = true)
    private void guardian_of_stone$overload(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() < 15 && OverloadAbility.isOverloaded(level, pos)) {
            cir.setReturnValue(15);
        }
    }
}