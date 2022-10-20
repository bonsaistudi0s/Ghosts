package com.wanmine.ghosts.entities.goals;

import com.wanmine.ghosts.entities.SmallGhostEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GhostsWanderGoal extends WaterAvoidingRandomFlyingGoal {
    private final Level level;
    private final TamableAnimal animal;

    public GhostsWanderGoal(TamableAnimal p_186224_, double p_186225_) {
        super(p_186224_, p_186225_);

        this.animal = p_186224_;

        this.level = p_186224_.level;
    }

    @Override
    public boolean canUse() {
        if (animal instanceof SmallGhostEntity ghost) {
            if (ghost.getIsSleeping())
                return false;
        }

        if (this.mob.isVehicle()) {
            return false;
        } else {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @javax.annotation.Nullable
    protected Vec3 getPosition() {
        Vec3 vec3 = null;
        if (this.mob.isInWater()) {
            vec3 = LandRandomPos.getPos(this.mob, 15, 15);
        }

        if (this.mob.getRandom().nextFloat() >= 0.5) {
            vec3 = this.getTreePos();
        }

        return vec3 == null ? super.getPosition() : vec3;
    }

    @javax.annotation.Nullable
    private Vec3 getTreePos() {
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();

        for(BlockPos blockpos1 : BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0D), Mth.floor(this.mob.getY() - 6.0D), Mth.floor(this.mob.getZ() - 3.0D), Mth.floor(this.mob.getX() + 3.0D), Mth.floor(this.mob.getY() + 6.0D), Mth.floor(this.mob.getZ() + 3.0D))) {
            if (!blockpos.equals(blockpos1)) {
                BlockState blockstate = this.mob.level.getBlockState(pos1.setWithOffset(blockpos1, Direction.DOWN));
                boolean flag = !blockstate.is(Blocks.LAVA) || !blockstate.is(Blocks.WATER) || !blockstate.is(Blocks.FIRE) || checkLight(blockpos1);
                if (flag && isEmptyBlock(blockpos1) && isEmptyBlock(pos.setWithOffset(blockpos1, Direction.UP))) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }
        }

        return null;
    }

    boolean isEmptyBlock(BlockPos p_46860_) {
        return level.getBlockState(p_46860_).isAir() || level.getBlockState(p_46860_).getBlock() == Blocks.GRASS;
    }

    private boolean checkLight(BlockPos pos) {
        if (animal.isTame()) {
            return level.getBrightness(LightLayer.BLOCK, pos) <= 4;
        } else {
            return true;
        }
    }
}
