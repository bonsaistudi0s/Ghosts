package dev.xylonity.bonsai.ghosts.common.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;

public abstract class MainGhostEntity extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = new InstancedAnimatableInstanceCache(this);

    public MainGhostEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
