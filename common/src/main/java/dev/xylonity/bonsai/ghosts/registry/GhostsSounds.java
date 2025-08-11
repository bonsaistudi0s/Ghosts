package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class GhostsSounds {

    public static void init() { ;; }

    public static final Supplier<SoundEvent> GHOST_AMBIENT = registerSound("ghost_ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "ghost_ambient")));
    public static final Supplier<SoundEvent> GHOST_DEATH = registerSound("ghost_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "ghost_death")));
    public static final Supplier<SoundEvent> GHOST_HURT = registerSound("ghost_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "ghost_hurt")));
    public static final Supplier<SoundEvent> SMALL_GHOST_AMBIENT = registerSound("mini_ghost_ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "mini_ghost_ambient")));
    public static final Supplier<SoundEvent> SMALL_GHOST_DEATH = registerSound("mini_ghost_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "mini_ghost_death")));
    public static final Supplier<SoundEvent> SMALL_GHOST_HURT = registerSound("mini_ghost_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "mini_ghost_hurt")));

    private static <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return Ghosts.PLATFORM.registerSound(id, sound);
    }

}
