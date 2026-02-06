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
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

public class GhostsServerEvents {

    @Mod.EventBusSubscriber(modid = Ghosts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class GhostsServerModEvents {

        @SubscribeEvent
        public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(GhostsEntities.GHOST.get(), GhostEntity.setAttributes().build());
            event.put(GhostsEntities.SMALL_GHOST.get(), SmallGhostEntity.setAttributes().build());
            event.put(GhostsEntities.KODAMA.get(), KodamaEntity.setAttributes().build());
        }

        @SubscribeEvent
        public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
            event.register(GhostsEntities.GHOST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TamableAnimal::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(GhostsEntities.SMALL_GHOST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TamableAnimal::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(GhostsEntities.KODAMA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KodamaEntity::checkKodamaSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        }

        @SubscribeEvent
        public static void gatherData(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput packOutput = generator.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            generator.addProvider(event.includeServer(), new GhostsWorldgenProvider(packOutput, lookupProvider));
        }

    }

    @Mod.EventBusSubscriber(modid = Ghosts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class GhostsServerForgeEvents {

        @SubscribeEvent
        public static void onBlockToolModification(BlockEvent.BlockToolModificationEvent event) {
            if (event.getToolAction() == ToolActions.AXE_STRIP) {
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