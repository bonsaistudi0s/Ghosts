package dev.xylonity.bonsai.ghosts.common.entity.ai.generic;

import dev.xylonity.bonsai.ghosts.common.entity.MainGhostEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GhostWanderGoal extends Goal {

    protected final MainGhostEntity ghost;
    protected double wantedX, wantedY, wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;
    private final float lerp;

    private Vec3 targetPos;

    public GhostWanderGoal(MainGhostEntity ghost, double speedModifier, int interval, boolean checkNoActionTime, float lerpFactor) {
        this.ghost = ghost;
        this.speedModifier = speedModifier;
        this.interval = interval;
        this.checkNoActionTime = checkNoActionTime;
        this.lerp = lerpFactor;
        this.forceTrigger = false;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public GhostWanderGoal(MainGhostEntity ghost, double speedModifier) {
        this(ghost, speedModifier, 120, false, 0.1f);
    }

    @Override
    public boolean canUse() {
        if (ghost.isTame() && ghost.getMainInteraction() != 2) return false;
        if (ghost.isVehicle() || ghost.isInSittingPose()) return false;

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
        var rand = ghost.getRandom();

        double horizontalRange = 8.0;
        double verticalRange = 4.0;

        for (int attempt = 0; attempt < 12; attempt++) {
            double dx = (rand.nextDouble() * 2 - 1) * horizontalRange;
            double dz = (rand.nextDouble() * 2 - 1) * horizontalRange;
            double dy = (rand.nextDouble() * 2 - 1) * verticalRange;

            Vec3 c = origin.add(dx, dy, dz);

            c = new Vec3(c.x, Mth.clamp(c.y, ghost.level().getMinBuildHeight() + 1, ghost.level().getMaxBuildHeight() - 1), c.z);

            if (!isSafePosition(c)) continue;

            return c;
        }

        return null;
    }

    private boolean isSafePosition(Vec3 pos) {
        if (!ghost.level().noCollision(ghost, ghost.getBoundingBox().move(pos.x - ghost.getX(), pos.y - ghost.getY(), pos.z - ghost.getZ()))) return false;

        BlockPos min = new BlockPos(Mth.floor(pos.x - ghost.getBbWidth() / 2), Mth.floor(pos.y), Mth.floor(pos.z - ghost.getBbWidth() / 2));
        BlockPos max = new BlockPos(Mth.floor(pos.x + ghost.getBbWidth() / 2), Mth.floor(pos.y + ghost.getBbHeight()), Mth.floor(pos.z + ghost.getBbWidth() / 2));

        for (BlockPos check : BlockPos.betweenClosed(min, max)) {
            BlockState state = ghost.level().getBlockState(check);

            if (!state.isAir() && state.getFluidState().isEmpty()) {
                return false;
            }
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
        if (ghost.isTame() && ghost.getMainInteraction() != 2) return false;
        if (ghost.isVehicle() || ghost.isInSittingPose() || targetPos == null) return false;

        double dx = targetPos.x - ghost.getX();
        double dy = targetPos.y - ghost.getY();
        double dz = targetPos.z - ghost.getZ();
        return (dx*dx + dy*dy + dz*dz) > 1.0;
    }

    @Override
    public void tick() {
        if (targetPos == null) return;

        ghost.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);

        Vec3 delta = targetPos.subtract(ghost.position());
        Vec3 dir = delta.scale(1.0 / delta.length());

        Vec3 vel = dir.scale(speedModifier);

        Vec3 cur = ghost.getDeltaMovement();
        double vx = net.minecraft.util.Mth.lerp(lerp, cur.x, vel.x);
        double vy = net.minecraft.util.Mth.lerp(lerp, cur.y, vel.y);
        double vz = net.minecraft.util.Mth.lerp(lerp, cur.z, vel.z);

        ghost.setDeltaMovement(vx, vy, vz);
    }

    @Override
    public void stop() {
        ghost.noPhysics = false;
        this.targetPos = null;
    }

}