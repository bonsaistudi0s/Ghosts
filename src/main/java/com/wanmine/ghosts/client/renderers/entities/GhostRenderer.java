package com.wanmine.ghosts.client.renderers.entities;

import com.google.common.collect.Maps;
import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.models.entities.GhostModel;
import com.wanmine.ghosts.client.renderers.layers.GhostGlowLayer;
import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.variants.GhostVariant;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GhostRenderer extends BaseGhostRenderer<GhostEntity> {
    public static final Map<GhostVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(GhostVariant.class), map -> {
                map.put(GhostVariant.NORMAL_40, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_40.png"));
                map.put(GhostVariant.NORMAL_80, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_80.png"));
                map.put(GhostVariant.MUSHROOM_40, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_mushroom_40.png"));
                map.put(GhostVariant.MUSHROOM_80, new ResourceLocation(Ghosts.MODID, "textures/entity/ghost_mushroom_80.png"));
            });

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostModel());
        // this.addLayer(new GhostGlowLayer<>(this));
    }

    @Override
    protected ItemStack getHeldItemStack() {
        return this.ghostEntity.getHoldItem();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GhostEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }
}
