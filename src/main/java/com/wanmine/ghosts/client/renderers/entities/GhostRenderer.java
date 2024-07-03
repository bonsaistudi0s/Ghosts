package com.wanmine.ghosts.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.models.entities.GhostModel;
import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.variants.GhostVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;

public class GhostRenderer extends BaseGhostRenderer<GhostEntity> {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Ghosts.MODID, "textures/entity/ghost.png");

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostModel());
        // this.addLayer(new GhostGlowLayer<>(this));
    }

    // @Override
    // protected void setupHeldItemRender(PoseStack poseStack, GeoBone bone) {
    //     // poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
    //
    //     GeoBone parent = bone.parent;
    //     // Unrotate parents
    //     while (parent != null) {
    //         float xRot = parent.getRotationX() * (180 / (float) Math.PI);
    //         float yRot = parent.getRotationY() * (180 / (float) Math.PI);
    //         float zRot = parent.getRotationZ() * (180 / (float) Math.PI);
    //         poseStack.mulPose(Vector3f.XN.rotationDegrees(xRot));
    //         poseStack.mulPose(Vector3f.YN.rotationDegrees(yRot));
    //         poseStack.mulPose(Vector3f.ZN.rotationDegrees(zRot));
    //         parent = parent.parent;
    //     }
    // }


    @Override
    public void renderRecursively(PoseStack poseStack, GhostEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if ("plant".equals(bone.getName()) && this.ghostEntity.getVariant() != GhostVariant.MUSHROOM)
            return;

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
