package dev.xylonity.bonsai.ghosts.configurations.tree;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsTrunkPlacerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class HauntedTrunkPlacer extends StraightTrunkPlacer {

    public static final MapCodec<HauntedTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> trunkPlacerParts(instance).apply(instance, HauntedTrunkPlacer::new));

    private boolean hasSpawnedEye;

    public HauntedTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
        this.hasSpawnedEye = false;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return GhostsTrunkPlacerTypes.HAUNTED_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        setDirtAt(level, blockSetter, random, pos.below(), config);

        for (int i = 0; i < freeTreeHeight; ++i) {
            final BlockPos currentPosition = pos.above(i);

            if (random.nextFloat() < 0.2f && !hasSpawnedEye) {
                blockSetter.accept(currentPosition, GhostsBlocks.HAUNTED_EYE_LOG.get().defaultBlockState());
                this.hasSpawnedEye = true;
            }
            else {
                this.placeLog(level, blockSetter, random, currentPosition, config);
            }

        }

        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));
    }

}
