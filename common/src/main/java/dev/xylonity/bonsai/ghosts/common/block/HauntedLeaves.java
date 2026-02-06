package dev.xylonity.bonsai.ghosts.common.block;

import dev.xylonity.bonsai.ghosts.registry.GhostsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HauntedLeaves extends LeavesBlock {

    public HauntedLeaves(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.9995) {
            return;
        }

        double x = (pos.getX() + 0.5) - 2 + random.nextDouble() * 4;
        double y = (pos.getY() + 0.5) - 1 + random.nextDouble() * 2;
        double z = (pos.getZ() + 0.5) - 2 + random.nextDouble() * 4;

        level.addParticle(GhostsParticles.FLYING_GHOST.get(), x, y, z, 0.0, 0.0, 0.0);

        super.animateTick(state, level, pos, random);
    }

}
