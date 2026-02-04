package dev.xylonity.bonsai.ghosts.registry;

import com.mojang.serialization.Codec;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.configurations.tree.HauntedFoliagePlacer;
import dev.xylonity.bonsai.ghosts.configurations.tree.HauntedTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.function.Supplier;

public class GhostsTrunkPlacerTypes {

    public static void init() { ;; }

    public static final Supplier<TrunkPlacerType<HauntedTrunkPlacer>> HAUNTED_TRUNK_PLACER = register("haunted_foliage", HauntedTrunkPlacer.CODEC);

    private static <U extends TrunkPlacer> Supplier<TrunkPlacerType<U>> register(String id, Codec<U> codec) {
        return Ghosts.PLATFORM.registerTrunkPlacer(id, codec);
    }

}
