package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.block.*;
import dev.xylonity.bonsai.ghosts.configurations.tree.HauntedTreeGrower;
import dev.xylonity.bonsai.ghosts.tag.GhostsWoodTypes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

public class GhostsBlocks {

    public static void init() { ;; }

    public static final Supplier<Block> HAUNTED_PLANKS = register("haunted_planks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS)));
    public static final Supplier<Block> HAUNTED_STAIRS = register("haunted_stairs", () -> new HauntedStair(Blocks.ACACIA_STAIRS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_STAIRS)));
    public static final Supplier<Block> HAUNTED_SLAB = register("haunted_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_SLAB)));
    public static final Supplier<Block> HAUNTED_LOG = register("haunted_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LOG)));
    public static final Supplier<Block> STRIPPED_HAUNTED_LOG = register("stripped_haunted_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_ACACIA_LOG)));
    public static final Supplier<Block> HAUNTED_TRAPDOOR = register("haunted_trapdoor", () -> new HauntedTrapdoor(BlockSetType.ACACIA, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_TRAPDOOR)));
    public static final Supplier<Block> HAUNTED_LEAVES = register("haunted_leaves", () -> new HauntedLeaves(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LEAVES).lightLevel(value -> 7)));
    public static final Supplier<Block> HAUNTED_BUTTON = register("haunted_button", () -> new HauntedButton(BlockSetType.ACACIA, 20, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_BUTTON)));
    public static final Supplier<Block> HAUNTED_PRESSURE_PLATE = register("haunted_pressure_plate", () -> new HauntedPressurePlateBlock(BlockSetType.ACACIA, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PRESSURE_PLATE)));
    public static final Supplier<Block> HAUNTED_EYE_LOG = register("haunted_eye_log", () -> new HauntedEyeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS).lightLevel(value -> 6)));
    public static final Supplier<Block> CALIBRATED_HAUNTED_EYE = register("calibrated_haunted_eye", () -> new CalibratedHauntedEyeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS)));
    public static final Supplier<Block> HAUNTED_DOOR = register("haunted_door", () -> new HauntedDoor(BlockSetType.ACACIA, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_DOOR)));

    public static final Supplier<Block> HAUNTED_SIGN = register("haunted_sign", () -> new HauntedStandingSignBlock(GhostsWoodTypes.HAUNTED, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_SIGN)), false);
    public static final Supplier<Block> HAUNTED_HANGING_SIGN = register("haunted_hanging_sign", () -> new HauntedHangingSignBlock(GhostsWoodTypes.HAUNTED, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_HANGING_SIGN)), false);
    public static final Supplier<Block> HAUNTED_WALL_SIGN = register("haunted_wall_sign", () -> new HauntedWallSignBlock(GhostsWoodTypes.HAUNTED, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_WALL_SIGN)), false);
    public static final Supplier<Block> HAUNTED_WALL_HANGING_SIGN = register("haunted_wall_hanging_sign", () -> new HauntedWallHangingSignBlock(GhostsWoodTypes.HAUNTED, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_WALL_HANGING_SIGN)), false);

    public static final Supplier<Block> HAUNTED_FENCE = register("haunted_fence", () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS)));
    public static final Supplier<Block> HAUNTED_FENCE_GATE = register("haunted_fence_gate", () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS)));

    public static final Supplier<Block> HAUNTED_SAPLING = register("haunted_sapling", () -> new HauntedSapling(HauntedTreeGrower.HAUNTED, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_SAPLING)));

    private static <T extends Block> Supplier<T> register(String id, Supplier<T> block) {
        return Ghosts.PLATFORM.registerBlock(id, block, true);
    }

    private static <T extends Block> Supplier<T> register(String id, Supplier<T> block, boolean registerItem) {
        return Ghosts.PLATFORM.registerBlock(id, block, registerItem);
    }

}
