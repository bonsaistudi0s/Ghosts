package com.wanmine.ghosts.registries;

import com.wanmine.ghosts.Ghosts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Ghosts.MODID);

    public static final RegistryObject<SoundEvent> GHOST_AMBIENT = SOUNDS.register("ghost_ambient", () -> new SoundEvent(new ResourceLocation(Ghosts.MODID, "ghost_ambient")));
    public static final RegistryObject<SoundEvent> GHOST_DEATH = SOUNDS.register("ghost_death", () -> new SoundEvent(new ResourceLocation(Ghosts.MODID, "ghost_death")));
    public static final RegistryObject<SoundEvent> GHOST_HURT = SOUNDS.register("ghost_hurt", () -> new SoundEvent(new ResourceLocation(Ghosts.MODID, "ghost_hurt")));
    public static final RegistryObject<SoundEvent> SMALL_GHOST_AMBIENT = SOUNDS.register("mini_ghost_ambient", () -> new SoundEvent(new ResourceLocation(Ghosts.MODID, "mini_ghost_ambient")));
    public static final RegistryObject<SoundEvent> SMALL_GHOST_DEATH = SOUNDS.register("mini_ghost_death", () -> new SoundEvent(new ResourceLocation(Ghosts.MODID, "mini_ghost_death")));
    public static final RegistryObject<SoundEvent> SMALL_GHOST_HURT = SOUNDS.register("mini_ghost_hurt", () -> new SoundEvent(new ResourceLocation(Ghosts.MODID, "mini_ghost_hurt")));

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}
