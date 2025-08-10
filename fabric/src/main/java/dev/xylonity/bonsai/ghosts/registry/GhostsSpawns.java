package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import dev.xylonity.bonsai.ghosts.config.SpawnConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.levelgen.Heightmap;

public class GhostsSpawns {

    private static final SpawnConfig GHOST_SPAWN_CONFIG = SpawnConfig.parse(GhostsConfig.GHOST_SPAWN);
    private static final SpawnConfig SMALL_GHOST_SPAWN_CONFIG = SpawnConfig.parse(GhostsConfig.SMALL_GHOST_SPAWN);

    public static void init() {
        BiomeModifications.addSpawn(ctx -> GHOST_SPAWN_CONFIG.matches(ctx.getBiomeRegistryEntry()), MobCategory.CREATURE,
            GhostsEntities.GHOST.get(),
            GHOST_SPAWN_CONFIG.weight,
            GHOST_SPAWN_CONFIG.minCount,
            GHOST_SPAWN_CONFIG.maxCount
        );

        BiomeModifications.addSpawn(ctx -> SMALL_GHOST_SPAWN_CONFIG.matches(ctx.getBiomeRegistryEntry()), MobCategory.CREATURE,
                GhostsEntities.SMALL_GHOST.get(),
                SMALL_GHOST_SPAWN_CONFIG.weight,
                SMALL_GHOST_SPAWN_CONFIG.minCount,
                SMALL_GHOST_SPAWN_CONFIG.maxCount
        );

        SpawnPlacements.register(GhostsEntities.GHOST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TamableAnimal::checkMobSpawnRules);
        SpawnPlacements.register(GhostsEntities.SMALL_GHOST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TamableAnimal::checkMobSpawnRules);
    }

}
