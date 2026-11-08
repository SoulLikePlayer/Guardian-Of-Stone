package guardian_of_stone.code.world.sound;

import guardian_of_stone.code.core.GuardianOfStone;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModSounds {
    public static final DeferredRegister<@NotNull SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, "guardian_of_stone");

    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_HURT = register("entity.guardian.hurt");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_DEATH = register("entity.guardian.death");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_STEP = register("entity.guardian.step");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_ATTACK = register("entity.guardian.attack");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_WAKE = register("entity.guardian.wake");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_SLEEP = register("entity.guardian.sleep");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_AMBIENT = register("entity.guardian.ambient");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_WORK = register("entity.guardian.work");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_TERRIFIED = register("entity.guardian.terrified");
    public static final DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> GUARDIAN_TRADE = register("entity.guardian.trade");

    private static DeferredHolder<@NotNull SoundEvent, @NotNull SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(GuardianOfStone.MODID, name)));
    }
}