package dev.xylonity.bonsai.ghosts.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.bonsai.ghosts.common.blockentity.CalibratedHauntedEyeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class CalibratedHauntedEyeBlock extends DirectionalBlock implements EntityBlock {

    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    private static final double MAX_DETECTION_DISTANCE = 15.0;

    public static final MapCodec<DirectionalBlock> CODEC = RecordCodecBuilder.mapCodec(blockInstance -> blockInstance.group(propertiesCodec()).apply(blockInstance, CalibratedHauntedEyeBlock::new));

    public CalibratedHauntedEyeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWER, 0));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getNearestLookingDirection().getOpposite().getOpposite())
                .setValue(POWER, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int newPower = calculatePowerLevel(level, pos, state);
        int currentPower = state.getValue(POWER);

        if (newPower != currentPower) {
            level.setBlock(pos, state.setValue(POWER, newPower), 3);
            updateNeighbors(level, pos, state);
        }

        level.scheduleTick(pos, this, 1);
    }

    private int calculatePowerLevel(Level level, BlockPos pos, BlockState state) {
        final Direction facing = state.getValue(FACING);
        final Vec3 eyePos = Vec3.atCenterOf(pos);
        final Vec3 lookDirection = Vec3.atLowerCornerOf(facing.getNormal());

        final List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, createSearchArea(pos, facing), entity -> !entity.isSpectator());

        if (entities.isEmpty()) {
            return 0;
        }

        double closestDistance = MAX_DETECTION_DISTANCE + 1;
        
        for (Entity entity : entities) {
            Vec3 entityPos = entity.position();
            Vec3 toEntity = entityPos.subtract(eyePos);

            double dotProduct = toEntity.normalize().dot(lookDirection);
            if (dotProduct > 0.5) {
                double distance = eyePos.distanceTo(entityPos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                }
            }

        }

        if (closestDistance > MAX_DETECTION_DISTANCE) {
            return 0;
        }

        int power = (int) Math.ceil(15 - (closestDistance / MAX_DETECTION_DISTANCE * 14));
        return Math.max(1, Math.min(15, power));
    }

    private AABB createSearchArea(BlockPos pos, Direction facing) {
        Vec3 center = Vec3.atCenterOf(pos);

        final double expand = 3;
        return switch (facing) {
            case NORTH -> new AABB(
                    center.x - expand, center.y - expand, center.z - MAX_DETECTION_DISTANCE,
                    center.x + expand, center.y + expand, center.z
            );
            case SOUTH -> new AABB(
                    center.x - expand, center.y - expand, center.z,
                    center.x + expand, center.y + expand, center.z + MAX_DETECTION_DISTANCE
            );
            case WEST -> new AABB(
                    center.x - MAX_DETECTION_DISTANCE, center.y - expand, center.z - expand,
                    center.x, center.y + expand, center.z + expand
            );
            case EAST -> new AABB(
                    center.x, center.y - expand, center.z - expand,
                    center.x + MAX_DETECTION_DISTANCE, center.y + expand, center.z + expand
            );
            case UP -> new AABB(
                    center.x - expand, center.y, center.z - expand,
                    center.x + expand, center.y + MAX_DETECTION_DISTANCE, center.z + expand
            );
            case DOWN -> new AABB(
                    center.x - expand, center.y - MAX_DETECTION_DISTANCE, center.z - expand,
                    center.x + expand, center.y, center.z + expand
            );

        };

    }

    private void updateNeighbors(Level level, BlockPos pos, BlockState state) {
        Direction out = state.getValue(FACING);
        BlockPos target = pos.relative(out);
        level.neighborChanged(target, this, pos);
        level.updateNeighborsAtExceptFromFacing(target, this, out.getOpposite());
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return side == state.getValue(FACING) ? state.getValue(POWER) : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return getSignal(state, level, pos, side);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CalibratedHauntedEyeBlockEntity(blockPos, blockState);
    }

}