package dev.xylonity.bonsai.ghosts.common.blockentity;

import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HauntedSignBlockEntity extends SignBlockEntity {

    public HauntedSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(GhostsBlockEntities.HAUNTED_SIGN.get(), pos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return GhostsBlockEntities.HAUNTED_SIGN.get();
    }

}
