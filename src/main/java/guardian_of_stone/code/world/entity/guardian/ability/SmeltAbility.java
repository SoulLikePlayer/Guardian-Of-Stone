package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SmeltAbility implements IGuardianAbility {
    @Override public GuardianAction getAction() { return GuardianAction.SMELT; }

    @Override
    public boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        return !smelt(level, held).isEmpty();
    }

    @Override
    public void onActionTick(GuardianOfStoneEntity guardian, ServerLevel level, int tick) {
        Vec3 p = guardian.getWorkPos();
        if (tick % 3 == 0) level.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 2, 0.1, 0.1, 0.1, 0.01);
        if (tick % 20 == 1) guardian.playSound(SoundEvents.FURNACE_FIRE_CRACKLE, 1.0F, 1.0F);
    }

    @Override
    public ItemStack finish(GuardianOfStoneEntity guardian, ServerLevel level, ItemStack input) {
        ItemStack result = smelt(level, input);
        if (result.isEmpty()) return input;
        Vec3 p = guardian.getWorkPos();
        level.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 8, 0.15, 0.15, 0.15, 0.02);
        return result;
    }

    private static ItemStack smelt(ServerLevel level, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<@NotNull SmeltingRecipe>> recipe =
                level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level);
        return recipe.isEmpty() ? ItemStack.EMPTY : recipe.get().value().assemble(input);
    }
}