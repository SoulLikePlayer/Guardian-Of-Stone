package net.guardian_of_stone.world.entitites.ai.goals;

import net.guardian_of_stone.world.entitites.GuardianOfStoneEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.spider.Spider;
import org.jetbrains.annotations.NotNull;

/**
 * A targeting goal that makes the {@link GuardianOfStoneEntity} pursue the
 * nearest {@link Monster}, with a hard exclusion for {@link Spider} mobs.
 *
 * <h2>Design rationale</h2>
 * <p>The Guardian is a neutral protector. It should feel like it defends its
 * territory from generic threats — zombies, skeletons, creepers.</p>
 *
 * <h2>Target selection</h2>
 * <ul>
 *   <li>Entity class: any {@link Monster}.</li>
 *   <li>Must-see: {@code false} — the Guardian can "sense" monsters even through
 *       thin walls, fitting for a primordial sentinel.</li>
 * </ul>
 */
public class GuardianNearestMonsterGoal extends NearestAttackableTargetGoal<@NotNull Monster> {

    /**
     * Constructs the targeting goal.
     *
     * @param guardian the Guardian that will use this goal
     */
    public GuardianNearestMonsterGoal(GuardianOfStoneEntity guardian) {
        super(guardian, Monster.class, 0, true,false, (livingEntity, serverLevel) -> (!(livingEntity instanceof Spider)) );
    }
}