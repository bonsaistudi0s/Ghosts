package dev.xylonity.bonsai.ghosts.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import dev.xylonity.bonsai.ghosts.util.GhostsColor;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class KodamaGlowLayer extends GeoRenderLayer<KodamaEntity> {

    public KodamaGlowLayer(GeoRenderer<KodamaEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, KodamaEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "textures/entity/kodama_" + animatable.getVariant() + ".png"));
        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

        float baseAlpha = 0.525f + 0.125f * (float) Math.sin(((animatable.tickCount + partialTick) / 80f) * (float) (2 * Math.PI));

        if (animatable.getRattlingTicks() > 0 && animatable.getRattlingTicks() == 14) {
            animatable.setFlashAlpha(1);
        }

        if (animatable.getFlashAlpha() > 0f) {
            animatable.setFlashAlpha(animatable.getFlashAlpha() - 0.025f / 20f);
        }

        float alpha = Math.max(baseAlpha, animatable.getFlashAlpha());
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowRenderType, glowBuffer, partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, new GhostsColor(1, 1, 1, alpha).toInt());
    }

}