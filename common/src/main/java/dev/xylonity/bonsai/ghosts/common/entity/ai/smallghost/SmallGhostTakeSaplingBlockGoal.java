package dev.xylonity.bonsai.ghosts.common.entity.ai.smallghost;

import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SmallGhostTakeSaplingBlockGoal extends Goal {

    private final SmallGhostEntity ghost;
    private final int scanRadius;
    private final double speed;
    private final float lerp;

    private int nextScanTick;

    private BlockPos targetPos;

    public SmallGhostTakeSaplingBlockGoal(SmallGhostEntity ghost, int scanRadius, double speed, float lerp) {
        this.ghost = ghost;
        this.scanRadius = Math.max(2, scanRadius);
        this.speed = speed;
        this.lerp = lerp;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (ghost.level().isClientSide) {
            return false;
        }
        if (!ghost.getHoldItem().isEmpty()) {
            return false;
        }
        if (ghost.getIsSleeping()) {
            return false;
        }

        if (ghost.tickCount < nextScanTick) {
            return false;
        }
        nextScanTick = ghost.tickCount + 20;

        targetPos = findSaplingBlock();
        return targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (ghost.level().isClientSide) {
            return false;
        }
        if (!ghost.getHoldItem().isEmpty()) {
            return false;
        }
        if (ghost.getIsSleeping()) {
            return false;
        }
        if (targetPos == null) {
            return false;
        }

        BlockState st = ghost.level().getBlockState(targetPos);
        if (!st.is(BlockTags.SAPLINGS)) {
            return false;
        }

        double max = (scanRadius + 2.0D);
        return ghost.position().distanceToSqr(Vec3.atCenterOf(targetPos)) <= max * max;
    }

    @Override
    public void start() {
        ghost.noPhysics = false;

        if (ghost.getIsSleeping()) {
            ghost.setIsSleeping(false);
        }
        if (ghost.getCdFullHide() > 0) {
            ghost.setCdFullHide(0);
        }

    }

    @Override
    public void stop() {
        targetPos = null;
        ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.6));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (targetPos == null) {
            stop();
            return;
        }

        BlockState blockState = ghost.level().getBlockState(targetPos);
        if (!blockState.is(BlockTags.SAPLINGS)) {
            stop();
            return;
        }

        Vec3 center = Vec3.atCenterOf(targetPos).add(0.0, 0.15, 0.0);
        ghost.getLookControl().setLookAt(center.x, center.y, center.z);

        double takeDistSqr = 1.35D * 1.35D;
        if (ghost.position().distanceToSqr(center) <= takeDistSqr) {
            ghost.setHoldItem(new ItemStack(blockState.getBlock().asItem()));
            ghost.level().setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
            stop();
            return;
        }

        steerSmooth(center);
    }

    private void steerSmooth(Vec3 target) {
        Vec3 delta = target.subtract(ghost.position());
        double distance = delta.length();
        if (distance < 1.0E-3) {
            return;
        }

        double slowRadius = 2.2D;
        double speed = this.speed * Mth.clamp(distance / slowRadius, 0.15D, 1.0D);

        Vec3 velocity = delta.scale(speed / distance);
        Vec3 currentVelocity = ghost.getDeltaMovement();

        ghost.setDeltaMovement(
                Mth.lerp(lerp, currentVelocity.x, velocity.x),
                Mth.lerp(lerp, currentVelocity.y, velocity.y),
                Mth.lerp(lerp, currentVelocity.z, velocity.z)
        );

    }

    private BlockPos findSaplingBlock() {
        BlockPos origin = ghost.blockPosition();

        BlockPos best = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (int i = 0; i < 48; i++) {
            int dx = ghost.getRandom().nextInt(-scanRadius, scanRadius + 1);
            int dz = ghost.getRandom().nextInt(-scanRadius, scanRadius + 1);
            int dy = ghost.getRandom().nextInt(-3, 4);

            BlockPos blockPos = origin.offset(dx, dy, dz);
            BlockState blockState = ghost.level().getBlockState(blockPos);
            if (!blockState.is(BlockTags.SAPLINGS)) continue;

            double distance = ghost.position().distanceToSqr(Vec3.atCenterOf(blockPos));
            if (distance < bestDist) {
                bestDist = distance;
                best = blockPos.immutable();
            }

        }

        return best;
    }

}