package com.wanmine.ghosts.client.models.entities;

import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.renderers.entities.GhostRenderer;
import com.wanmine.ghosts.entities.GhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GhostModel extends AnimatedGeoModel<GhostEntity> {

    @Override
    public ResourceLocation getModelLocation(GhostEntity object) {
        return new ResourceLocation(Ghosts.MODID, "geo/ghost.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(GhostEntity object) {
        return GhostRenderer.TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(GhostEntity animatable) {
        return new ResourceLocation(Ghosts.MODID, "animations/ghost.animation.json");
    }
}
