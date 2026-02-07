package dev.xylonity.bonsai.ghosts.mixin;

import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "getStripped", at = @At("HEAD"), cancellable = true)
    private void ghosts$getStripped(BlockState unstrippedState, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (unstrippedState.getBlock() != GhostsBlocks.HAUNTED_EYE_LOG.get()) {
            return;
        }

        BlockState stripped = GhostsBlocks.STRIPPED_HAUNTED_LOG.get().defaultBlockState();

        if (stripped.hasProperty(RotatedPillarBlock.AXIS)) {
            stripped = stripped.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        }

        if (unstrippedState.hasProperty(HorizontalDirectionalBlock.FACING) && stripped.hasProperty(HorizontalDirectionalBlock.FACING)) {
            stripped = stripped.setValue(HorizontalDirectionalBlock.FACING, unstrippedState.getValue(HorizontalDirectionalBlock.FACING));
        }

        cir.setReturnValue(Optional.of(stripped));
    }

}
