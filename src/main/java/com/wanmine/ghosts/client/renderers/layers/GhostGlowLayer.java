package com.wanmine.ghosts.client.renderers.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanmine.ghosts.client.renderers.entities.BaseGhostRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public class GhostGlowLayer<T extends LivingEntity & IAnimatable> extends GeoLayerRenderer<T> {
    private final BaseGhostRenderer<T> renderer;

    public GhostGlowLayer(BaseGhostRenderer<T> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T ghost, float limbSwing, float limbSwingAmount, float partialTick,
            float ageInTicks, float netHeadYaw, float headPitch) {
        GeoModelProvider<T> modelProvider = this.renderer.getGeoModelProvider();
        GeoModel geoModel = modelProvider.getModel(modelProvider.getModelResource(ghost));
        geoModel.getBone("glow").ifPresent(bone -> {
            RenderType renderType = this.renderer.getRenderType(ghost, partialTick, poseStack, bufferSource, null, packedLight, this.renderer.getTextureLocation(ghost));
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

            poseStack.pushPose();

            RenderUtils.translate(bone.parent, poseStack);
            RenderUtils.translate(bone, poseStack);
            RenderUtils.moveToPivot(bone, poseStack);
            RenderUtils.rotate(bone, poseStack);
            RenderUtils.scale(bone, poseStack);
            RenderUtils.moveBackFromPivot(bone, poseStack);

            for (GeoCube cube : bone.childCubes) {
                poseStack.pushPose();
                this.renderer.renderCube(cube, poseStack, vertexConsumer, packedLight, renderer.getOverlay(ghost, 0), 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }

            poseStack.popPose();
        });
    }
}
