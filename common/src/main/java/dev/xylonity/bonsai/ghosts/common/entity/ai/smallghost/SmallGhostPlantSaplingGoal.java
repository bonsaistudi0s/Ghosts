package dev.xylonity.bonsai.ghosts.common.entity.ai.smallghost;

import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SmallGhostPlantSaplingGoal extends Goal {

    private final SmallGhostEntity ghost;
    private final double speed;
    private final float lerp;
    private final int retryCooldown;
    private final int searchRadius;

    private int nextTryTick;

    private BlockPos targetPlacePos;

    public SmallGhostPlantSaplingGoal(SmallGhostEntity ghost, double speed, float lerp, int retryCooldown, int searchRadius) {
        this.ghost = ghost;
        this.speed = speed;
        this.lerp = lerp;
        this.retryCooldown = Math.max(10, retryCooldown);
        this.searchRadius = Math.max(2, searchRadius);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (ghost.level().isClientSide) {
            return false;
        }
        if (ghost.getIsSleeping()) {
            return false;
        }
        if (ghost.tickCount < nextTryTick) {
            return false;
        }

        ItemStack held = ghost.getHoldItem();
        if (!isSaplingBlockItem(held)) {
            return false;
        }

        if (ghost.getRandom().nextInt(40) != 0) {
            return false;
        }

        BlockPos found = findPlantPos();
        if (found == null) {
            return false;
        }

        targetPlacePos = found.immutable();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (ghost.level().isClientSide) {
            return false;
        }
        if (ghost.getIsSleeping()) {
            return false;
        }
        if (targetPlacePos == null) {
            return false;
        }

        ItemStack held = ghost.getHoldItem();
        if (!isSaplingBlockItem(held)) {
            return false;
        }

        return isValidPlacement(targetPlacePos, (BlockItem) held.getItem());
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
        targetPlacePos = null;

        int desired = ghost.tickCount + retryCooldown;
        if (nextTryTick < desired) {
            nextTryTick = desired;
        }

        ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.6));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (targetPlacePos == null) {
            stop();
            return;
        }

        ItemStack held = ghost.getHoldItem();
        if (!isSaplingBlockItem(held)) {
            stop();
            return;
        }

        BlockItem blockItem = (BlockItem) held.getItem();
        if (!isValidPlacement(targetPlacePos, blockItem)) {
            stop();
            return;
        }

        Vec3 center = Vec3.atCenterOf(targetPlacePos).add(0.0, 0.15, 0.0);
        ghost.getLookControl().setLookAt(center.x, center.y, center.z);

        double placeDistSqr = 1.45D * 1.45D;
        if (ghost.position().distanceToSqr(center) <= placeDistSqr) {
            ghost.level().setBlock(targetPlacePos, blockItem.getBlock().defaultBlockState(), 3);
            held.shrink(1);
            if (held.isEmpty()) {
                ghost.setHoldItem(ItemStack.EMPTY);
            }

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

    private boolean isSaplingBlockItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ItemTags.SAPLINGS) && stack.getItem() instanceof BlockItem;
    }

    private boolean isValidPlacement(BlockPos placePos, BlockItem blockItem) {
        if (!ghost.level().getBlockState(placePos).isAir()) {
            return false;
        }

        BlockPos basePos = placePos.below();
        BlockState base = ghost.level().getBlockState(basePos);
        if (!(base.is(Blocks.GRASS_BLOCK) || base.is(Blocks.DIRT))) {
            return false;
        }

        return blockItem.getBlock().defaultBlockState().canSurvive(ghost.level(), placePos);
    }

    private BlockPos findPlantPos() {
        BlockPos origin = ghost.blockPosition();

        for (int i = 0; i < 32; i++) {
            int dx = ghost.getRandom().nextInt(-searchRadius, searchRadius + 1);
            int dz = ghost.getRandom().nextInt(-searchRadius, searchRadius + 1);

            for (int down = 0; down <= 5; down++) {
                BlockPos basePos = origin.offset(dx, -down, dz);
                BlockState base = ghost.level().getBlockState(basePos);

                if (!(base.is(Blocks.GRASS_BLOCK) || base.is(Blocks.DIRT))) {
                    continue;
                }

                BlockPos placePos = basePos.above();
                if (!ghost.level().getBlockState(placePos).isAir()) {
                    continue;
                }

                ItemStack held = ghost.getHoldItem();
                if (!(held.getItem() instanceof BlockItem blockItem)) {
                    continue;
                }

                if (!blockItem.getBlock().defaultBlockState().canSurvive(ghost.level(), placePos)) {
                    continue;
                }

                return placePos;
            }

        }

        return null;
    }

}
