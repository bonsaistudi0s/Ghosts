package dev.xylonity.bonsai.ghosts.client.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.client.entity.render.GhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.KodamaRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.SmallGhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.blockentity.CalibratedHauntedEyeGlowRenderer;
import dev.xylonity.bonsai.ghosts.client.particle.FlyingGhostParticle;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsParticles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Ghosts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GhostsClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(GhostsEntities.GHOST.get(), GhostRenderer::new);
        EntityRenderers.register(GhostsEntities.SMALL_GHOST.get(), SmallGhostRenderer::new);
        EntityRenderers.register(GhostsEntities.KODAMA.get(), KodamaRenderer::new);

        ItemBlockRenderTypes.setRenderLayer(GhostsBlocks.HAUNTED_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GhostsBlocks.HAUNTED_SAPLING.get(), RenderType.cutout());

        BlockEntityRenderers.register(GhostsBlockEntities.CALIBRATED_HAUNTED_EYE.get(), CalibratedHauntedEyeGlowRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(GhostsParticles.FLYING_GHOST.get(), FlyingGhostParticle.Provider::new);
    }

}