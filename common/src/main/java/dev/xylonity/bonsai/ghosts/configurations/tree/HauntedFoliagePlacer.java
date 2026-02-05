package dev.xylonity.bonsai.ghosts.configurations.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.bonsai.ghosts.registry.GhostsFoliagePlacers;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class HauntedFoliagePlacer extends BlobFoliagePlacer {

    public static final Codec<HauntedFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) ->
            blobParts(instance).apply(instance, HauntedFoliagePlacer::new)
    );

    public HauntedFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset, height);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return GhostsFoliagePlacers.HAUNTED_FOLIAGE.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter blockSetter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
        int baseRadius = foliageRadius + attachment.radiusOffset();
        this.placeLeavesRow(level, blockSetter, random, config, attachment.pos(), baseRadius, offset, attachment.doubleTrunk());
        this.placeLeavesRow(level, blockSetter, random, config, attachment.pos(), baseRadius, offset + 1, attachment.doubleTrunk());

        int topRadius = Math.max(baseRadius - 1, 0);
        this.placeLeavesRow(level, blockSetter, random, config, attachment.pos(), topRadius, offset + 2, attachment.doubleTrunk());

        BlockPos centerPos = attachment.pos();
        for (int i = offset; i <= offset + 1; ++i) {
            BlockPos layerPos = centerPos.above(i);
            for (int x = -baseRadius; x <= baseRadius; ++x) {
                for (int z = -baseRadius; z <= baseRadius; ++z) {
                    int distance = Math.max(Math.abs(x), Math.abs(z));
                    if (distance == baseRadius) {
                        BlockPos leafPos = layerPos.offset(x, 0, z);
                        if (random.nextFloat() < 0.25f) {
                            if (level.isStateAtPosition(leafPos, state -> state.is(config.foliageProvider.getState(random, leafPos).getBlock()))) {
                                int hangingLength = 1 + random.nextInt(2);
                                for (int h = 1; h <= hangingLength; ++h) {
                                    BlockPos hangingPos = leafPos.below(h);
                                    if (level.isStateAtPosition(hangingPos, BlockBehaviour.BlockStateBase::isAir)) {
                                        blockSetter.set(hangingPos, GhostsBlocks.HAUNTED_LEAVES.get().defaultBlockState());
                                    }
                                    else {
                                        break;
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

    }

}