package dev.xylonity.bonsai.ghosts.client.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.client.blockentity.renderer.HauntedHangingSignRenderer;
import dev.xylonity.bonsai.ghosts.client.blockentity.renderer.HauntedSignRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.GhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.HauntedBoatRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.KodamaRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.SmallGhostRenderer;
import dev.xylonity.bonsai.ghosts.client.entity.render.blockentity.CalibratedHauntedEyeGlowRenderer;
import dev.xylonity.bonsai.ghosts.client.particle.FlyingGhostParticle;
import dev.xylonity.bonsai.ghosts.common.entity.boat.HauntedBoat;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsParticles;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;

public class GhostsClientEvents {

    public static void init() {
        EntityRendererRegistry.register(GhostsEntities.GHOST.get(), GhostRenderer::new);
        EntityRendererRegistry.register(GhostsEntities.SMALL_GHOST.get(), SmallGhostRenderer::new);
        EntityRendererRegistry.register(GhostsEntities.KODAMA.get(), KodamaRenderer::new);
        EntityRendererRegistry.register(GhostsEntities.HAUNTED_BOAT.get(), context -> new HauntedBoatRenderer(context, false));
        EntityRendererRegistry.register(GhostsEntities.HAUNTED_CHEST_BOAT.get(), context -> new HauntedBoatRenderer(context, true));

        BlockRenderLayerMap.INSTANCE.putBlock(GhostsBlocks.HAUNTED_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GhostsBlocks.HAUNTED_SAPLING.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GhostsBlocks.HAUNTED_TRAPDOOR.get(), RenderType.cutout());

        BlockEntityRendererRegistry.register(
                GhostsBlockEntities.CALIBRATED_HAUNTED_EYE.get(),
                CalibratedHauntedEyeGlowRenderer::new
        );
        BlockEntityRendererRegistry.register(
                GhostsBlockEntities.HAUNTED_SIGN.get(),
                HauntedSignRenderer::new
        );
        BlockEntityRendererRegistry.register(
                GhostsBlockEntities.HAUNTED_HANGING_SIGN.get(),
                HauntedHangingSignRenderer::new
        );

        for (HauntedBoat.Type type : HauntedBoat.Type.values()) {
            EntityModelLayerRegistry.registerModelLayer(
                    new ModelLayerLocation(Ghosts.of("boat/" + type.getName()), "main"),
                    BoatModel::createBodyModel
            );

            EntityModelLayerRegistry.registerModelLayer(
                    new ModelLayerLocation(Ghosts.of("chest_boat/" + type.getName()), "main"),
                    ChestBoatModel::createBodyModel
            );

        }

        ParticleFactoryRegistry.getInstance().register(GhostsParticles.FLYING_GHOST.get(), FlyingGhostParticle.Provider::new);
    }

}