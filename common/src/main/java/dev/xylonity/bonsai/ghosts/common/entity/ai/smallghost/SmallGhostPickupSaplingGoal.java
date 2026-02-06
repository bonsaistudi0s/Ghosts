package dev.xylonity.bonsai.ghosts.common.entity.ai.smallghost;

import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class SmallGhostPickupSaplingGoal extends Goal {

    private final SmallGhostEntity ghost;
    private final double speed;
    private final float lerp;
    private final int scanRadius;

    private int nextScanTick;

    private ItemEntity targetItem;

    public SmallGhostPickupSaplingGoal(SmallGhostEntity ghost, double speed, float lerp, int scanRadius) {
        this.ghost = ghost;
        this.speed = speed;
        this.lerp = lerp;
        this.scanRadius = scanRadius;
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
        if (!ghost.getHoldItem().isEmpty()) {
            return false;
        }

        if (ghost.tickCount < nextScanTick) {
            return false;
        }
        nextScanTick = ghost.tickCount + 20;

        targetItem = findNearestSaplingItem();

        return targetItem != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (ghost.level().isClientSide) {
            return false;
        }
        if (ghost.getIsSleeping()) {
            return false;
        }
        if (!ghost.getHoldItem().isEmpty()) {
            return false;
        }

        if (targetItem == null || !targetItem.isAlive()) {
            return false;
        }
        ItemStack itemStack = targetItem.getItem();
        if (itemStack.isEmpty() || !itemStack.is(ItemTags.SAPLINGS)) {
            return false;
        }

        double max = (scanRadius + 2.0D);
        return ghost.distanceToSqr(targetItem) <= max * max;
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
        targetItem = null;
        ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.6));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (targetItem == null || !targetItem.isAlive()) {
            stop();
            return;
        }

        BlockPos blockPos = targetItem.blockPosition();
        Vec3 baseTarget = Vec3.atCenterOf(blockPos).add(0.0, 0.15, 0.0);

        ghost.getLookControl().setLookAt(baseTarget.x, baseTarget.y, baseTarget.z);

        double pickDistSqr = 1.25D * 1.25D;
        if (ghost.position().distanceToSqr(baseTarget) <= pickDistSqr) {
            ItemStack stack = targetItem.getItem();
            if (!stack.isEmpty() && stack.is(ItemTags.SAPLINGS) && ghost.getHoldItem().isEmpty()) {
                ItemStack pickup = stack.copy();
                pickup.setCount(1);

                if (stack.getCount() > 1) {
                    stack.shrink(1);
                    ghost.take(targetItem, 1);
                }
                else {
                    targetItem.discard();
                }

                ghost.setHoldItem(pickup);
            }

            stop();
            return;
        }

        steerSmooth(baseTarget);
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

    private ItemEntity findNearestSaplingItem() {
        Vec3 position = ghost.position();
        AABB box = new AABB(
                position.x - scanRadius, position.y - scanRadius, position.z - scanRadius,
                position.x + scanRadius, position.y + scanRadius, position.z + scanRadius
        );

        List<ItemEntity> items = ghost.level().getEntitiesOfClass(ItemEntity.class, box,
                e -> e.isAlive() && !e.getItem().isEmpty() && e.getItem().is(ItemTags.SAPLINGS));

        if (items.isEmpty()) return null;

        return items.stream()
                .min(Comparator.comparingDouble(ghost::distanceToSqr))
                .orElse(null);
    }

}
