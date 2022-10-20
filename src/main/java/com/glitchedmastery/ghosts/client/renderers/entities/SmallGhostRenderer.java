package com.wanmine.ghosts.client.renderers.entities;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.models.entities.SmallGhostModel;
import com.wanmine.ghosts.client.renderers.layers.GhostItemInHandLayer;
import com.wanmine.ghosts.client.renderers.layers.SmallGhostItemInHandLayer;
import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import com.wanmine.ghosts.entities.variants.SmallGhostVariant;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

import java.util.Map;

public class SmallGhostRenderer extends GeoEntityRenderer<SmallGhostEntity> {
    public static final Map<SmallGhostVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(SmallGhostVariant.class), (rersourceLocation) -> {
                rersourceLocation.put(SmallGhostVariant.NORMAL_40, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_40.png"));
                rersourceLocation.put(SmallGhostVariant.NORMAL_80, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_80.png"));
                rersourceLocation.put(SmallGhostVariant.PLANT_40, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_seed_40.png"));
                rersourceLocation.put(SmallGhostVariant.PLANT_80, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_seed_80.png"));
            });

    public SmallGhostRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SmallGhostModel());
        this.addLayer(new SmallGhostItemInHandLayer<>(this));
    }

    @Override
    public RenderType getRenderType(SmallGhostEntity animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SmallGhostEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }
}
