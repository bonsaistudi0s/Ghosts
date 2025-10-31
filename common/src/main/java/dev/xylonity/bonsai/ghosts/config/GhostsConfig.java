package dev.xylonity.bonsai.ghosts.config;

import dev.xylonity.bonsai.ghosts.config.wrapper.AutoConfig;
import dev.xylonity.bonsai.ghosts.config.wrapper.ConfigEntry;

@AutoConfig(file = "ghosts")
public final class GhostsConfig {

    @ConfigEntry(
            comment = "Ghost spawnrate: [weight, minAmount, maxAmount, biomes and tags... (as many as you want)]"
    )
    public static String GHOST_SPAWN = "14, 1, 2, #minecraft:is_forest";

    @ConfigEntry(
            comment = "Ghost spawnrate: [weight, minAmount, maxAmount, biomes and tags... (as many as you want)]"
    )
    public static String SMALL_GHOST_SPAWN = "14, 1, 3, minecraft:flower_forest, #minecraft:is_forest, #forge:is_swamp";

    @ConfigEntry(
            comment = "Kodama spawnrate: [weight, minAmount, maxAmount, biomes and tags... (as many as you want)]"
    )
    public static String KODAMA_SPAWN = "40, 1, 4, minecraft:birch_forest, minecraft:forest, minecraft:old_growth_spruce_taiga, minecraft:dark_forest";

    @ConfigEntry(
            comment = "The maximum distance (in blocks) at which ghosts will teleport to their owner.",
            min = 0, max = 1000
    )
    public static int GHOSTS_FOLLOW_OWNER_TELEPORT_DISTANCE = 10;

}
