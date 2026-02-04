package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.block.HauntedDoor;
import dev.xylonity.bonsai.ghosts.common.block.HauntedSapling;
import dev.xylonity.bonsai.ghosts.configurations.tree.HauntedTreeGrower;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.Supplier;

public class GhostsBlocks {

    public static void init() { ;; }

    public static final Supplier<Block> HAUNTED_PLANKS = register("haunted_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final Supplier<Block> HAUNTED_LOG = register("haunted_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG)));
    public static final Supplier<Block> HAUNTED_LEAVES = register("haunted_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES)));
    public static final Supplier<Block> HAUNTED_EYE_LOG = register("haunted_eye_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG)));
    public static final Supplier<Block> HAUNTED_DOOR = register("haunted_door", () -> new HauntedDoor(BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR), BlockSetType.ACACIA));

    public static final Supplier<Block> HAUNTED_SAPLING = register("haunted_sapling", () -> new HauntedSapling(new HauntedTreeGrower(), BlockBehaviour.Properties.copy(Blocks.ACACIA_SAPLING)));

    private static <T extends Block> Supplier<T> register(String id, Supplier<T> block) {
        return Ghosts.PLATFORM.registerBlock(id, block);
    }

}
