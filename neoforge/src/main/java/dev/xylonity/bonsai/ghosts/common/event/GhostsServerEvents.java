package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.datagen.GhostsWorldgenProvider;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.concurrent.CompletableFuture;

public class GhostsServerEvents {

    @EventBusSubscriber(modid = Ghosts.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class GhostsServerModEvents {

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
        public static void gatherData(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput packOutput = generator.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            generator.addProvider(event.includeServer(), new GhostsWorldgenProvider(packOutput, lookupProvider));
        }

    }

    @EventBusSubscriber(modid = Ghosts.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class GhostsServerForgeEvents {

        @SubscribeEvent
        public static void onBlockToolModification(BlockEvent.BlockToolModificationEvent event) {
            if (event.getItemAbility() == ItemAbilities.AXE_STRIP) {
                if (event.getState().is(GhostsBlocks.HAUNTED_LOG.get())) {
                    event.setFinalState(GhostsBlocks.STRIPPED_HAUNTED_LOG.get().defaultBlockState());
                }
                if (event.getState().is(GhostsBlocks.HAUNTED_EYE_LOG.get())) {
                    event.setFinalState(GhostsBlocks.STRIPPED_HAUNTED_LOG.get().defaultBlockState());
                }

            }

        }

    }

}