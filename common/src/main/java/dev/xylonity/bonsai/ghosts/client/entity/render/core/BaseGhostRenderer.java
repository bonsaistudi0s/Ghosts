package dev.xylonity.bonsai.ghosts.client.entity.render.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

import javax.annotation.Nullable;

public class BaseGhostRenderer<T extends LivingEntity & GeoEntity> extends GeoEntityRenderer<T> {

    protected BaseGhostRenderer(EntityRendererProvider.Context context, GeoModel<T> modelProvider) {
        super(context, modelProvider);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        ItemStack stack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);

        // If the item in hand isn't empty ('item' bone is a dummy anchor)
        if (!stack.isEmpty() && bone.getName().equals("item")) {
            poseStack.pushPose();
            this.moveToBone(poseStack, bone);

            poseStack.scale(0.6F, 0.6F, 0.6F);

            // Corrected direction (frontal)
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            // Item cast
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.level(), 0);

            poseStack.popPose();

            // Important to reassign the buffer as renderStatic uses its own buffers and it's needed to align the consumer to the actual rendertype
            buffer = bufferSource.getBuffer(renderType);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    protected void moveToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.getPosX() / 16, bone.getPosY() / 16, bone.getPosZ() / 16);
        RenderUtil.rotateMatrixAroundBone(poseStack, bone);
        poseStack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
    }

}
