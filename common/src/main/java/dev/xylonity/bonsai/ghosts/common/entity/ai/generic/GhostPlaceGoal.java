package dev.xylonity.bonsai.ghosts.common.entity.ai.generic;

import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Predicate;

public class GhostPlaceGoal extends Goal {
    private final GhostEntity ghost;
    private final Ingredient placeables;
    private final Predicate<BlockState> preference;
    private final int minLight;
    private final int retryCooldown;
    private final double approachSpeed;

    private static final double DIST_2_PLACE_TORCH = 1.0D;
    private static final double STOP_DIST = 49;
    private static final double APPROACH_BACKSLASH = 0.5D;
    private static final int IGNORE_RECENT_TORCH = 100;

    private int nextTryTickk = 0;
    private BlockPos basePos = null;
    private Vec3 approachPos = null;
    private int stuckTicks = 0;
    private Vec3 lastPos = null;

    private BlockPos recentUp = null;
    private int avoidLatest = 0;

    public GhostPlaceGoal(GhostEntity ghost, Ingredient placeables, Predicate<BlockState> pref, int lightThreshold, int retry, double speed) {
        this.ghost = ghost;
        this.placeables = placeables;
        this.preference = pref;
        this.minLight = lightThreshold;
        this.retryCooldown = Math.max(5, retry);
        this.approachSpeed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (ghost.getMainInteraction() != 1) return false;
        if (ghost.isInSittingPose() || ghost.isPassenger()) return false;
        if (ghost.tickCount < nextTryTickk) return false;

        ItemStack stack = ghost.getHoldItem();
        if (stack.isEmpty() || !placeables.test(stack) || !(stack.getItem() instanceof BlockItem)) return false;

        BlockPos base = findBlock(ghost.level());
        if (base == null) return false;

        BlockPos above = base.above();
        Level level = ghost.level();

        if (!preference.test(level.getBlockState(base))) return false;
        if (!level.getBlockState(above).canBeReplaced()) return false;
        if (level.getMaxLocalRawBrightness(above) > minLight) return false;
        if (!((BlockItem) stack.getItem()).getBlock().defaultBlockState().canSurvive(level, above)) return false;

        basePos = base;
        approachPos = new Vec3(basePos.getX() + 0.5D, basePos.getY() + 1.2D, basePos.getZ() + 0.5D);
        stuckTicks = 0;
        lastPos = ghost.position();

        return true;
    }

    private boolean isRecentlyPlacedInA(BlockPos up) {
        if (recentUp == null) return false;
        if (ghost.tickCount > avoidLatest) return false;

        int dx = Math.abs(up.getX() - recentUp.getX());
        int dy = Math.abs(up.getY() - recentUp.getY());
        int dz = Math.abs(up.getZ() - recentUp.getZ());

        return dx <= 1 && dy <= 1 && dz <= 1;
    }

    @Override
    public boolean canContinueToUse() {
        if (ghost.getMainInteraction() != 1) return false;
        if (basePos == null || approachPos == null) return false;

        ItemStack stack = ghost.getHoldItem();
        if (stack.isEmpty() || !placeables.test(stack) || !(stack.getItem() instanceof BlockItem)) return false;

        if (!ghost.level().getBlockState(basePos.above()).canBeReplaced()) return false;
        if (ghost.level().getMaxLocalRawBrightness(basePos.above()) > minLight) return false;

        if (ghost.getOwner() != null && ghost.distanceToSqr(ghost.getOwner()) > STOP_DIST) return false;

        return true;
    }

    @Override
    public void start() {
        Vec3 to = goBackAfterPlacing(approachPos);
        ghost.getNavigation().moveTo(to.x, to.y, to.z, approachSpeed);
        ghost.getMoveControl().setWantedPosition(to.x, to.y, to.z, approachSpeed);
    }

