package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.item.CreativeModeTabs;

public class GhostsServerEvents {

    public static void init() {
        FabricDefaultAttributeRegistry.register(GhostsEntities.GHOST.get(), GhostEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(GhostsEntities.SMALL_GHOST.get(), SmallGhostEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(GhostsEntities.KODAMA.get(), KodamaEntity.setAttributes());

        StrippableBlockRegistry.register(
                GhostsBlocks.HAUNTED_LOG.get(),
                GhostsBlocks.STRIPPED_HAUNTED_LOG.get()
        );
        StrippableBlockRegistry.register(
                GhostsBlocks.HAUNTED_EYE_LOG.get(),
                GhostsBlocks.STRIPPED_HAUNTED_LOG.get()
        );

    }

}