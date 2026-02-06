package dev.xylonity.bonsai.ghosts.common.block;

import dev.xylonity.bonsai.ghosts.common.blockentity.HauntedHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class HauntedHangingSignBlock extends CeilingHangingSignBlock {

    public HauntedHangingSignBlock(Properties properties, WoodType type) {
        super(properties, type);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HauntedHangingSignBlockEntity(pos, state);
    }
}