    @Override
    public void stop() {
        basePos = null;
        approachPos = null;
        lastPos = null;
        stuckTicks = 0;

        ghost.getNavigation().stop();

        nextTryTickk = ghost.tickCount + retryCooldown;
    }

    @Override
    public void tick() {
        if (basePos == null) return;

        BlockPos up = basePos.above();
        Vec3 u = new Vec3(up.getX() + 0.5, up.getY() + 0.5, up.getZ() + 0.5);

        ghost.getLookControl().setLookAt(u.x, u.y, u.z);

        if (lastPos != null) {
            if (lastPos.distanceTo(ghost.position()) < 0.02D) stuckTicks++; else stuckTicks = 0;
        }

        lastPos = ghost.position();

        Vec3 wanted = goBackAfterPlacing(approachPos);

        if (stuckTicks > 8) {
            ghost.getNavigation().moveTo(wanted.x, wanted.y, wanted.z, approachSpeed);
            ghost.getMoveControl().setWantedPosition(wanted.x, wanted.y, wanted.z, approachSpeed);
            stuckTicks = 0;
        }

        if (ghost.position().distanceTo(u) <= DIST_2_PLACE_TORCH && canSeePos(up)) {
            place(up);
            return;
        }

        if (ghost.position().distanceTo(wanted) > 0.25D) {
            ghost.getNavigation().moveTo(wanted.x, wanted.y, wanted.z, approachSpeed);
            ghost.getMoveControl().setWantedPosition(wanted.x, wanted.y, wanted.z, approachSpeed);
        }

    }

    private void place(BlockPos up) {
        ItemStack stack = ghost.getHoldItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) { stop(); return; }

        if (!ghost.level().getBlockState(up).canBeReplaced() || !blockItem.getBlock().defaultBlockState().canSurvive(ghost.level(), up)) {
            stop();
            return;
        }

        ghost.triggerAnim("torch_place_controller", "torch_place");

        ghost.level().setBlock(up, blockItem.getBlock().defaultBlockState(), 3);
        stack.shrink(1);

        if (stack.isEmpty()) ghost.setHoldItem(ItemStack.EMPTY);

        recentUp = up.immutable();
        avoidLatest = ghost.tickCount + IGNORE_RECENT_TORCH;

        stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private Vec3 goBackAfterPlacing(Vec3 target) {
        double d = target.subtract(ghost.position()).length();
        if (d < 1.0E-3) return target;

        return ghost.position().add(target.subtract(ghost.position()).scale(Math.max(0.0, d - GhostPlaceGoal.APPROACH_BACKSLASH) / d));
    }

    private boolean canSeePos(BlockPos up) {
        Vec3 target = new Vec3(up.getX() + 0.5, up.getY() + 0.5, up.getZ() + 0.5);
        HitResult hit = ghost.level().clip(new ClipContext(ghost.getEyePosition(), target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, ghost));

        if (hit.getType() == HitResult.Type.MISS) return true;
        if (hit instanceof BlockHitResult bhr) return bhr.getBlockPos().equals(up);

        return false;
    }

    private BlockPos findBlock(Level level) {
        for (int dy = 0; dy <= 4; dy++) {
            BlockPos pos = new BlockPos(Mth.floor(ghost.getX()), Mth.floor(ghost.getY()) - 1 - dy, Mth.floor(ghost.getZ()));
            if (!level.getBlockState(pos).isAir()) {
                BlockPos[] possiblePositions = new BlockPos[] { pos, pos.east(), pos.west(), pos.north(), pos.south() };
                for (BlockPos b : possiblePositions) {
                    BlockPos up = b.above();

                    if (isRecentlyPlacedInA(up)) continue;
                    if (!preference.test(level.getBlockState(b))) continue;
                    if (!level.getBlockState(up).canBeReplaced()) continue;
                    if (level.getMaxLocalRawBrightness(up) > minLight) continue;

                    return b;
                }
            }

        }

        return null;
    }

}
