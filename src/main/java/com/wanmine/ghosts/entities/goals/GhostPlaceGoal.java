package com.wanmine.ghosts.entities.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class GhostPlaceGoal extends MoveToBlockGoal {
    protected final PathfinderMob mob;
    protected final Ingredient requiredHeld;
    protected final Predicate<BlockState> targetBlockPredicate;
    protected boolean placedBlock;

    public GhostPlaceGoal(PathfinderMob mob, Ingredient requiredHeld, TagKey<Block> targetBlockTag, int horizontalSearchRange, int verticalSearchRange) {
        this(mob, requiredHeld, state -> state.is(targetBlockTag), horizontalSearchRange, verticalSearchRange);
    }

    public GhostPlaceGoal(PathfinderMob mob, Ingredient requiredHeld, Predicate<BlockState> targetBlockPredicate, int horizontalSearchRange, int verticalSearchRange) {
        super(mob, 1.0D, horizontalSearchRange, verticalSearchRange);
        this.mob = mob;
        this.requiredHeld = requiredHeld;
        this.targetBlockPredicate = targetBlockPredicate;
    }

    @Override
    public boolean canUse() {
        return isHoldingValidItem() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return isHoldingValidItem() && super.canContinueToUse();
    }

    private boolean isHoldingValidItem() {
        return this.requiredHeld.test(this.mob.getItemBySlot(EquipmentSlot.MAINHAND));
    }

    @Override
    public void start() {
        super.start();
        this.placedBlock = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isReachedTarget() && !this.placedBlock) {
            this.placedBlock = true;
            ItemStack heldStack = this.mob.getItemBySlot(EquipmentSlot.MAINHAND);
            if (heldStack.getItem() instanceof BlockItem blockItem) {
                blockItem.place(new DirectionalPlaceContext(this.mob.level(), this.mob.blockPosition(), Direction.DOWN, heldStack, Direction.UP));
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.isEmptyBlock(pos.above()) && this.targetBlockPredicate.test(level.getBlockState(pos));
    }
}
