package com.wanmine.ghosts.client.models.entities;

import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.client.renderers.entities.SmallGhostRenderer;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SmallGhostModel extends AnimatedGeoModel<SmallGhostEntity> {

    @Override
    public ResourceLocation getModelLocation(SmallGhostEntity object) {
        return new ResourceLocation(Ghosts.MODID, "geo/small_ghost.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(SmallGhostEntity object) {
        return SmallGhostRenderer.TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SmallGhostEntity animatable) {
        return new ResourceLocation(Ghosts.MODID, "animations/small_ghost.animation.json");
    }
}
