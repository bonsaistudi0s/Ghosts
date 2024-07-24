package com.wanmine.ghosts.client.models.entities;

import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.renderers.entities.SmallGhostRenderer;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SmallGhostModel extends AnimatedGeoModel<SmallGhostEntity> {
    private static final ResourceLocation GEO_LOCATION = new ResourceLocation(Ghosts.MODID, "geo/small_ghost.geo.json");
    private static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(Ghosts.MODID, "animations/small_ghost.animation.json");

    @Override
    public ResourceLocation getModelResource(SmallGhostEntity object) {
        return GEO_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(SmallGhostEntity object) {
        return SmallGhostRenderer.TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(SmallGhostEntity animatable) {
        return ANIMATION_LOCATION;
    }
}
