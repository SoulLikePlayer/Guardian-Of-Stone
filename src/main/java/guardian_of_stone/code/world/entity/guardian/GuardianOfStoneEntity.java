package guardian_of_stone.code.world.entity.guardian;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class GuardianOfStoneEntity extends AbstractGolem implements NeutralMob {
    public GuardianOfStoneEntity(EntityType<? extends @NotNull AbstractGolem> type, Level level) {
        super(type, level);
    }

    @Override
    public long getPersistentAngerEndTime() {
        return 0;
    }

    @Override
    public void setPersistentAngerEndTime(long l) {

    }

    @Override
    public @Nullable EntityReference<@NotNull LivingEntity> getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<@NotNull LivingEntity> entityReference) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }
}