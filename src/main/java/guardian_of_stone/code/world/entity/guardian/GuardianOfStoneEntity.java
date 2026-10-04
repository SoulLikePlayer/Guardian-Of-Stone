package guardian_of_stone.code.world.entity.guardian;

import guardian_of_stone.code.world.entity.ModEntityDataSerializers;
import guardian_of_stone.code.world.entity.ai.goal.GuardianFindOreGoal;
import guardian_of_stone.code.world.entity.ai.goal.GuardianMeleeAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class GuardianOfStoneEntity extends AbstractGolem implements NeutralMob {
    private int attackAnimationTick;
    private long persistentAngerEndTime;
    private @Nullable EntityReference<@NotNull LivingEntity> persistentAngerTarget;

    private static final UniformInt PERSISTENT_ANGER_TIME;

    private static final EntityDataAccessor<@NotNull GuardianOfStoneState> STATE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, ModEntityDataSerializers.STATE_SERIALIZER);
    private static final EntityDataAccessor<@NotNull GuardianOfStoneType> TYPE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, ModEntityDataSerializers.TYPE_SERIALIZER);

    private static final EntityDataAccessor<@NotNull Optional<BlockPos>> POINTING_POS =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);


    private static final HashMap<Item, ArrayList<Block>> ORE_MAP = new HashMap<>();
    private GuardianFindOreGoal findOreGoal;


    public final AnimationState attackAnimationState = new AnimationState();

    public GuardianOfStoneEntity(EntityType<? extends @NotNull AbstractGolem> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        setGuardianType(GuardianOfStoneType.getRandomized(level.getRandom()));

        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(STATE, GuardianOfStoneState.SLEEP);
        entityData.define(TYPE, GuardianOfStoneType.COAL);
        entityData.define(POINTING_POS, Optional.empty());
    }

    public GuardianOfStoneState getState() {
        return this.entityData.get(STATE);
    }

    public void setState(GuardianOfStoneState state){
        this.entityData.set(STATE, state);
    }

    public GuardianOfStoneType getGuardianType(){
        return this.entityData.get(TYPE);
    }

    public void setGuardianType(GuardianOfStoneType type){
        this.entityData.set(TYPE, type);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.store("State", GuardianOfStoneState.CODEC, this.getState());
        output.store("Type", GuardianOfStoneType.CODEC, this.getGuardianType());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);

        this.setState(input.read("State", GuardianOfStoneState.CODEC).orElse(GuardianOfStoneState.SLEEP));
        this.setGuardianType(input.read("Type", GuardianOfStoneType.CODEC).orElse(GuardianOfStoneType.COAL));

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        findOreGoal = new GuardianFindOreGoal(this, 50);

        this.goalSelector.addGoal(1, new GuardianMeleeAttackGoal(this, 1.0F, true));
        this.goalSelector.addGoal(2, findOreGoal);

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (target, _) -> target instanceof Enemy && !(target instanceof Creeper)));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        Item item = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        if(!ORE_MAP.containsKey(item)) return InteractionResult.PASS;

        if(!level().isClientSide()) {
            findOreGoal.setSearchedBlock(ORE_MAP.get(item));
        }

        ItemStack held = player.getItemInHand(hand);

        switch (this.getGuardianType()){
            case COAL -> {
                if (!held.isEmpty() && this.level() instanceof ServerLevel serverLevel) {
                    if (trySmelt(serverLevel, player, held)) {
                        return InteractionResult.SUCCESS_SERVER;
                    }
                }

            }
            case COPPER -> {}
            case IRON -> {}
            case LAPIS -> {}
            case REDSTONE -> {}
            case EMERALD -> {}
            case AMETHYST -> {}
            case DIAMOND -> {}
        }

        return InteractionResult.SUCCESS;
    }

    private boolean trySmelt(ServerLevel level, Player player, ItemStack held) {
        SingleRecipeInput input = new SingleRecipeInput(held);

        Optional<RecipeHolder<@NotNull SmeltingRecipe>> recipe =
                level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().assemble(input);
        if (result.isEmpty()) return false;

        if (!player.hasInfiniteMaterials()) {
            held.shrink(1);
        }

        if (!player.getInventory().add(result)) {
            player.addItem(result);
        }

        this.playSound(SoundEvents.FURNACE_FIRE_CRACKLE, 1.0F, 1.0F);
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            this.attackAnimationTick--;
        }
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, @NotNull Entity target) {
        level.broadcastEntityEvent(this, (byte) 4);
        return super.doHurtTarget(level, target);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationTick = 20;
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void setupAnimationStates() {
        this.attackAnimationState.animateWhen(this.attackAnimationTick > 0, this.tickCount);
    }

    @Override
    public long getPersistentAngerEndTime() {
        return this.persistentAngerEndTime;
    }


    @Override
    public void setPersistentAngerEndTime(long endTime) {
        this.persistentAngerEndTime = endTime;
    }

    @Override
    public @Nullable EntityReference<@NotNull LivingEntity> getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<@NotNull LivingEntity> persistentAngerTarget) {
        this.persistentAngerTarget = persistentAngerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setTimeToRemainAngry(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public void setPointingPos(@Nullable BlockPos pos) {
        this.entityData.set(POINTING_POS, Optional.ofNullable(pos));
    }

    public @Nullable BlockPos getPointingPos() {
        return this.entityData.get(POINTING_POS).orElse(null);
    }

    @Override
    public boolean isPushable() {
        return getState().equals(GuardianOfStoneState.AWAKE);
    }

    static {
        PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

        ORE_MAP.put(Items.COAL, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_COAL_ORE, Blocks.COAL_ORE)));
        ORE_MAP.put(Items.RAW_COPPER, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_COPPER_ORE, Blocks.COPPER_ORE)));
        ORE_MAP.put(Items.RAW_IRON, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE)));
        ORE_MAP.put(Items.REDSTONE, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.REDSTONE_ORE)));
        ORE_MAP.put(Items.LAPIS_LAZULI, new ArrayList<>(Arrays.asList(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE)));
        ORE_MAP.put(Items.RAW_GOLD, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_GOLD_ORE, Blocks.GOLD_ORE)));
        ORE_MAP.put(Items.EMERALD, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_EMERALD_ORE, Blocks.EMERALD_ORE)));
        ORE_MAP.put(Items.DIAMOND, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DIAMOND_ORE)));
    }
}
