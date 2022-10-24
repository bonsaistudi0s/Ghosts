package com.wanmine.ghosts.worldgen.entity;

import com.wanmine.ghosts.registries.ModEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;

public class ModEntityGeneration {
    public static void ghostSpawn(final BiomeLoadingEvent event) {
        ResourceLocation biomeName = event.getName();
        if (biomeName == null)
            return;

        if (biomeName.equals(Biomes.FOREST.location()) || biomeName.equals(Biomes.BIRCH_FOREST.location()) || biomeName.equals(Biomes.DARK_FOREST.location())) {
            List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(ModEntityTypes.GHOST.get().getCategory());
            base.add(new MobSpawnSettings.SpawnerData(ModEntityTypes.GHOST.get(), 8, 1, 2));
        }
    }

    public static void smallGhostSpawn(final BiomeLoadingEvent event) {
        ResourceLocation biomeName = event.getName();
        if (biomeName == null)
            return;

        if (biomeName.equals(Biomes.SWAMP.location()) || biomeName.equals(Biomes.FLOWER_FOREST.location()) || biomeName.equals(Biomes.DARK_FOREST.location())) {
            List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(ModEntityTypes.SMALL_GHOST.get().getCategory());
            base.add(new MobSpawnSettings.SpawnerData(ModEntityTypes.SMALL_GHOST.get(), 8, 1, 3));
        }
    }
}
