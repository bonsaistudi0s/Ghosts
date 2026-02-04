package dev.xylonity.bonsai.ghosts.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SmallGhostGlowLayer extends GeoRenderLayer<SmallGhostEntity> {

    public SmallGhostGlowLayer(GeoRenderer<SmallGhostEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, SmallGhostEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.entityTranslucentEmissive(new ResourceLocation(Ghosts.MOD_ID, "textures/entity/small_ghost_glowmask.png"));
        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowRenderType, glowBuffer, partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
    }

}