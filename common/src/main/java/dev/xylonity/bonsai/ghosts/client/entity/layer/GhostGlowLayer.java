package dev.xylonity.bonsai.ghosts.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.util.GhostsColor;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GhostGlowLayer extends GeoRenderLayer<GhostEntity> {

    public GhostGlowLayer(GeoRenderer<GhostEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, GhostEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.entityTranslucentEmissive(Ghosts.of("textures/entity/ghost_glowmask.png"));
        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowRenderType, glowBuffer, partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, new GhostsColor(1, 1, 1, 1).toInt());
    }

}