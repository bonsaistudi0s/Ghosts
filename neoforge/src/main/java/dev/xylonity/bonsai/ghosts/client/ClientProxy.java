package dev.xylonity.bonsai.ghosts.client;

import dev.xylonity.bonsai.ghosts.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class ClientProxy implements IProxy {

    @Override
    public void createTrackingEmitter(Entity entity, ParticleOptions data, int lifetime) {
        Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, data, lifetime);
    }

    @Override
    public void displayItemActivation(ItemStack stack) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
    }

}
