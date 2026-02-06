package dev.xylonity.bonsai.ghosts.common.block;

import dev.xylonity.bonsai.ghosts.common.blockentity.HauntedSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class HauntedStandingSignBlock extends StandingSignBlock {

    public HauntedStandingSignBlock(WoodType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HauntedSignBlockEntity(pos, state);
    }

}
