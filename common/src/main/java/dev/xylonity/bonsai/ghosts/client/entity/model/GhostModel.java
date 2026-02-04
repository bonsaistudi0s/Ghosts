package dev.xylonity.bonsai.ghosts.client.entity.model;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GhostModel extends GeoModel<GhostEntity> {

    @Override
    public ResourceLocation getModelResource(GhostEntity object) {
        return Ghosts.of("geo/ghost.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GhostEntity object) {
        return Ghosts.of("textures/entity/ghost.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GhostEntity animatable) {
        return Ghosts.of("animations/ghost.animation.json");
    }

}
