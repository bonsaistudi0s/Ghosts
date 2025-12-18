package dev.xylonity.bonsai.ghosts.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.bonsai.ghosts.client.entity.layer.GhostGlowLayer;
import dev.xylonity.bonsai.ghosts.client.entity.model.GhostModel;
import dev.xylonity.bonsai.ghosts.client.entity.render.core.BaseGhostRenderer;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.AbstractSkullBlock;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;
import software.bernie.geckolib.util.RenderUtils;

public class GhostRenderer extends BaseGhostRenderer<GhostEntity> {

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostModel());
        this.renderLayers.addLayer(new GhostGlowLayer(this));
        this.addRenderLayer(new HeadAnyItemArmorAwareLayer(this, "glow_1"));
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
        if ("mushroom_red".equals(bone.getName()) || "mushroom_brown".equals(bone.getName())) {
            ItemStack head = animatable.getItemBySlot(EquipmentSlot.HEAD);

            if ("mushroom_red".equals(bone.getName()) && !isRedMush(head)) return;
            if ("mushroom_brown".equals(bone.getName()) && !isBrownMush(head)) return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private static boolean isRedMush(ItemStack s) {
        return s.is(Items.RED_MUSHROOM);
    }

    private static boolean isBrownMush(ItemStack s) {
        return s.is(Items.BROWN_MUSHROOM);
    }

    public static class HeadAnyItemArmorAwareLayer extends ItemArmorGeoLayer<GhostEntity> {

        private final String headBoneName;

        public HeadAnyItemArmorAwareLayer(GeoRenderer<GhostEntity> renderer, String headBoneName) {
            super(renderer);
            this.headBoneName = headBoneName;
        }

        @Override
        protected ItemStack getArmorItemForBone(GeoBone bone, GhostEntity animatable) {
            if (headBoneName.equals(bone.getName())) {
                return this.helmetStack;
            }

            return null;
        }

        @Override
        protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, GhostEntity animatable) {
            if (headBoneName.equals(bone.getName())) return EquipmentSlot.HEAD;
            return super.getEquipmentSlotForBone(bone, stack, animatable);
        }

        @Override
        protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, GhostEntity animatable, HumanoidModel<?> baseModel) {
            if (headBoneName.equals(bone.getName())) return baseModel.head;
            return super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
        }

        @Override
        public void renderForBone(PoseStack poseStack, GhostEntity anim, GeoBone bone, RenderType renderType, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

            ItemStack stack = this.getArmorItemForBone(bone, anim);
            if (stack == null || stack.isEmpty()) return;
            if (isRedMush(stack) || isBrownMush(stack)) return;
            if (stack.getItem() instanceof ArmorItem) return;

            if (stack.getItem() instanceof BlockItem block && block.getBlock() instanceof AbstractSkullBlock) {
                super.renderForBone(poseStack, anim, bone, renderType, buffers, buffer, partialTick, packedLight, packedOverlay);
                return;
            }

            HumanoidModel<?> model = this.getModelForItem(bone, EquipmentSlot.HEAD, stack, anim);
            try {
                this.getReferenceCubeForModel(bone, this.getModelPartForBone(bone, EquipmentSlot.HEAD, stack, anim, model));
            } catch (IndexOutOfBoundsException ex) {
                return;
            }

            poseStack.pushPose();

            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);

            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            poseStack.scale(0.5125f, 0.5125f, 0.5125f);
            poseStack.translate(0, 0.5f, 0);

            // Renderer for equipable items (for example, carved pumpkin) that are not literal instances of Armor Items
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, anim.level(), 0);

            poseStack.popPose();

            buffers.getBuffer(renderType);
        }
    }

}
