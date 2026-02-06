package dev.xylonity.bonsai.ghosts.common.blockentity;

import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HauntedHangingSignBlockEntity extends SignBlockEntity {

    public HauntedHangingSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(GhostsBlockEntities.HAUNTED_HANGING_SIGN.get(), pos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return GhostsBlockEntities.HAUNTED_HANGING_SIGN.get();
    }

}
