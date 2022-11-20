package com.wanmine.ghosts.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public abstract class BaseGhostRenderer<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {
    protected T ghostEntity;
    protected RenderType renderType;
    protected int cachedPackedLight;

    protected BaseGhostRenderer(EntityRendererProvider.Context context, AnimatedGeoModel<T> modelProvider) {
        super(context, modelProvider);
    }

    @Override
    public void renderEarly(T animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLight, int packedOverlayIn,
            float red, float green, float blue, float partialTicks) {
        this.ghostEntity = animatable;
        this.cachedPackedLight = packedLight;
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLight, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ItemStack heldItemStack = this.ghostEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        String boneName = bone.getName();

        if (heldItemStack != null && !heldItemStack.isEmpty() && boneName.equals("item")) {
            poseStack.pushPose();
            this.moveAndRotateToBone(poseStack, bone);
            ItemInHandRenderer itemInHandRenderer = Minecraft.getInstance().getItemInHandRenderer();
            // poseStack.mulPose(Vector3f.XN.rotationDegrees(90));
            // if (!Minecraft.getInstance().getItemRenderer().getModel(heldItemStack, this.ghostEntity.level, this.ghostEntity, this.ghostEntity.getId()).isGui3d())
            //     poseStack.mulPose(Vector3f.ZP.rotationDegrees(90));
            this.setupHeldItemRender(poseStack, bone);
            // poseStack.translate((bone.getPositionX() * 0.1f) - (0.2f * 0.1f), (bone.getPositionY() * 0.1f) + (0.8f * 0.1f), (bone.getPositionZ() * 0.1f) + (3f * 0.1f) - 0.3f);
            poseStack.scale(0.6F, 0.6F, 0.6F);
            itemInHandRenderer.renderItem(this.ghostEntity, heldItemStack,
                    ItemTransforms.TransformType.GROUND, false, poseStack, this.rtb,
                    this.cachedPackedLight);

            poseStack.popPose();
            vertexConsumer = this.rtb.getBuffer(this.renderType);
        }

        if (boneName.equals("glow"))
            return;

        if (boneName.equals("main")) {
            packedLight = LightTexture.FULL_BRIGHT;
        } else if (boneName.equals("plant")) {
            packedLight = this.cachedPackedLight;
        }

        super.renderRecursively(bone, poseStack, vertexConsumer, packedLight, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder,
            int packedLightIn, ResourceLocation textureLocation) {
        this.renderType = RenderType.entityTranslucent(textureLocation);
        return renderType;
    }

    protected void moveAndRotateToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
        RenderUtils.rotate(bone, poseStack);
        poseStack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
    }

    protected void setupHeldItemRender(PoseStack poseStack, GeoBone bone) {}
}
