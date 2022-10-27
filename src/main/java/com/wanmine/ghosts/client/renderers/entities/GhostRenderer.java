package com.wanmine.ghosts.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.models.entities.GhostModel;
import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.variants.GhostVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.geo.render.built.GeoBone;

public class GhostRenderer extends BaseGhostRenderer<GhostEntity> {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Ghosts.MODID, "textures/entity/ghost.png");

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostModel());
        // this.addLayer(new GhostGlowLayer<>(this));
    }

    @Override
    protected ItemStack getHeldItemStack() {
        return this.ghostEntity.getHoldItem();
    }

    @Override
    protected void setupHeldItemRender(PoseStack poseStack, GeoBone bone) {
        // poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if ("plant".equals(bone.getName()) && this.ghostEntity.getVariant() != GhostVariant.MUSHROOM)
            return;

        super.renderRecursively(bone, poseStack, vertexConsumer, packedLight, packedOverlayIn, red, green, blue, alpha);
    }
}
