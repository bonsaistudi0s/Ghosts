package com.wanmine.ghosts.client.models.entities;

import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.renderers.entities.GhostRenderer;
import com.wanmine.ghosts.entities.GhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GhostModel extends AnimatedGeoModel<GhostEntity> {
    private static final ResourceLocation GEO_LOCATION = new ResourceLocation(Ghosts.MODID, "geo/ghost.geo.json");
    private static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(Ghosts.MODID, "animations/ghost.animation.json");

    @Override
    public ResourceLocation getModelLocation(GhostEntity object) {
        return GEO_LOCATION;
    }

    @Override
    public ResourceLocation getTextureLocation(GhostEntity object) {
        return GhostRenderer.TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(GhostEntity animatable) {
        return ANIMATION_LOCATION;
    }
}
