package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.configurations.tree.GhostsTreeConfigurationBuilder;
import dev.xylonity.bonsai.ghosts.configurations.tree.HauntedFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class GhostsConfiguredFeatures {

    public static void init() { ;; }

    public static final ResourceKey<ConfiguredFeature<?, ?>> HAUNTED_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Ghosts.of("haunted_tree"));

    public static void register(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(HAUNTED_TREE, new ConfiguredFeature<>(Feature.TREE,
                new GhostsTreeConfigurationBuilder(
                        BlockStateProvider.simple(GhostsBlocks.HAUNTED_LOG.get()),
                        new StraightTrunkPlacer(5, 0, 1),
                        BlockStateProvider.simple(GhostsBlocks.HAUNTED_LEAVES.get()),
                        new HauntedFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).build())
        );
    }

}
