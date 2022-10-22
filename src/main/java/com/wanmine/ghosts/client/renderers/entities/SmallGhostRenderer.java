package com.wanmine.ghosts.client.renderers.entities;

import com.google.common.collect.Maps;
import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.models.entities.SmallGhostModel;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import com.wanmine.ghosts.entities.variants.SmallGhostVariant;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SmallGhostRenderer extends BaseGhostRenderer<SmallGhostEntity> {
    public static final Map<SmallGhostVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(SmallGhostVariant.class), map -> {
                map.put(SmallGhostVariant.NORMAL_40, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_40.png"));
                map.put(SmallGhostVariant.NORMAL_80, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_80.png"));
                map.put(SmallGhostVariant.PLANT_40, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_seed_40.png"));
                map.put(SmallGhostVariant.PLANT_80, new ResourceLocation(Ghosts.MODID, "textures/entity/small_ghost_seed_80.png"));
            });

    public SmallGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new SmallGhostModel());
    }

    @Override
    protected ItemStack getHeldItemStack() {
        return this.ghostEntity.getHoldItem();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SmallGhostEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }
}
