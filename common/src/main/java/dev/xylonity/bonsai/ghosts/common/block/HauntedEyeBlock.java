package dev.xylonity.bonsai.ghosts.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

public class HauntedEyeBlock extends HorizontalDirectionalBlock {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public static final MapCodec<HorizontalDirectionalBlock> CODEC = RecordCodecBuilder.mapCodec(blockInstance -> blockInstance.group(propertiesCodec()).apply(blockInstance, HauntedEyeBlock::new));

    public HauntedEyeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(POWERED, false)
                        .setValue(FACING, Direction.NORTH)
        );

    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean shouldBePowered = hasEntityInFront(level, pos, state);
        boolean isPowered = state.getValue(POWERED);

        if (shouldBePowered != isPowered) {
            level.setBlock(pos, state.setValue(POWERED, shouldBePowered), 3);
            updateNeighbors(level, pos, state);
        }

        level.scheduleTick(pos, this, 1);
    }

    private boolean hasEntityInFront(Level level, BlockPos pos, BlockState state) {
        final Direction facing = state.getValue(FACING);

        final BlockPos frontPos = pos.relative(facing).relative(facing);
        final AABB detectionArea = new AABB(frontPos).inflate(1.0);

        return !level.getEntities(null, detectionArea).isEmpty();
    }

    private void updateNeighbors(Level level, BlockPos pos, BlockState state) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
    }

}
