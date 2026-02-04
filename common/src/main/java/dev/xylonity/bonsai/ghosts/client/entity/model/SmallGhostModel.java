package dev.xylonity.bonsai.ghosts.client.entity.model;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SmallGhostModel extends GeoModel<SmallGhostEntity> {

    @Override
    public ResourceLocation getModelResource(SmallGhostEntity object) {
        return Ghosts.of("geo/small_ghost.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SmallGhostEntity object) {
        return Ghosts.of("textures/entity/small_ghost.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmallGhostEntity animatable) {
        return Ghosts.of("animations/small_ghost.animation.json");
    }

}
