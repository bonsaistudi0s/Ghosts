package com.wanmine.ghosts.client.renderers.entities;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.models.entities.GhostModel;
import com.wanmine.ghosts.client.renderers.layers.GhostItemInHandLayer;
import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.variants.GhostVariant;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Map;

public class GhostRenderer extends GeoEntityRenderer<GhostEntity> {
    public static final Map<GhostVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(GhostVariant.class), (rersourceLocation) -> {
                rersourceLocation.put(GhostVariant.NORMAL_40, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_40.png"));
                rersourceLocation.put(GhostVariant.NORMAL_80, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_80.png"));
                rersourceLocation.put(GhostVariant.MUSHROOM_40, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_mushroom_40.png"));
                rersourceLocation.put(GhostVariant.MUSHROOM_80, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_mushroom_80.png"));
            });

    public GhostRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GhostModel());
        this.addLayer(new GhostItemInHandLayer<>(this));
    }

    @Override
    public RenderType getRenderType(GhostEntity animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GhostEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }
}
