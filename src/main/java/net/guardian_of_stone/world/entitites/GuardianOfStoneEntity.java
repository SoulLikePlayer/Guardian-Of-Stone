package net.guardian_of_stone.world.entitites;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class GuardianOfStoneEntity extends PathfinderMob implements NeutralMob {
    protected GuardianOfStoneEntity(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    public long getPersistentAngerEndTime() {
        return 0;
    }

    public void setPersistentAngerEndTime(long l) {

    }

    public @Nullable EntityReference<@NotNull LivingEntity> getPersistentAngerTarget() {
        return null;
    }

    public void setPersistentAngerTarget(@Nullable EntityReference<@NotNull LivingEntity> entityReference) {

    }

    public void startPersistentAngerTimer() {

    }
}