package com.wanmine.ghosts.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.models.entities.SmallGhostModel;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import com.wanmine.ghosts.entities.variants.SmallGhostVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoBone;

public class SmallGhostRenderer extends BaseGhostRenderer<SmallGhostEntity> {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost.png");

    public SmallGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new SmallGhostModel());
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if ("plant".equals(bone.getName()) && this.ghostEntity.getVariant() != SmallGhostVariant.PLANT)
            return;

        super.renderRecursively(bone, poseStack, vertexConsumer, packedLight, packedOverlayIn, red, green, blue, alpha);
    }
}
