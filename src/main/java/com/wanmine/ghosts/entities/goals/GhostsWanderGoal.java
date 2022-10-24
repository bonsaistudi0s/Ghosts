package com.wanmine.ghosts.entities.goals;

import com.wanmine.ghosts.entities.SmallGhostEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GhostsWanderGoal extends WaterAvoidingRandomStrollGoal {
    private final Level level;
    private final TamableAnimal animal;

    public GhostsWanderGoal(TamableAnimal animal, double speedModifier) {
        super(animal, speedModifier, 0.01F);
        this.animal = animal;
        this.interval = 3 * 20;
        this.level = animal.level;
    }

    @Override
    public boolean canUse() {
        if (this.animal instanceof SmallGhostEntity ghost && ghost.getIsSleeping())
            return false;

        this.animal.setNoActionTime(0);
        return super.canUse();
    }

    @Nullable
    protected Vec3 getPosition() {
        Vec3 vec3 = null;
        if (this.mob.isInWater()) {
            vec3 = LandRandomPos.getPos(this.mob, 15, 15);
        }

        if (this.mob.getRandom().nextFloat() >= 0.5) {
            vec3 = this.getTreePos();
        }

        return vec3 == null ? this.getFlyingPosition() : vec3;
    }

    @Nullable
    protected Vec3 getFlyingPosition() {
        Vec3 vec3 = this.mob.getViewVector(0.0F);
        int i = 8;
        Vec3 vec31 = HoverRandomPos.getPos(this.mob, 8, 7, vec3.x, vec3.z, ((float) Math.PI / 2F), 3, 1);
        return vec31 != null ? vec31 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, vec3.x, vec3.z, (double) ((float) Math.PI / 2F));
    }

    @Nullable
    private Vec3 getTreePos() {
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos1 : BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0D), Mth.floor(this.mob.getY() - 6.0D), Mth.floor(this.mob.getZ() - 3.0D), Mth.floor(this.mob.getX() + 3.0D),
                Mth.floor(this.mob.getY() + 6.0D), Mth.floor(this.mob.getZ() + 3.0D))) {
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
