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

}
