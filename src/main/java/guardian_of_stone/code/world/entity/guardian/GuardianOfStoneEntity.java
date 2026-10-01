package guardian_of_stone.code.world.entity.guardian;

import guardian_of_stone.code.world.entity.ModEntityDataSerializers;
import guardian_of_stone.code.world.entity.ai.goal.GuardianMeleeAttackGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class GuardianOfStoneEntity extends AbstractGolem implements NeutralMob {
    private static final EntityDataAccessor<@NotNull GuardianOfStoneState> STATE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, ModEntityDataSerializers.STATE_SERIALIZER);

    public GuardianOfStoneEntity(EntityType<? extends @NotNull AbstractGolem> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(STATE, GuardianOfStoneState.SLEEP);
    }

    public GuardianOfStoneState getState() {
        return this.entityData.get(STATE);
    }

    public void setState(GuardianOfStoneState state){
        this.entityData.set(STATE, state);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.store("State", GuardianOfStoneState.CODEC, this.getState());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);

        this.setState(input.read("State", GuardianOfStoneState.CODEC).orElse(GuardianOfStoneState.SLEEP));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new GuardianMeleeAttackGoal(this, 1.0F, true));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (target, var1) -> target instanceof Enemy && !(target instanceof Creeper)));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
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