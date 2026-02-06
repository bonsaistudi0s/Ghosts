package dev.xylonity.bonsai.ghosts.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.bonsai.ghosts.common.blockentity.HauntedSignBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;

public final class HauntedSignRenderer implements BlockEntityRenderer<HauntedSignBlockEntity> {

    private final SignRenderer delegate;

    public HauntedSignRenderer(BlockEntityRendererProvider.Context ctx) {
        this.delegate = new SignRenderer(ctx);
    }

    @Override
    public void render(HauntedSignBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        delegate.render(be, partialTick, poseStack, buffer, packedLight, packedOverlay);
    }

}
