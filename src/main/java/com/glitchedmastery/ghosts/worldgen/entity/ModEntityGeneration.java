package com.wanmine.ghosts.worldgen.entity;

import com.wanmine.ghosts.registries.ModEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;

public class ModEntityGeneration {
    public static void ghostSpawn(final BiomeLoadingEvent event) {
        ResourceLocation name = event.getName();
        if (name != null) {
            if (name.toString().equals(Biomes.FOREST.location().toString()) || name.toString().equals(Biomes.BIRCH_FOREST.location().toString()) || name.toString().equals(Biomes.DARK_FOREST.location().toString())) {
                List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(ModEntityTypes.GHOST.get().getCategory());
                base.add(new MobSpawnSettings.SpawnerData(ModEntityTypes.GHOST.get(), 1, 1, 1));
            }
        }
    }

    public static void smallGhostSpawn(final BiomeLoadingEvent event) {
        ResourceLocation name = event.getName();
        if (name != null) {
            if (name.toString().equals(Biomes.SWAMP.location().toString()) || name.toString().equals(Biomes.FLOWER_FOREST.location().toString()) || name.toString().equals(Biomes.DARK_FOREST.location().toString())) {
                List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(ModEntityTypes.SMALL_GHOST.get().getCategory());
                base.add(new MobSpawnSettings.SpawnerData(ModEntityTypes.SMALL_GHOST.get(), 1, 3, 3));
            }
        }
    }
}
