package dev.xylonity.bonsai.ghosts.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.ghosts.client.entity.layer.SmallGhostGlowLayer;
import dev.xylonity.bonsai.ghosts.client.entity.model.SmallGhostModel;
import dev.xylonity.bonsai.ghosts.client.entity.render.core.BaseGhostRenderer;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.variant.SmallGhostVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SmallGhostRenderer extends BaseGhostRenderer<SmallGhostEntity> {

    public SmallGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new SmallGhostModel());
        addRenderLayer(new SmallGhostGlowLayer(this));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SmallGhostEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if ("plant".equals(bone.getName()) && animatable.getVariant() == SmallGhostVariant.PLANT) {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, 1);
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, SmallGhostEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, 0.65f);
    }

}
