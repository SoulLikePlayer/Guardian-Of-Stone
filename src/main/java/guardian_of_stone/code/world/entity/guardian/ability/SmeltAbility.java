package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.Optional;

public class SmeltAbility implements GuardianAbility {
    @Override
    public boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        SingleRecipeInput input = new SingleRecipeInput(held);

        Optional<RecipeHolder<SmeltingRecipe>> recipe =
                level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().assemble(input);
        if (result.isEmpty()) return false;

        if (!player.hasInfiniteMaterials()) held.shrink(1);
        if (!player.getInventory().add(result)) player.addItem(result);

        guardian.playSound(SoundEvents.FURNACE_FIRE_CRACKLE, 1.0F, 1.0F);
        return true;
    }
}