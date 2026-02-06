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

    public static final Supplier<Block> HAUNTED_PLANKS = register("haunted_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final Supplier<Block> HAUNTED_STAIRS = register("haunted_stairs", () -> new HauntedStair(Blocks.ACACIA_STAIRS.defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.ACACIA_STAIRS)));
    public static final Supplier<Block> HAUNTED_SLAB = register("haunted_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_SLAB)));
    public static final Supplier<Block> HAUNTED_LOG = register("haunted_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG)));
    public static final Supplier<Block> STRIPPED_HAUNTED_LOG = register("stripped_haunted_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_LOG)));
    public static final Supplier<Block> HAUNTED_TRAPDOOR = register("haunted_trapdoor", () -> new HauntedTrapdoor(BlockBehaviour.Properties.copy(Blocks.ACACIA_TRAPDOOR), BlockSetType.ACACIA));
    public static final Supplier<Block> HAUNTED_LEAVES = register("haunted_leaves", () -> new HauntedLeaves(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES).lightLevel(value -> 4)));
    public static final Supplier<Block> HAUNTED_BUTTON = register("haunted_button", () -> new HauntedButton(BlockBehaviour.Properties.copy(Blocks.ACACIA_BUTTON), BlockSetType.ACACIA, 20, false));
    public static final Supplier<Block> HAUNTED_PRESSURE_PLATE = register("haunted_pressure_plate", () -> new HauntedPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().mapColor(Blocks.ACACIA_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY), BlockSetType.ACACIA));
    public static final Supplier<Block> HAUNTED_EYE_LOG = register("haunted_eye_log", () -> new HauntedEyeBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS).lightLevel(value -> 3)));
    public static final Supplier<Block> CALIBRATED_HAUNTED_EYE = register("calibrated_haunted_eye", () -> new CalibratedHauntedEyeBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final Supplier<Block> HAUNTED_DOOR = register("haunted_door", () -> new HauntedDoor(BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR), BlockSetType.ACACIA));

    public static final Supplier<Block> HAUNTED_SIGN = register("haunted_sign", () -> new HauntedStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_SIGN), GhostsWoodTypes.HAUNTED), false);
    public static final Supplier<Block> HAUNTED_HANGING_SIGN = register("haunted_hanging_sign", () -> new HauntedHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_HANGING_SIGN), GhostsWoodTypes.HAUNTED), false);
    public static final Supplier<Block> HAUNTED_WALL_SIGN = register("haunted_wall_sign", () -> new HauntedWallSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WALL_SIGN), GhostsWoodTypes.HAUNTED), false);
    public static final Supplier<Block> HAUNTED_WALL_HANGING_SIGN = register("haunted_wall_hanging_sign", () -> new HauntedWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WALL_HANGING_SIGN), GhostsWoodTypes.HAUNTED), false);

    public static final Supplier<Block> HAUNTED_FENCE = register("haunted_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final Supplier<Block> HAUNTED_FENCE_GATE = register("haunted_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS), WoodType.ACACIA));

    public static final Supplier<Block> HAUNTED_SAPLING = register("haunted_sapling", () -> new HauntedSapling(new HauntedTreeGrower(), BlockBehaviour.Properties.copy(Blocks.ACACIA_SAPLING)));

    private static <T extends Block> Supplier<T> register(String id, Supplier<T> block) {
        return Ghosts.PLATFORM.registerBlock(id, block, true);
    }

    private static <T extends Block> Supplier<T> register(String id, Supplier<T> block, boolean registerItem) {
        return Ghosts.PLATFORM.registerBlock(id, block, registerItem);
    }

}
