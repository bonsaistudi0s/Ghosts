package dev.xylonity.bonsai.ghosts.registry;

import com.mojang.serialization.Codec;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.configurations.tree.HauntedFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import java.util.function.Supplier;

public class GhostsFoliagePlacers {

    public static void init() { ;; }

    public static final Supplier<FoliagePlacerType<HauntedFoliagePlacer>> HAUNTED_FOLIAGE = register("haunted_foliage", HauntedFoliagePlacer.CODEC);

    private static <U extends FoliagePlacer> Supplier<FoliagePlacerType<U>> register(String id, Codec<U> codec) {
        return Ghosts.PLATFORM.registerFoliagePlacer(id, codec);
    }

}
