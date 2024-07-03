package com.wanmine.ghosts.registries;

import com.wanmine.ghosts.Ghosts;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Ghosts.MODID);

    public static final RegistryObject<ForgeSpawnEggItem> SMALL_GHOST_SPAWN_EGG = ITEMS.register("small_ghost_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.SMALL_GHOST, 0xA5FFFF, 0x00FF00, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> GHOST_SPAWN_EGG = ITEMS.register("ghost_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.GHOST, 0xA5FFFF, 0x783D7C, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
