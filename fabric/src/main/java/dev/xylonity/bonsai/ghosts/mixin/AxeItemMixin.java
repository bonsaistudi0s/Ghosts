package dev.xylonity.bonsai.ghosts.mixin;

import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(
            method = "evaluateNewBlockState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ghosts$stripHauntedEyeLog(Level level, BlockPos pos, Player player, BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {

        if (state.getBlock() != GhostsBlocks.HAUNTED_EYE_LOG.get()) {
            return;
        }

        BlockState stripped = GhostsBlocks.STRIPPED_HAUNTED_LOG.get().defaultBlockState();

        if (state.hasProperty(HorizontalDirectionalBlock.FACING) && stripped.hasProperty(HorizontalDirectionalBlock.FACING)) {
            stripped = stripped.setValue(HorizontalDirectionalBlock.FACING, state.getValue(HorizontalDirectionalBlock.FACING));
        }

        cir.setReturnValue(Optional.of(stripped));
    }

}
