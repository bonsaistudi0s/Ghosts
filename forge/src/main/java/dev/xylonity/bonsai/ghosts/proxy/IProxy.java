package dev.xylonity.bonsai.ghosts.proxy;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface IProxy {
    default void createTrackingEmitter(Entity entity, ParticleOptions data, int lifetime) { ;; }
    default void displayItemActivation(ItemStack stack) { ;; }
}
