package dev.xylonity.bonsai.ghosts;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Ghosts.MOD_ID)
public class GhostsForge {

    public static final String MOD_ID = Ghosts.MOD_ID;

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Ghosts.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ghosts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Ghosts.MOD_ID);

    public GhostsForge() {

        IEventBus modBusEvent = FMLJavaModLoadingContext.get().getModEventBus();

        ENTITIES.register(modBusEvent);
        ITEMS.register(modBusEvent);
        SOUNDS.register(modBusEvent);

        Ghosts.init();
    }

}