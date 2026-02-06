package dev.xylonity.bonsai.ghosts.client.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.client.entity.render.GhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.HauntedBoatRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.KodamaRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.SmallGhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.blockentity.CalibratedHauntedEyeGlowRenderer;
import dev.xylonity.bonsai.ghosts.client.particle.FlyingGhostParticle;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsParticles;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = Ghosts.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GhostsClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(GhostsEntities.GHOST.get(), GhostRenderer::new);
        EntityRenderers.register(GhostsEntities.SMALL_GHOST.get(), SmallGhostRenderer::new);
        EntityRenderers.register(GhostsEntities.KODAMA.get(), KodamaRenderer::new);

        EntityRenderers.register(GhostsEntities.HAUNTED_BOAT.get(), context -> new HauntedBoatRenderer(context, false));
        EntityRenderers.register(GhostsEntities.HAUNTED_CHEST_BOAT.get(), context -> new HauntedBoatRenderer(context, true));

        ItemBlockRenderTypes.setRenderLayer(GhostsBlocks.HAUNTED_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GhostsBlocks.HAUNTED_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GhostsBlocks.HAUNTED_TRAPDOOR.get(), RenderType.cutout());

        BlockEntityRenderers.register(GhostsBlockEntities.CALIBRATED_HAUNTED_EYE.get(), CalibratedHauntedEyeGlowRenderer::new);
        BlockEntityRenderers.register(GhostsBlockEntities.HAUNTED_SIGN.get(), SignRenderer::new);
        BlockEntityRenderers.register(GhostsBlockEntities.HAUNTED_HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                new ModelLayerLocation(Ghosts.of("boat/haunted"), "main"),
                BoatModel::createBodyModel
        );

        event.registerLayerDefinition(
                new ModelLayerLocation(Ghosts.of("chest_boat/haunted"), "main"),
                ChestBoatModel::createBodyModel
        );

    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(GhostsParticles.FLYING_GHOST.get(), FlyingGhostParticle.Provider::new);
    }

}