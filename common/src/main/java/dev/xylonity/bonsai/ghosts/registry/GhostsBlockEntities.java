package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.blockentity.CalibratedHauntedEyeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class GhostsBlockEntities {

    public static void init() { ;; }

    public static final Supplier<BlockEntityType<CalibratedHauntedEyeBlockEntity>> CALIBRATED_HAUNTED_EYE =
            register("calibrated_haunted_eye",
                    CalibratedHauntedEyeBlockEntity::new,
                    GhostsBlocks.CALIBRATED_HAUNTED_EYE
            );

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String id, BlockEntityFactory<T> supplier, Supplier<Block> block) {
        return Ghosts.PLATFORM.registerBlockEntity(id, supplier, block);
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

}
