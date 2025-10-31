package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class GhostsItems {

    public static void init() { ;; }

    public static final Supplier<SpawnEggItem> GHOST_SPAWN_EGG = registerSpawnEgg("ghost_spawn_egg", GhostsEntities.GHOST, 0xA5FFFF, 0x783D7C, new Item.Properties());
    public static final Supplier<SpawnEggItem> SMALL_GHOST_SPAWN_EGG = registerSpawnEgg("small_ghost_spawn_egg", GhostsEntities.SMALL_GHOST, 0xA5FFFF, 0x00FF00, new Item.Properties());
    public static final Supplier<SpawnEggItem> KODAMA_SPAWN_EGG = registerSpawnEgg("kodama_spawn_egg", GhostsEntities.KODAMA, 0xe8ead7, 0x92a8a5, new Item.Properties());

    private static <T extends Item, X extends LivingEntity> Supplier<T> registerSpawnEgg(String id, Supplier<? extends EntityType<? extends Mob>> entity, int color1, int color2, Item.Properties properties) {
        return Ghosts.PLATFORM.registerSpawnEgg(id, entity, color1, color2, properties);
    }

}
