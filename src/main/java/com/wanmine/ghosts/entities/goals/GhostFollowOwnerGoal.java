package com.wanmine.ghosts.entities.goals;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class GhostFollowOwnerGoal extends FollowOwnerGoal {

    private final float teleportDistanceSqr;
    private final double speedModifier;
    private final PathNavigation navigation;

    public GhostFollowOwnerGoal(TamableAnimal tamable, double speedModifier, float startDistance, float stopDistance) {
        super(tamable, speedModifier, startDistance, stopDistance, true);
        var teleportDistance = startDistance + 5F;
        this.teleportDistanceSqr = teleportDistance * teleportDistance;
        this.speedModifier = speedModifier;
        this.navigation = tamable.getNavigation();
    }

    @Override
    protected void teleportToOwner() {
        if (tamable.distanceToSqr(owner) >= teleportDistanceSqr) {
            super.teleportToOwner();
        } else {
            navigation.moveTo(this.owner, this.speedModifier);
        }
    }
}
