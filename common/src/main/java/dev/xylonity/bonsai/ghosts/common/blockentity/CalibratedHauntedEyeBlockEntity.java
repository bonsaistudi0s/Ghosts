package dev.xylonity.bonsai.ghosts.common.blockentity;

import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CalibratedHauntedEyeBlockEntity extends BlockEntity {

    public CalibratedHauntedEyeBlockEntity(BlockPos pos, BlockState state) {
        super(GhostsBlockEntities.CALIBRATED_HAUNTED_EYE.get(), pos, state);
    }

}
