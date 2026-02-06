package dev.xylonity.bonsai.ghosts.client.entity.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.block.CalibratedHauntedEyeBlock;
import dev.xylonity.bonsai.ghosts.common.blockentity.CalibratedHauntedEyeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class CalibratedHauntedEyeGlowRenderer implements BlockEntityRenderer<CalibratedHauntedEyeBlockEntity> {

    private static final ResourceLocation GLOW_OFF = Ghosts.of("block/calibrated_haunted_eye_front_glow");
    private static final ResourceLocation GLOW_ON = Ghosts.of("block/calibrated_haunted_eye_front_on_glow");

    public CalibratedHauntedEyeGlowRenderer(BlockEntityRendererProvider.Context context) {
        ;;
    }

    @Override
    public void render(CalibratedHauntedEyeBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof CalibratedHauntedEyeBlock)) {
            return;
        }

        int power = state.getValue(CalibratedHauntedEyeBlock.POWER);
        Direction frontDirection = state.getValue(CalibratedHauntedEyeBlock.FACING);

        renderFrontGlow(poseStack, buffers, power, frontDirection);

        if (power > 0) {
            renderTopGlow(poseStack, buffers, power, frontDirection);
        }

    }

    private void renderFrontGlow(PoseStack poseStack, MultiBufferSource buffers, int power, Direction frontDirection) {
        ResourceLocation texture = (power > 0) ? GLOW_ON : GLOW_OFF;
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

        int fullBright = LightTexture.pack(15, 15);
        VertexConsumer vertexConsumer = buffers.getBuffer(RenderType.translucent());

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        rotateFromNorthTo(poseStack, frontDirection);
        poseStack.scale(1.001f, 1.001f, 1.001f);
        poseStack.translate(-0.5, -0.5, -0.5);

        Matrix4f lastPose = poseStack.last().pose();
        PoseStack.Pose normalMatrix = poseStack.last();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float x0 = 0f;
        float y0 = 0f;
        float x1 = 1f;
        float y1 = 1f;
        float z = -0.001f;

        vertexConsumer.addVertex(lastPose, x0, y0, z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u0, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 0f, -1f);

        vertexConsumer.addVertex(lastPose, x0, y1, z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u0, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 0f, -1f);

        vertexConsumer.addVertex(lastPose, x1, y1, z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u1, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 0f, -1f);

        vertexConsumer.addVertex(lastPose, x1, y0, z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u1, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 0f, -1f);

        poseStack.popPose();
    }

    private void renderTopGlow(PoseStack poseStack, MultiBufferSource buffers, int power, Direction frontDirection) {
        ResourceLocation texture;
        if (power <= 4) {
            texture = Ghosts.of("block/calibrated_haunted_eye_top_2_glow");
        }
        else if (power <= 6) {
            texture = Ghosts.of("block/calibrated_haunted_eye_top_3_glow");
        }
        else if (power <= 8) {
            texture = Ghosts.of("block/calibrated_haunted_eye_top_4_glow");
        }
        else if (power <= 10) {
            texture = Ghosts.of("block/calibrated_haunted_eye_top_5_glow");
        }
        else {
            texture = Ghosts.of("block/calibrated_haunted_eye_top_6_glow");
        }

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

        int fullBright = LightTexture.pack(15, 15);
        VertexConsumer vertexConsumer = buffers.getBuffer(RenderType.translucent());

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        rotateTopForFacing(poseStack, frontDirection);

        poseStack.scale(1.001f, 1.001f, 1.001f);
        poseStack.translate(-0.5, -0.5, -0.5);

        Matrix4f lastPose = poseStack.last().pose();
        PoseStack.Pose normalMatrix = poseStack.last();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float x0 = 0f;
        float z0 = 0f;
        float x1 = 1f;
        float z1 = 1f;
        float y = 1.001f;

        vertexConsumer.addVertex(lastPose, x0, y, z0)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u0, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 1f, 0f);

        vertexConsumer.addVertex(lastPose, x0, y, z1)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u0, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 1f, 0f);

        vertexConsumer.addVertex(lastPose, x1, y, z1)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u1, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 1f, 0f);

        vertexConsumer.addVertex(lastPose, x1, y, z0)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u1, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(fullBright)
                .setNormal(normalMatrix, 0f, 1f, 0f);

        poseStack.popPose();
    }

    private static void rotateFromNorthTo(PoseStack poseStack, Direction direction) {
        switch (direction) {
            case NORTH -> { ;; }
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180f));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270f));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90f));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
        }

    }

    private static void rotateTopForFacing(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH -> { ;; }
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180f));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270f));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90f));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
        }

    }

}