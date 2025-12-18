package dev.xylonity.bonsai.ghosts.common.entity.ai.generic;

import dev.xylonity.bonsai.ghosts.common.entity.AbstractGhostEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GhostWanderGoal extends Goal {

    protected final AbstractGhostEntity ghost;
    protected double wantedX, wantedY, wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;
    private final float lerp;

    private Vec3 targetPos;

    public GhostWanderGoal(AbstractGhostEntity ghost, double speedModifier, int interval, boolean checkNoActionTime, float lerpFactor) {
        this.ghost = ghost;
        this.speedModifier = speedModifier;
        this.interval = interval;
        this.checkNoActionTime = checkNoActionTime;
        this.lerp = lerpFactor;
        this.forceTrigger = false;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public GhostWanderGoal(AbstractGhostEntity ghost, double speedModifier) {
        this(ghost, speedModifier, 120, false, 0.1f);
    }

    @Override
    public boolean canUse() {
        if (ghost.isVehicle() || ghost.isInSittingPose()) return false;

        if (ghost.isTame()) {
            int inter = ghost.getMainInteraction();
            boolean allow = (inter == 2) || (inter == 1 && isOwnerStill());
            if (!allow) return false;
        }

        if (!forceTrigger) {
            if (checkNoActionTime && ghost.getNoActionTime() >= 100) return false;
            if (ghost.getRandom().nextInt(reducedTickDelay(interval)) != 0) return false;
        }

        Vec3 pos = getPosition();
        if (pos == null) return false;

        this.wantedX = pos.x;
        this.wantedY = pos.y;
        this.wantedZ = pos.z;
        this.forceTrigger = false;
        return true;
    }

    protected Vec3 getPosition() {
        Vec3 origin = ghost.position();

        for (int i = 0; i < 12; i++) {
            double dx = (ghost.getRandom().nextDouble() * 2 - 1) * 4;
            double dz = (ghost.getRandom().nextDouble() * 2 - 1) * 4;
            double dy = (ghost.getRandom().nextDouble() * 2 - 1);

            Vec3 c = origin.add(dx, dy, dz);
            c = new Vec3(c.x, Mth.clamp(c.y, ghost.level().getMinBuildHeight() + 1, ghost.level().getMaxBuildHeight() - 1), c.z);

            if (!isSafePosition(c)) continue;
            return c;
        }

        return null;
    }

    private boolean isSafePosition(Vec3 pos) {
        if (!ghost.level().noCollision(ghost, ghost.getBoundingBox().move(pos.x - ghost.getX(), pos.y - ghost.getY(), pos.z - ghost.getZ())))
            return false;

        BlockPos min = new BlockPos(
                Mth.floor(pos.x - ghost.getBbWidth() / 2),
                Mth.floor(pos.y),
                Mth.floor(pos.z - ghost.getBbWidth() / 2)
        );
        BlockPos max = new BlockPos(
                Mth.floor(pos.x + ghost.getBbWidth() / 2),
                Mth.floor(pos.y + ghost.getBbHeight()),
                Mth.floor(pos.z + ghost.getBbWidth() / 2)
        );

        for (BlockPos check : BlockPos.betweenClosed(min, max)) {
            BlockState state = ghost.level().getBlockState(check);
            if (!state.isAir() && state.getFluidState().isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void start() {
        this.targetPos = new Vec3(wantedX, wantedY, wantedZ);
        this.ghost.noPhysics = true;
    }

    @Override
    public boolean canContinueToUse() {
        if (ghost.isVehicle() || ghost.isInSittingPose() || targetPos == null) return false;

        if (ghost.isTame()) {
            int inter = ghost.getMainInteraction();
            boolean allow = (inter == 2) || (inter == 1 && isOwnerStill());
            if (!allow) return false;
        }

        double dx = targetPos.x - ghost.getX();
        double dy = targetPos.y - ghost.getY();
        double dz = targetPos.z - ghost.getZ();
        return (dx * dx + dy * dy + dz * dz) > 1.0;
    }

    @Override
    public void tick() {
        if (targetPos == null) return;

        ghost.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);

        Vec3 delta = targetPos.subtract(ghost.position());
        double len = delta.length();
        if (len < 1.0E-3) return;

        Vec3 dir = delta.scale(1.0 / len);
        Vec3 vel = dir.scale(speedModifier);

        Vec3 cur = ghost.getDeltaMovement();
        double vx = Mth.lerp(lerp, cur.x, vel.x);
        double vy = Mth.lerp(lerp, cur.y, vel.y);
        double vz = Mth.lerp(lerp, cur.z, vel.z);

        ghost.setDeltaMovement(vx, vy, vz);
    }

    @Override
    public void stop() {
        ghost.noPhysics = false;
        this.targetPos = null;
    }

    private boolean isOwnerStill() {
        LivingEntity owner = ghost.getOwner();
        if (owner == null) return false;
        return owner.getDeltaMovement().lengthSqr() < 0.01;
    }

}
