package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.item.CreativeModeTabs;

public class GhostsServerEvents {

    public static void init() {
        FabricDefaultAttributeRegistry.register(GhostsEntities.GHOST.get(), GhostEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(GhostsEntities.SMALL_GHOST.get(), SmallGhostEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(GhostsEntities.KODAMA.get(), SmallGhostEntity.setAttributes());

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(GhostsItems.GHOST_SPAWN_EGG.get());
            entries.accept(GhostsItems.SMALL_GHOST_SPAWN_EGG.get());
            entries.accept(GhostsItems.KODAMA_SPAWN_EGG.get());
        });
    }

}