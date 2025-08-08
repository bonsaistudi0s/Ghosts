package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class GhostsItems {

    public static void init() { ;; }

    public static final Supplier<SpawnEggItem> GHOST_SPAWN_EGG = registerItem("ghost_spawn_egg", () -> new SpawnEggItem(GhostsEntities.GHOST.get(), 0xA5FFFF, 0x783D7C, new Item.Properties()));
    public static final Supplier<SpawnEggItem> SMALL_GHOST_SPAWN_EGG = registerItem("small_ghost_spawn_egg", () -> new SpawnEggItem(GhostsEntities.SMALL_GHOST.get(), 0xA5FFFF, 0x00FF00, new Item.Properties()));

    private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return Ghosts.PLATFORM.registerItem(id, item);
    }

}
