package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.blockentity.CalibratedHauntedEyeBlockEntity;
import dev.xylonity.bonsai.ghosts.common.blockentity.HauntedHangingSignBlockEntity;
import dev.xylonity.bonsai.ghosts.common.blockentity.HauntedSignBlockEntity;
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

    public static final Supplier<BlockEntityType<HauntedSignBlockEntity>> HAUNTED_SIGN =
            register("haunted_sign",
                    HauntedSignBlockEntity::new,
                    GhostsBlocks.HAUNTED_SIGN,
                    GhostsBlocks.HAUNTED_WALL_SIGN
            );

    public static final Supplier<BlockEntityType<HauntedHangingSignBlockEntity>> HAUNTED_HANGING_SIGN =
            register("haunted_hanging_sign",
                    HauntedHangingSignBlockEntity::new,
                    GhostsBlocks.HAUNTED_HANGING_SIGN,
                    GhostsBlocks.HAUNTED_WALL_HANGING_SIGN
            );

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String id, BlockEntityFactory<T> supplier, Supplier<Block>... blocks) {
        return Ghosts.PLATFORM.registerBlockEntity(id, supplier, blocks);
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

}
