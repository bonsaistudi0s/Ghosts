package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ghosts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GhostsServerEvents {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(GhostsEntities.GHOST.get(), GhostEntity.setAttributes());
        event.put(GhostsEntities.SMALL_GHOST.get(), SmallGhostEntity.setAttributes());
    }

}
