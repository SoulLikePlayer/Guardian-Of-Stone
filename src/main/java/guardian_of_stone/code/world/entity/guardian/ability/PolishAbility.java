package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class PolishAbility implements IGuardianAbility {
    @Override public GuardianAction getAction() { return GuardianAction.POLISH; }
    @Override public int getInputCount(ItemStack held) { return held.getCount(); }

    @Override
    public boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        return convert(held) != null;
    }

    @Override
    public void onActionTick(GuardianOfStoneEntity guardian, ServerLevel level, int tick) {
        if (tick % 10 != 1) return;
        Vec3 p = guardian.getWorkPos();
        level.sendParticles(ParticleTypes.SCRAPE, p.x, p.y, p.z, 4, 0.2, 0.2, 0.2, 0.0);
        guardian.playSound(SoundEvents.AXE_SCRAPE.value(), 1.0F, 1.0F);
    }

    @Override
    public ItemStack finish(GuardianOfStoneEntity guardian, ServerLevel level, ItemStack input) {
        Block result = convert(input);
        return result == null ? input : new ItemStack(result, input.getCount());
    }

    private static @Nullable Block convert(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return null;
        Block block = blockItem.getBlock();
        Block unwaxed = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(block);
        return unwaxed != null ? unwaxed : WeatheringCopper.getPrevious(block).orElse(null);
    }
}