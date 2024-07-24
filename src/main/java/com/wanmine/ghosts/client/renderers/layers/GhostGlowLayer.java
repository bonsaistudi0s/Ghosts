package com.wanmine.ghosts.client.renderers.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanmine.ghosts.client.renderers.entities.BaseGhostRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class GhostGlowLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
    private final BaseGhostRenderer<T> renderer;

    public GhostGlowLayer(BaseGhostRenderer<T> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        var geoModel = this.renderer.getGeoModel();
        geoModel.getBone("glow").ifPresent(bone -> {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

            poseStack.pushPose();

            RenderUtils.translateToPivotPoint(poseStack, bone.getParent());
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            for (GeoCube cube : bone.getCubes()) {
                poseStack.pushPose();
                this.renderer.renderCube(poseStack, cube, vertexConsumer, packedLight, renderer.getPackedOverlay(animatable, 0), 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }

            poseStack.popPose();
        });
    }
}
