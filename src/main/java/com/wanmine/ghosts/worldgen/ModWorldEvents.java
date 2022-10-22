package com.wanmine.ghosts.worldgen;

import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.worldgen.entity.ModEntityGeneration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ghosts.MODID)
public class ModWorldEvents {
    @SubscribeEvent
    public static void biomeLoadingEvent(final BiomeLoadingEvent event) {
        ModEntityGeneration.ghostSpawn(event);
        ModEntityGeneration.smallGhostSpawn(event);
    }
}
