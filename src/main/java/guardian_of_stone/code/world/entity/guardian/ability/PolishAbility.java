package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.Optional;

public class PolishAbility implements GuardianAbility {
    @Override
    public boolean tryUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        if (!(held.getItem() instanceof BlockItem blockItem)) return false;

        Block block = blockItem.getBlock();

        Block unwaxed = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(block);
        Optional<Block> previous = WeatheringCopper.getPrevious(block);

        Block result = unwaxed != null ? unwaxed : previous.orElse(null);
        if (result == null) return false;

        ItemStack polished = new ItemStack(result, held.getCount());
        if (!player.hasInfiniteMaterials()) held.setCount(0);

        if (!player.getInventory().add(polished)) player.addItem(polished);

        guardian.playSound(SoundEvents.AXE_SCRAPE.value(), 1.0F, 1.0F);
        return true;
    }
}