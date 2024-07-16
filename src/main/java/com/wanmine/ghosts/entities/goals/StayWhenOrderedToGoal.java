package com.wanmine.ghosts.entities.goals;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;

public class StayWhenOrderedToGoal extends SitWhenOrderedToGoal {

    private final TamableAnimal mob;

    public StayWhenOrderedToGoal(TamableAnimal mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (!mob.isTame()) {
            return false;
        } else if (mob.isInWaterOrBubble()) {
            return false;
        } else {
            return mob.isOrderedToSit();
        }
    }
}
