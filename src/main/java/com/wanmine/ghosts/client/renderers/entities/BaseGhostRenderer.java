package com.wanmine.ghosts.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public abstract class BaseGhostRenderer<T extends LivingEntity & GeoAnimatable> extends GeoEntityRenderer<T> {
    protected T ghostEntity;
    protected RenderType renderType;
    protected int cachedPackedLight;

    protected BaseGhostRenderer(EntityRendererProvider.Context context, GeoModel<T> modelProvider) {
        super(context, modelProvider);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.ghostEntity = animatable;
        this.cachedPackedLight = packedLight;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        ItemStack heldItemStack = this.ghostEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        String boneName = bone.getName();

        if (heldItemStack != null && !heldItemStack.isEmpty() && boneName.equals("item")) {
            poseStack.pushPose();
            this.moveAndRotateToBone(poseStack, bone);
            // poseStack.mulPose(Vector3f.XN.rotationDegrees(90));
            // if (!Minecraft.getInstance().getItemRenderer().getModel(heldItemStack, this.ghostEntity.level, this.ghostEntity, this.ghostEntity.getId()).isGui3d())
            //     poseStack.mulPose(Vector3f.ZP.rotationDegrees(90));
            this.setupHeldItemRender(poseStack, bone);
            // poseStack.translate((bone.getPositionX() * 0.1f) - (0.2f * 0.1f), (bone.getPositionY() * 0.1f) + (0.8f * 0.1f), (bone.getPositionZ() * 0.1f) + (3f * 0.1f) - 0.3f);
            poseStack.scale(0.6F, 0.6F, 0.6F);

            Minecraft.getInstance().getItemRenderer().renderStatic(heldItemStack, ItemDisplayContext.GROUND, cachedPackedLight, packedOverlay, poseStack, bufferSource, animatable.level(), 0);

            poseStack.popPose();
            //vertexConsumer = bufferSource.getBuffer(this.renderType);
        }

        if (boneName.equals("glow"))
            return;

        if (boneName.equals("main")) {
            packedLight = LightTexture.FULL_BRIGHT;
        } else if (boneName.equals("plant")) {
            packedLight = this.cachedPackedLight;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        this.renderType = RenderType.entityTranslucent(texture);
        return renderType;
    }

    protected void moveAndRotateToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.getPosX() / 16, bone.getPosY() / 16, bone.getPosZ() / 16);
        RenderUtils.rotateMatrixAroundBone(poseStack, bone);
        poseStack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
    }

    protected void setupHeldItemRender(PoseStack poseStack, GeoBone bone) {}
}
