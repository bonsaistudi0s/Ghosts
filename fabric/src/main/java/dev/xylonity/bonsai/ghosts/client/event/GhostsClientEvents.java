package dev.xylonity.bonsai.ghosts.client.event;

import dev.xylonity.bonsai.ghosts.client.entity.render.GhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.KodamaRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.SmallGhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.blockentity.CalibratedHauntedEyeGlowRenderer;
import dev.xylonity.bonsai.ghosts.client.particle.FlyingGhostParticle;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsParticles;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;

public class GhostsClientEvents {

    public static void init() {
        EntityRendererRegistry.register(GhostsEntities.GHOST.get(), GhostRenderer::new);
        EntityRendererRegistry.register(GhostsEntities.SMALL_GHOST.get(), SmallGhostRenderer::new);
        EntityRendererRegistry.register(GhostsEntities.KODAMA.get(), KodamaRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(GhostsBlocks.HAUNTED_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GhostsBlocks.HAUNTED_SAPLING.get(), RenderType.cutout());

        BlockEntityRendererRegistry.register(
                GhostsBlockEntities.CALIBRATED_HAUNTED_EYE.get(),
                CalibratedHauntedEyeGlowRenderer::new
        );

        ParticleFactoryRegistry.getInstance().register(GhostsParticles.FLYING_GHOST.get(), FlyingGhostParticle.Provider::new);
    }

}