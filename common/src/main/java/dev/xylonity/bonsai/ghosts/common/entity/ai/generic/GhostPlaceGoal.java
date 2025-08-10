package dev.xylonity.bonsai.ghosts.common.entity.ai.generic;

import dev.xylonity.bonsai.ghosts.common.entity.MainGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Predicate;

public class GhostPlaceGoal extends Goal {
    private final GhostEntity ghost;
    private final Ingredient placeables;
    private final Predicate<BlockState> preference;
    private final int lightThreshold;
    private final int retryCooldown;
    private final double approachSpeed;
    private final double placeRange;

    private long nextTryTick = 0L;
    private BlockPos basePos = null;
    private Vec3 hoverPos = null;
    private int stuckTicks = 0;
    private Vec3 lastPos = null;

    public GhostPlaceGoal(GhostEntity ghost, Ingredient placeables, Predicate<BlockState> pref, int lightThreshold, int retry) {
        this(ghost, placeables, pref, lightThreshold, retry, 1.0D, 0.9D);
    }

    public GhostPlaceGoal(GhostEntity ghost, Ingredient placeables, Predicate<BlockState> pref, int lightThreshold, int retry, double speed, double range) {
        this.ghost = ghost;
        this.placeables = placeables;
        this.preference = pref;
        this.lightThreshold = lightThreshold;
        this.retryCooldown = Math.max(5, retry);
        this.approachSpeed = speed;
        this.placeRange = range;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (ghost.getMainInteraction() != 1) return false;
        if (ghost.isInSittingPose() || ghost.isPassenger()) return false;
        if (ghost.tickCount < nextTryTick) return false;

        ItemStack stack = ghost.getHoldItem();
        if (stack.isEmpty() || !placeables.test(stack) || !(stack.getItem() instanceof BlockItem)) return false;

        BlockPos base = findBaseBelow(ghost.level());
        if (base == null) return false;

        Level level = ghost.level();
        BlockPos above = base.above();
        if (!preference.test(level.getBlockState(base))) return false;
        if (!level.getBlockState(above).canBeReplaced()) return false;
        if (level.getMaxLocalRawBrightness(above) > lightThreshold) return false;

        if (!((BlockItem) stack.getItem()).getBlock().defaultBlockState().canSurvive(level, above)) return false;

        basePos = base;
        hoverPos = new Vec3(basePos.getX() + 0.5D, basePos.getY() + 1.2D, basePos.getZ() + 0.5D);
        stuckTicks = 0;
        lastPos = ghost.position();

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (ghost.getMainInteraction() != 1) return false;
        if (basePos == null || hoverPos == null) return false;

        ItemStack stack = ghost.getHoldItem();
        if (stack.isEmpty() || !placeables.test(stack) || !(stack.getItem() instanceof BlockItem)) return false;

        if (!ghost.level().getBlockState(basePos.above()).canBeReplaced()) return false;
        if (ghost.level().getMaxLocalRawBrightness(basePos.above()) > lightThreshold) return false;

        return true;
    }

    @Override
    public void start() {
        if (hoverPos != null) ghost.getNavigation().moveTo(hoverPos.x, hoverPos.y, hoverPos.z, approachSpeed);
    }

    @Override
    public void stop() {
        basePos = null;
        hoverPos = null;
        lastPos = null;
        stuckTicks = 0;
        ghost.getNavigation().stop();
        nextTryTick = ghost.tickCount + retryCooldown;
    }

    @Override
    public void tick() {
        if (hoverPos == null || basePos == null) return;

        if (lastPos != null) {
            double moved = lastPos.distanceTo(ghost.position());

            if (moved < 0.02D) stuckTicks++; else stuckTicks = 0;

        }

        this.lastPos = ghost.position();
        if (stuckTicks > 10) {
            ghost.getNavigation().moveTo(hoverPos.x, hoverPos.y, hoverPos.z, approachSpeed);
            ghost.getMoveControl().setWantedPosition(hoverPos.x, hoverPos.y, hoverPos.z, approachSpeed);
            stuckTicks = 0;
        }

        ghost.getLookControl().setLookAt(hoverPos.x, hoverPos.y, hoverPos.z);

        if (ghost.position().distanceTo(hoverPos) <= placeRange) {
            tryPlace();
        }

    }

    private void tryPlace() {
        if (basePos == null) return;

        BlockPos up = basePos.above();
        ItemStack stack = ghost.getHoldItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
            stop();
            return;
        }

        if (!ghost.level().getBlockState(up).canBeReplaced() || !blockItem.getBlock().defaultBlockState().canSurvive(ghost.level(), up)) {
            stop();
            return;
        }

        ghost.level().setBlock(up, blockItem.getBlock().defaultBlockState(), 3);
        stack.shrink(1);

        if (stack.isEmpty()) {
            ghost.setHoldItem(ItemStack.EMPTY);
        }

        stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private BlockPos findBaseBelow(Level level) {
        int gx = (int) Math.floor(ghost.getX());
        int gz = (int) Math.floor(ghost.getZ());
        int gy = (int) Math.floor(ghost.getY());

        for (int dy = 0; dy <= 3; dy++) {
            BlockPos p = new BlockPos(gx, gy - 1 - dy, gz);
            if (!level.getBlockState(p).isAir()) return p;
        }

        return null;
    }

}
