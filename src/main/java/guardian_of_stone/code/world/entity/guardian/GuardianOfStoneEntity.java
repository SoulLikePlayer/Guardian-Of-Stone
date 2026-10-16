package guardian_of_stone.code.world.entity.guardian;

import guardian_of_stone.code.world.entity.ModEntityDataSerializers;
import guardian_of_stone.code.world.entity.ai.goal.*;
import guardian_of_stone.code.world.entity.guardian.ability.IGuardianAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A neutral golem whose behavior depends on an ore type picked at spawn.
 * <p>
 * Each guardian has a {@link GuardianOfStoneType} that provides an {@link IGuardianAbility},
 * a {@link GuardianOfStoneState} (sleeping or awake) and can perform timed
 * {@link GuardianAction}s on items handed over by a player. It can also be told to search
 * for a given ore by right-clicking it with the matching item.
 */
public class GuardianOfStoneEntity extends AbstractGolem implements NeutralMob {

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private static final HashMap<Item, ArrayList<Block>> ORE_MAP = new HashMap<>();

    private static final EntityDataAccessor<@NotNull GuardianOfStoneState> STATE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, ModEntityDataSerializers.STATE_SERIALIZER);
    private static final EntityDataAccessor<@NotNull GuardianOfStoneType> TYPE =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, ModEntityDataSerializers.TYPE_SERIALIZER);
    private static final EntityDataAccessor<@NotNull Optional<BlockPos>> POINTING_POS =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<@NotNull Integer> ACTION =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<@NotNull ItemStack> WORK_ITEM =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<@NotNull Boolean> TERRIFIED =
            SynchedEntityData.defineId(GuardianOfStoneEntity.class, EntityDataSerializers.BOOLEAN);

    static {
        ORE_MAP.put(Items.COAL, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_COAL_ORE, Blocks.COAL_ORE)));
        ORE_MAP.put(Items.RAW_COPPER, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_COPPER_ORE, Blocks.COPPER_ORE)));
        ORE_MAP.put(Items.RAW_IRON, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE)));
        ORE_MAP.put(Items.REDSTONE, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.REDSTONE_ORE)));
        ORE_MAP.put(Items.LAPIS_LAZULI, new ArrayList<>(Arrays.asList(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE)));
        ORE_MAP.put(Items.RAW_GOLD, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_GOLD_ORE, Blocks.GOLD_ORE)));
        ORE_MAP.put(Items.EMERALD, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_EMERALD_ORE, Blocks.EMERALD_ORE)));
        ORE_MAP.put(Items.DIAMOND, new ArrayList<>(Arrays.asList(Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DIAMOND_ORE)));
    }

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState actionAnimationState = new AnimationState();

    private int attackAnimationTick;
    private int actionTicks;
    private long persistentAngerEndTime;
    private @Nullable EntityReference<@NotNull LivingEntity> persistentAngerTarget;
    private @Nullable UUID actionPlayer;
    private GuardianFindOreGoal findOreGoal;

    public GuardianOfStoneEntity(EntityType<? extends @NotNull AbstractGolem> type, Level level) {
        super(type, level);
    }

    /**
     * Builds the base attributes shared by every guardian.
     *
     * @return the attribute builder (health, speed, damage, knockback resistance, step height, follow range)
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.STEP_HEIGHT, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    /**
     * Assigns a random guardian type when the entity spawns.
     *
     * @param level       the level the entity spawns in
     * @param difficulty  the local difficulty at the spawn position
     * @param spawnReason why the entity is spawning
     * @param groupData   the spawn group data, if any
     * @return the group data returned by the parent implementation
     */
    @Override
    @SuppressWarnings("deprecation")
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                                  @NotNull DifficultyInstance difficulty,
                                                  @NotNull EntitySpawnReason spawnReason,
                                                  @Nullable SpawnGroupData groupData) {
        setGuardianType(GuardianOfStoneType.getRandomized(level.getRandom()));
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    /**
     * Registers the synchronized data of the guardian with its default values.
     *
     * @param entityData the builder collecting the synchronized data definitions
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(STATE, GuardianOfStoneState.SLEEP);
        entityData.define(TYPE, GuardianOfStoneType.COAL);
        entityData.define(POINTING_POS, Optional.empty());
        entityData.define(ACTION, 0);
        entityData.define(WORK_ITEM, ItemStack.EMPTY);
        entityData.define(TERRIFIED, false);
    }

    /**
     * Saves the state, type and work item of the guardian.
     *
     * @param output the destination of the saved data
     */
    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.store("State", GuardianOfStoneState.CODEC, this.getState());
        output.store("Type", GuardianOfStoneType.CODEC, this.getGuardianType());
        output.store("WorkItem", ItemStack.OPTIONAL_CODEC, getWorkItem());
    }

    /**
     * Restores the state, type and work item of the guardian, using defaults for missing values.
     *
     * @param input the source of the saved data
     */
    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);

        this.setState(input.read("State", GuardianOfStoneState.CODEC).orElse(GuardianOfStoneState.SLEEP));
        this.setGuardianType(input.read("Type", GuardianOfStoneType.CODEC).orElse(GuardianOfStoneType.COAL));
        this.entityData.set(WORK_ITEM, input.read("WorkItem", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
    }

    /**
     * Registers the movement goals (flee from wardens, use ability, search ore)
     * and the targeting goals (retaliation, hostile mobs except creepers and wardens, angry players).
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();

        findOreGoal = new GuardianFindOreGoal(this, 50);

        this.goalSelector.addGoal(0, new GuardianFleeWardenGoal(this));
        this.goalSelector.addGoal(0, new GuardianAvoidSculkGoal(this));
        this.goalSelector.addGoal(1, new GuardianMeleeAttackGoal(this, 1.0f, true));
        this.goalSelector.addGoal(1, new GuardianUseAbilityGoal(this));
        this.goalSelector.addGoal(2, findOreGoal);

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                (target, _) -> target instanceof Enemy
                        && !(target instanceof Creeper)
                        && !(target instanceof Warden)));
    }

    /**
     * Handles a right-click: starts an action with the held item, or, if the item is an ore drop,
     * tells the guardian which blocks to search for.
     *
     * @param player the player interacting with the guardian
     * @param hand   the hand used for the interaction
     * @return the result of the interaction
     */
    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (this.level() instanceof ServerLevel serverLevel) {
            if (getAction() != GuardianAction.NONE) return InteractionResult.SUCCESS_SERVER;
            if (startAction(serverLevel, player, held)) return InteractionResult.SUCCESS_SERVER;
        }

        if (ORE_MAP.containsKey(held.getItem())) {
            if (!level().isClientSide()) findOreGoal.setSearchedBlock(ORE_MAP.get(held.getItem()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /**
     * Starts the action of the current ability using the item held by the player.
     * Consumes the required amount of items unless the player has infinite materials.
     *
     * @param level  the server level
     * @param player the player giving the item
     * @param held   the item stack held by the player
     * @return {@code true} if an action was started, {@code false} if the guardian is busy,
     * has no action, or the item is not accepted
     */
    public boolean startAction(ServerLevel level, Player player, ItemStack held) {
        if (isTerrified()) return false;

        IGuardianAbility ability = ability();
        GuardianAction action = ability.getAction();
        if (action == GuardianAction.NONE || getAction() != GuardianAction.NONE) return false;
        if (held.isEmpty() || !ability.canUse(this, level, player, held)) return false;

        int count = Math.min(ability.getInputCount(held), held.getCount());
        ItemStack taken = player.hasInfiniteMaterials() ? held.copyWithCount(count) : held.split(count);

        this.actionPlayer = player.getUUID();
        this.actionTicks = 0;
        this.setPointingPos(null);
        this.entityData.set(WORK_ITEM, taken);
        this.entityData.set(ACTION, action.ordinal());
        return true;
    }

    /**
     * Advances the current action by one tick and finishes it when its duration is reached.
     * When no action is running, drops any leftover work item.
     *
     * @param level the server level
     */
    private void tickAction(ServerLevel level) {
        GuardianAction action = getAction();

        if (action == GuardianAction.NONE) {
            if (!getWorkItem().isEmpty()) {
                this.spawnAtLocation(level, getWorkItem().copy());
                this.entityData.set(WORK_ITEM, ItemStack.EMPTY);
            }
            return;
        }

        actionTicks++;
        ability().onActionTick(this, level, actionTicks);
        if (actionTicks >= action.getDurationTicks()) finishAction(level);
    }

    /**
     * Completes the current action, resets the action data and gives the result
     * to the player, or drops it if the player is gone or dead.
     *
     * @param level the server level
     */
    private void finishAction(ServerLevel level) {
        ItemStack result = ability().finish(this, level, getWorkItem().copy());
        Player player = getActionPlayer();

        this.entityData.set(WORK_ITEM, ItemStack.EMPTY);
        this.entityData.set(ACTION, GuardianAction.NONE.ordinal());
        this.actionPlayer = null;
        this.actionTicks = 0;

        if (result.isEmpty()) return;
        if (player != null && player.isAlive()) player.addItem(result);
        else this.spawnAtLocation(level, result);
    }

    /**
     * Runs the ability logic and the current action on the server,
     * or updates the animation states on the client.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) this.attackAnimationTick--;

        if (this.level() instanceof ServerLevel serverLevel) {
            ability().tick(this, serverLevel);
            tickAction(serverLevel);
        } else {
            this.setupAnimationStates();
        }
    }

    /**
     * Applies damage and notifies the ability if the guardian was actually hurt.
     *
     * @param level  the server level
     * @param source the source of the damage
     * @param amount the amount of damage
     * @return {@code true} if the guardian was hurt
     */
    @Override
    public boolean hurtServer(@NotNull ServerLevel level,
                              @NotNull DamageSource source,
                              float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt) ability().onHurt(this, level, source, amount);
        return hurt;
    }

    /**
     * Attacks the target, broadcasts the attack animation event and notifies the ability on a hit.
     *
     * @param level  the server level
     * @param target the entity being attacked
     * @return {@code true} if the target was hit
     */
    @Override
    public boolean doHurtTarget(ServerLevel level,
                                @NotNull Entity target) {
        level.broadcastEntityEvent(this, (byte) 4);
        boolean hit = super.doHurtTarget(level, target);
        if (hit) ability().onAttack(this, level, target);
        return hit;
    }

    /**
     * Starts the attack animation when the attack event (id 4) is received.
     *
     * @param id the entity event id
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationTick = 20;
        } else {
            super.handleEntityEvent(id);
        }
    }

    /**
     * Updates the attack and action animation states on the client.
     */
    private void setupAnimationStates() {
        this.attackAnimationState.animateWhen(this.attackAnimationTick > 0, this.tickCount);
        this.actionAnimationState.animateWhen(getAction() != GuardianAction.NONE, this.tickCount);
    }

    /**
     * Drops the loot of the guardian's ore type (quantities and chances depend on the type)
     * and any item still being worked on.
     *
     * @param level       the server level
     * @param source      the damage source that killed the guardian
     * @param recentlyHit whether a player recently damaged the guardian
     */
    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        ItemStack drop = switch (getGuardianType()) {
            case COAL -> new ItemStack(Items.COAL, 1 + random.nextInt(3));
            case COPPER -> new ItemStack(Items.RAW_COPPER, 1 + random.nextInt(3));
            case IRON -> new ItemStack(Items.RAW_IRON, 1 + random.nextInt(2));
            case REDSTONE -> new ItemStack(Items.REDSTONE, 2 + random.nextInt(4));
            case LAPIS -> new ItemStack(Items.LAPIS_LAZULI, 2 + random.nextInt(4));
            case GOLD -> new ItemStack(Items.RAW_GOLD, 1 + random.nextInt(2));
            case AMETHYST -> new ItemStack(Items.AMETHYST_SHARD, 1 + random.nextInt(3));
            case DIAMOND -> random.nextFloat() < 0.35F ? new ItemStack(Items.DIAMOND) : ItemStack.EMPTY;
            case EMERALD -> random.nextFloat() < 0.35F ? new ItemStack(Items.EMERALD) : ItemStack.EMPTY;
            case NONE -> ItemStack.EMPTY;
        };

        if (!drop.isEmpty()) this.spawnAtLocation(level, drop);
        if (!getWorkItem().isEmpty()) this.spawnAtLocation(level, getWorkItem().copy());
    }

    /**
     * Computes where the work item is displayed: in front of the guardian, at chest height.
     *
     * @return the world position of the work item
     */
    public Vec3 getWorkPos() {
        return this.position()
                .add(Vec3.directionFromRotation(0.0F, this.yBodyRot).scale(0.9D))
                .add(0.0D, 1.4D, 0.0D);
    }

    /**
     * Gets the player who started the current action.
     *
     * @return the player, or {@code null} if there is none or the player is offline
     */
    public @Nullable Player getActionPlayer() {
        return actionPlayer == null ? null : level().getPlayerByUUID(actionPlayer);
    }

    /**
     * Updates the state and notifies the ability only if the state actually changed.
     *
     * @param state the new state
     */
    public void setState(GuardianOfStoneState state) {
        GuardianOfStoneState old = getState();
        this.entityData.set(STATE, state);
        if (old != state) ability().onStateChange(this, old, state);
    }

    public GuardianOfStoneState getState() {
        return this.entityData.get(STATE);
    }

    public GuardianOfStoneType getGuardianType() {
        return this.entityData.get(TYPE);
    }

    public void setGuardianType(GuardianOfStoneType type) {
        this.entityData.set(TYPE, type);
    }

    public GuardianAction getAction() {
        return GuardianAction.BY_ID.apply(this.entityData.get(ACTION));
    }

    public ItemStack getWorkItem() {
        return this.entityData.get(WORK_ITEM);
    }

    public @Nullable BlockPos getPointingPos() {
        return this.entityData.get(POINTING_POS).orElse(null);
    }

    public void setPointingPos(@Nullable BlockPos pos) {
        this.entityData.set(POINTING_POS, Optional.ofNullable(pos));
    }

    private IGuardianAbility ability() {
        return getGuardianType().getAbility();
    }

    /**
     * Tells whether other entities can push the guardian. Only an awake guardian can be pushed.
     *
     * @return {@code true} if the guardian is awake
     */
    @Override
    public boolean isPushable() {
        return getState().equals(GuardianOfStoneState.AWAKE);
    }

    /**
     * Picks a random anger duration between 20 and 39 seconds and applies it.
     */
    @Override
    public void startPersistentAngerTimer() {
        this.setTimeToRemainAngry(PERSISTENT_ANGER_TIME.sample(this.random));
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

    public boolean isTerrified() { return this.entityData.get(TERRIFIED); }
    public void setTerrified(boolean terrified) { this.entityData.set(TERRIFIED, terrified); }

    public void cancelAction() {
        if (getAction() == GuardianAction.NONE) return;
        this.entityData.set(ACTION, GuardianAction.NONE.ordinal());
        this.actionPlayer = null;
        this.actionTicks = 0;
    }

    @Override
    public boolean dampensVibrations() {
        return isTerrified() || super.dampensVibrations();
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        return !(target instanceof Warden) && super.canAttack(target);
    }
}