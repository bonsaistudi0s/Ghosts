package dev.xylonity.bonsai.ghosts.client.entity.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.boat.HauntedBoat;
import dev.xylonity.bonsai.ghosts.common.entity.boat.HauntedChestBoat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.stream.Stream;

public class HauntedBoatRenderer extends BoatRenderer {

    private final Map<HauntedBoat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public HauntedBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);
        this.boatResources = Stream.of(HauntedBoat.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type,
                type -> Pair.of(Ghosts.of(getTextureLocation(type, chestBoat)), this.createBoatModel(context, type, chestBoat))));
    }

    private static String getTextureLocation(HauntedBoat.Type type, boolean chestBoat) {
        return chestBoat ? "textures/entity/chest_boat/" + type.getName() + ".png" : "textures/entity/boat/" + type.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, HauntedBoat.Type type, boolean chestBoat) {
        ModelLayerLocation location = chestBoat ? createChestBoatModelName(type) : createBoatModelName(type);
        ModelPart modelpart = context.bakeLayer(location);
        return chestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    public static ModelLayerLocation createBoatModelName(HauntedBoat.Type type) {
        return new ModelLayerLocation(Ghosts.of("boat/" + type.getName()), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(HauntedBoat.Type type) {
        return new ModelLayerLocation(Ghosts.of("chest_boat/" + type.getName()), "main");
    }

    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        if (boat instanceof HauntedBoat b) {
            return this.boatResources.get(b.getBoatVariant());
        }
        else if (boat instanceof HauntedChestBoat b) {
            return this.boatResources.get(b.getBoatVariant());
        }

        return null;
    }

    @Override
    public void render(Boat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Pair<ResourceLocation, ListModel<Boat>> pair = getModelWithLocation(entity);
        if (pair == null) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float f = (float) entity.getHurtTime() - partialTicks;
        float f1 = entity.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }
        if (f > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) entity.getHurtDir()));
        }

        float f2 = entity.getBubbleAngle(partialTicks);
        if (!Mth.equal(f2, 0.0F)) {
            poseStack.mulPose(new Quaternionf().setAngleAxis(f2 * ((float) Math.PI / 180F), 1.0F, 0.0F, 1.0F));
        }

        ResourceLocation texture = pair.getFirst();
        ListModel<Boat> model = pair.getSecond();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = buffer.getBuffer(model.renderType(texture));
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        if (!entity.isUnderWater()) {
            VertexConsumer waterConsumer = buffer.getBuffer(RenderType.waterMask());
            if (model instanceof WaterPatchModel waterPatchModel) {
                waterPatchModel.waterPatch().render(poseStack, waterConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }

        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Boat entity) {
        Pair<ResourceLocation, ListModel<Boat>> pair = getModelWithLocation(entity);
        if (pair != null) {
            return pair.getFirst();
        }

        return super.getTextureLocation(entity);
    }

}