package dev.xylonity.bonsai.ghosts.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.bonsai.ghosts.common.blockentity.HauntedHangingSignBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;

public final class HauntedHangingSignRenderer implements BlockEntityRenderer<HauntedHangingSignBlockEntity> {

    private final HangingSignRenderer delegate;

    public HauntedHangingSignRenderer(BlockEntityRendererProvider.Context context) {
        this.delegate = new HangingSignRenderer(context);
    }

    @Override
    public void render(HauntedHangingSignBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        delegate.render(be, partialTick, poseStack, buffer, packedLight, packedOverlay);
    }

}
