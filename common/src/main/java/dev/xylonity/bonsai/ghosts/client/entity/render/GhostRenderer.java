package dev.xylonity.bonsai.ghosts.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.ghosts.client.entity.model.GhostModel;
import dev.xylonity.bonsai.ghosts.client.entity.render.core.BaseGhostRenderer;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.variant.GhostVariant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class GhostRenderer extends BaseGhostRenderer<GhostEntity> {

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostModel());
        this.addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, GhostEntity animatable) {
                if ("glow_1".equals(bone.getName())) {
                    return this.helmetStack;
                }

                return null;
            }

            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, GhostEntity animatable) {
                if ("glow_1".equals(bone.getName())) {
                    return EquipmentSlot.HEAD;
                }

                return super.getEquipmentSlotForBone(bone, stack, animatable);
            }

            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, GhostEntity animatable, HumanoidModel<?> baseModel) {
                if ("glow_1".equals(bone.getName())) {
                    return baseModel.head;
                }

                return super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
            }

            @Override
            protected void prepModelPartForRender(PoseStack poseStack, GeoBone bone, ModelPart sourcePart) {
                super.prepModelPartForRender(poseStack, bone, sourcePart);
                if ("glow_1".equals(bone.getName())) {
                    // Realigned helmet position as the default one collides with the model per se
                    poseStack.translate(0.0, -0.1, 0.0);
                }
            }

        });

    }

    @Override
    public void renderRecursively(PoseStack poseStack, GhostEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if ("plant".equals(bone.getName()) && animatable.getVariant() != GhostVariant.MUSHROOM)
            return;

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
