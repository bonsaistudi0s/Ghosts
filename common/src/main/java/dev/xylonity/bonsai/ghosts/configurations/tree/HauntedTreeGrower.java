package dev.xylonity.bonsai.ghosts.configurations.tree;

import dev.xylonity.bonsai.ghosts.registry.GhostsConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class HauntedTreeGrower {

    public static final TreeGrower HAUNTED = new TreeGrower("haunted", Optional.empty(), Optional.of(GhostsConfiguredFeatures.HAUNTED_TREE), Optional.empty());

}
