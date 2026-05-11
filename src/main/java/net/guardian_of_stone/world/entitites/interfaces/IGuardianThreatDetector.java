package net.guardian_of_stone.world.entitites.interfaces;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates the Guardian's threat-evaluation logic.
 *
 * <h2>Threat rules</h2>
 * <p>An entity is considered a threat if all of the following are true:</p>
 * <ul>
 *   <li>It is not the Guardian itself.</li>
 *   <li>It is not a {@link net.minecraft.world.entity.monster.spider.Spider}
 *       (the Guardian is neutral toward spiders).</li>
 *   <li>It is not a {@link net.minecraft.world.entity.monster.warden.Warden}
 *       (the Guardian fears Wardens and never fights them).</li>
 *   <li>It is either a {@link net.minecraft.world.entity.monster.Monster},
 *       or a {@link net.minecraft.world.entity.player.Player} the Guardian is
 *       currently angry at (via {@link net.minecraft.world.entity.NeutralMob}).</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>This interface is used by {@code updateActiveState()} in
 * {@link GuardianOfStoneEntity} and by several goal classes
 * (e.g. {@link net.guardian_of_stone.world.entitites.ai.goals.attack.GuardianGroundSlamGoal})
 * that need to count or collect nearby hostile entities.</p>
 */
public interface IGuardianThreatDetector {

    /**
     * Returns whether this entity qualifies as a threat to the Guardian.
     *
     * @param entity   the candidate entity to evaluate
     * @param guardian the Guardian performing the check (used for angry-at lookup)
     * @return {@code true} if the entity should wake or target the Guardian
     */
    boolean isThreat(@NotNull LivingEntity entity, @NotNull GuardianOfStoneEntity guardian);
}