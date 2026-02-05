package dev.xylonity.bonsai.ghosts.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.bonsai.ghosts.client.entity.layer.KodamaGlowLayer;
import dev.xylonity.bonsai.ghosts.client.entity.model.KodamaModel;
import dev.xylonity.bonsai.ghosts.client.entity.render.core.BaseGhostRenderer;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;

public class KodamaRenderer extends GeoEntityRenderer<KodamaEntity> {

    public KodamaRenderer(EntityRendererProvider.Context context) {
        super(context, new KodamaModel());
        this.renderLayers.addLayer(new KodamaGlowLayer(this));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, KodamaEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        ItemStack stack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);

        // If the item in hand isn't empty ('item' bone is a dummy anchor)
        if (!stack.isEmpty() && bone.getName().equals("item")) {
            poseStack.pushPose();
            this.moveToBone(poseStack, bone);

            poseStack.scale(0.4F, 0.4F, 0.4F);

            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
            poseStack.mulPose(Axis.XN.rotationDegrees(140));

            poseStack.translate(0, -0.3, 0);

            // Item cast
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.level(), 0);

            poseStack.popPose();

            // Important to reassign the buffer as renderStatic uses its own buffers and it's needed to align the consumer to the actual rendertype
            buffer = bufferSource.getBuffer(renderType);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void moveToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.getPosX() / 16, bone.getPosY() / 16, bone.getPosZ() / 16);
        RenderUtils.rotateMatrixAroundBone(poseStack, bone);
        poseStack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
    }

}
