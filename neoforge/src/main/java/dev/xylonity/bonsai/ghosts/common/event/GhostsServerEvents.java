package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsItems;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = Ghosts.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class GhostsServerEvents {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(GhostsEntities.GHOST.get(), GhostEntity.setAttributes().build());
        event.put(GhostsEntities.SMALL_GHOST.get(), SmallGhostEntity.setAttributes().build());
        event.put(GhostsEntities.KODAMA.get(), KodamaEntity.setAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(GhostsEntities.GHOST.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TamableAnimal::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GhostsEntities.SMALL_GHOST.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TamableAnimal::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GhostsEntities.KODAMA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KodamaEntity::checkKodamaSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void onCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(GhostsItems.GHOST_SPAWN_EGG.get());
            event.accept(GhostsItems.SMALL_GHOST_SPAWN_EGG.get());
            event.accept(GhostsItems.KODAMA_SPAWN_EGG.get());
        }
    }

}