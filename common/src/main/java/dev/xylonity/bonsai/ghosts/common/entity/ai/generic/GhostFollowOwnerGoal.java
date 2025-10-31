package dev.xylonity.bonsai.ghosts.common.entity.ai.generic;

import dev.xylonity.bonsai.ghosts.common.entity.AbstractGhostEntity;
import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GhostFollowOwnerGoal extends Goal {
    public static final int TELEPORT_WHEN_DISTANCE_IS = GhostsConfig.GHOSTS_FOLLOW_OWNER_TELEPORT_DISTANCE;

    private final double minDistance;
    private final double startDistance;
    private final double maxSpeedModifier;
    private final float lerpFactor;

    private final AbstractGhostEntity ghost;
    private final PathNavigation navigation;

    private LivingEntity owner;

    public GhostFollowOwnerGoal(AbstractGhostEntity ghost, double maxSpeedModifier, double minDistance, double startDistance, float lerpFactor) {
        this.ghost = ghost;
        this.maxSpeedModifier = maxSpeedModifier;
        this.minDistance = minDistance;
        this.startDistance = startDistance;
        this.lerpFactor = lerpFactor;

        this.navigation = this.ghost.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.ghost.getMainInteraction() != 1) return false;

        LivingEntity poss = this.ghost.getOwner();
        if (poss == null || !poss.isAlive()) return false;
        if (this.ghost.distanceToSqr(poss) < this.startDistance * this.startDistance) return false;
        this.owner = poss;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.ghost.getMainInteraction() != 1) return false;
        if (this.owner == null) return false;
        if (!this.owner.isAlive()) return false;

        return this.ghost.distanceToSqr(this.owner) > this.minDistance * this.minDistance;
    }

    @Override
    public void tick() {
        if (this.owner == null) return;

        if (this.ghost.distanceToSqr(this.owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS) {
            this.teleportToOwner();
        }

        ghost.lookAt(this.owner, 30f, 30f);

        double dx = this.owner.getX() - this.ghost.getX();
        double dy = (this.owner.getY() + 2.0) - this.ghost.getY();
        double dz = this.owner.getZ() - this.ghost.getZ();

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= this.minDistance) {
            Vec3 slowed = this.ghost.getDeltaMovement().scale(0.9);
            this.ghost.setDeltaMovement(slowed);

            if (slowed.lengthSqr() < 0.01) {
                this.navigation.stop();
                this.ghost.getMoveControl().setWantedPosition(this.ghost.getX(), this.ghost.getY(), this.ghost.getZ(), 0);
            }

            this.ghost.noPhysics = false;
            return;
        }

        double theta = dist >= this.startDistance ? 1.0 : Mth.clamp((dist - this.minDistance) / (this.startDistance - this.minDistance), 0.0, 1.0);

        Vec3 target = new Vec3(this.owner.getX() - (dx/dist) * this.minDistance, (this.owner.getY() + 2) - (dy/dist) * this.minDistance, this.owner.getZ() - (dz/dist) * this.minDistance);
        Vec3 vel = target.subtract(this.ghost.position()).normalize().scale(this.maxSpeedModifier * theta);

        Vec3 cVel = this.ghost.getDeltaMovement();
        this.ghost.setDeltaMovement(new Vec3(Mth.lerp(this.lerpFactor, cVel.x, vel.x), Mth.lerp(this.lerpFactor, cVel.y, vel.y), Mth.lerp(this.lerpFactor, cVel.z, vel.z)));

        this.ghost.noPhysics = isPathBlocked(this.ghost.level(), this.ghost.getEyePosition(), target);
    }

    private boolean isPathBlocked(Level level, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.ghost)).getType() != HitResult.Type.MISS;
    }

    protected void teleportToOwner() {
        BlockPos pos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int x = this.randomIntInclusive(-3, 3);
            int y = this.randomIntInclusive(-1, 1);
            int z = this.randomIntInclusive(-3, 3);
            if (this.maybeTeleportTo(pos.getX() + x, pos.getY() + y, pos.getZ() + z)) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int pX, int pY, int pZ) {
        if (Math.abs(pX - this.owner.getX()) < 2.0F && Math.abs(pZ - this.owner.getZ()) < 2.0f) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
            return false;
        } else {
            this.ghost.moveTo(pX + 0.5F, pY, pZ + 0.5F, this.ghost.getYRot(), this.ghost.getXRot());
            this.navigation.stop();
            return true;
        }

    }

    private boolean canTeleportTo(BlockPos pPos) {
        if (FlyNodeEvaluator.getBlockPathTypeStatic(this.ghost.level(), pPos.mutable()) != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            if ( this.ghost.level().getBlockState(pPos.below()).getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                return ghost.level().noCollision(this.ghost, this.ghost.getBoundingBox().move(pPos.subtract(this.ghost.blockPosition())));
            }
        }

    }

    private int randomIntInclusive(int pMin, int pMax) {
        return this.ghost.getRandom().nextInt(pMax - pMin + 1) + pMin;
    }

}