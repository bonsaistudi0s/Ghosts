package dev.xylonity.bonsai.ghosts.configurations.tree;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
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

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

public class HauntedTrunkPlacer extends StraightTrunkPlacer {

    public static final Codec<HauntedTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> trunkPlacerParts(instance).apply(instance, HauntedTrunkPlacer::new));

    private boolean hasSpawnedEye;

    public HauntedTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
        this.hasSpawnedEye = false;
    }

    @Override
    protected @Nonnull TrunkPlacerType<?> type() {
        return GhostsTrunkPlacerTypes.HAUNTED_TRUNK_PLACER.get();
    }

    @Override
    public @Nonnull List<FoliagePlacer.FoliageAttachment> placeTrunk(@Nonnull LevelSimulatedReader level, @Nonnull BiConsumer<BlockPos, BlockState> blockSetter, @Nonnull RandomSource random, int freeTreeHeight, BlockPos pos, @Nonnull TreeConfiguration config) {
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
