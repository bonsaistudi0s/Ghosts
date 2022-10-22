package com.wanmine.ghosts;

import com.mojang.logging.LogUtils;
import com.wanmine.ghosts.client.renderers.entities.GhostRenderer;
import com.wanmine.ghosts.client.renderers.entities.SmallGhostRenderer;
import com.wanmine.ghosts.events.ForgeEventBusEvents;
import com.wanmine.ghosts.registries.ModEntityTypes;
import com.wanmine.ghosts.registries.ModItems;
import com.wanmine.ghosts.registries.ModSounds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(Ghosts.MODID)
public class Ghosts
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "ghosts";

    public Ghosts()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntityTypes.register(eventBus);
        ModItems.register(eventBus);
        ModSounds.register(eventBus);

        GeckoLib.initialize();

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(new ForgeEventBusEvents());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntityTypes.GHOST.get(), GhostRenderer::new);
        EntityRenderers.register(ModEntityTypes.SMALL_GHOST.get(), SmallGhostRenderer::new);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntityTypes.SMALL_GHOST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMobSpawnRules);
        });
    }
}
