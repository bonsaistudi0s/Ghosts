package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import dev.xylonity.bonsai.ghosts.config.SpawnConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public class GhostsSpawns {

    private static final SpawnConfig GHOST_SPAWN_CONFIG = SpawnConfig.parse(GhostsConfig.GHOST_SPAWN);
    private static final SpawnConfig SMALL_GHOST_SPAWN_CONFIG = SpawnConfig.parse(GhostsConfig.SMALL_GHOST_SPAWN);

    public static void addBiomeSpawns(Holder<Biome> biomeHolder, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (GHOST_SPAWN_CONFIG.matches(biomeHolder)) {
            int weight = GHOST_SPAWN_CONFIG.weight;
            int min = GHOST_SPAWN_CONFIG.minCount;
            int max = GHOST_SPAWN_CONFIG.maxCount;

            builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(GhostsEntities.GHOST.get(), weight, min, max));
        }
        if (SMALL_GHOST_SPAWN_CONFIG.matches(biomeHolder)) {
            int weight = SMALL_GHOST_SPAWN_CONFIG.weight;
            int min = SMALL_GHOST_SPAWN_CONFIG.minCount;
            int max = SMALL_GHOST_SPAWN_CONFIG.maxCount;

            builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(GhostsEntities.SMALL_GHOST.get(), weight, min, max));
        }

    }

}
