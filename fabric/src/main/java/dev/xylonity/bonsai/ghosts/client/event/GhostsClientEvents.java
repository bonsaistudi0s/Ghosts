package dev.xylonity.bonsai.ghosts.client.event;

import dev.xylonity.bonsai.ghosts.client.entity.render.GhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.KodamaRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.SmallGhostRenderer;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GhostsClientEvents {

    public static void init() {
        EntityRendererRegistry.register(GhostsEntities.GHOST.get(), GhostRenderer::new);
        EntityRendererRegistry.register(GhostsEntities.SMALL_GHOST.get(), SmallGhostRenderer::new);
        EntityRendererRegistry.register(GhostsEntities.KODAMA.get(), KodamaRenderer::new);
    }

}