package net.guardian_of_stone.world.entitites.interfaces;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * Defines the ore-scouting contract for the Guardian of Stone.
 *
 * <h2>Scouting lifecycle</h2>
 * <ol>
 *   <li>A player right-clicks the Guardian with an ore item →
 *       {@link #startScouting(Item, Block)} is called, setting the scouting flag
 *       and storing the target block type.</li>
 *   <li>{@link net.guardian_of_stone.world.entitites.ai.goals.support.GuardianOreScoutGoal}
 *       picks up the flag, searches for the nearest matching vein, and escorts the
 *       Guardian there. Combat goals suppress themselves while {@link #isScouting()}
 *       is {@code true}.</li>
 *   <li>On arrival, {@link #startPointing(BlockPos)} freezes the Guardian facing the
 *       vein and starts the pointing animation. The scouting flag stays active during
 *       pointing to keep combat suppressed.</li>
 *   <li>After {@link GuardianOfStoneEntity#POINTING_DURATION_TICKS}
 *       ticks the entity calls {@link #stopPointing()} then
 *       {@link #stopScouting(boolean)}.</li>
 * </ol>
 *
 * <h2>Thread safety</h2>
 * <p>All methods in this interface are intended to be called from the server thread only.
 * The {@code isScouting()} and {@code isPointing()} flags are synced to the client
 * via {@link net.minecraft.network.syncher.SynchedEntityData} but must only be
 * mutated server-side.</p>
 */
public interface IGuardianScouting {

    /**
     * Enters scouting mode for the given ore type.
     *
     * @param item        the ore item handed to the Guardian (used for future feedback)
     * @param targetBlock the ore block the Guardian should search for
     */
    void startScouting(Item item, Block targetBlock);

    /**
     * Exits scouting mode.
     * <p>Called by {@link net.guardian_of_stone.world.entitites.ai.goals.support.GuardianOreScoutGoal}
     * upon success or failure.</p>
     *
     * @param success {@code true} if the Guardian successfully reached the vein
     */
    void stopScouting(boolean success);

    /**
     * Returns whether the Guardian is currently in scouting mode.
     *
     * @return {@code true} if scouting
     */
    boolean isScouting();

    /**
     * Returns the ore block type the Guardian is currently searching for.
     *
     * @return the target {@link Block}, or {@code null} if not scouting
     */
    @Nullable
    Block getScoutBlock();

    /**
     * Enters the pointing pose: the Guardian freezes, faces the vein, and raises
     * its arm for {@link GuardianOfStoneEntity#POINTING_DURATION_TICKS}
     * ticks.
     *
     * <p>Must be called <em>before</em> {@link #stopScouting(boolean)} so the
     * {@code SCOUTING} flag stays active (keeping combat suppressed) for the
     * full duration of the animation.</p>
     *
     * @param veinPos the block position of the found ore vein
     */
    void startPointing(BlockPos veinPos);

    /**
     * Exits the pointing pose and clears the stored vein position.
     */
    void stopPointing();

    /**
     * Returns whether the Guardian is currently in its pointing pose.
     *
     * @return {@code true} if pointing
     */
    boolean isPointing();

    /**
     * Returns the synchronized position of the ore vein the Guardian is pointing at.
     * Safe to call on both server and client.
     *
     * @return the vein {@link BlockPos}, or {@link BlockPos#ZERO} if not pointing
     */
    BlockPos getPointingTarget();
}