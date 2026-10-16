package guardian_of_stone.code.world.entity.guardian.ability;

import guardian_of_stone.code.world.entity.guardian.GuardianAction;
import guardian_of_stone.code.world.entity.guardian.GuardianOfStoneEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MidasTouchAbility implements IGuardianAbility {
    private static final HashMap<Item, Item> GOLDEN_ITEM_MAP = new HashMap<>();

    static {
        GOLDEN_ITEM_MAP.put(Items.CARROT, Items.GOLDEN_CARROT);
        GOLDEN_ITEM_MAP.put(Items.APPLE, Items.GOLDEN_APPLE);
    }

    @Override public GuardianAction getAction() { return GuardianAction.MIDAS; }
    @Override public int getInputCount(ItemStack held) { return held.getCount(); }

    @Override
    public boolean canUse(GuardianOfStoneEntity guardian, ServerLevel level, Player player, ItemStack held) {
        return !held.isEmpty() && GOLDEN_ITEM_MAP.containsKey(held.getItem());
    }

    @Override
    public void onActionTick(GuardianOfStoneEntity guardian, ServerLevel level, int tick) {
        if (tick % 3 != 0) return;
        Vec3 p = guardian.getWorkPos();
        level.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 2, 0.2, 0.2, 0.2, 0.02);
    }

    @Override
    public ItemStack finish(GuardianOfStoneEntity guardian, ServerLevel level, ItemStack input) {
        Item golden = GOLDEN_ITEM_MAP.get(input.getItem());
        return golden == null ? input : new ItemStack(golden, input.getCount());
    }
}