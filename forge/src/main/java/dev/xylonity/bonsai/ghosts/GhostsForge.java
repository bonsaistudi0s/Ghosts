package dev.xylonity.bonsai.ghosts;

import dev.xylonity.bonsai.ghosts.client.ClientProxy;
import dev.xylonity.bonsai.ghosts.common.CommonProxy;
import dev.xylonity.bonsai.ghosts.common.biome.GhostsSpawnBiomeModifier;
import dev.xylonity.bonsai.ghosts.config.ConfigManager;
import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import dev.xylonity.bonsai.ghosts.proxy.IProxy;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Ghosts.MOD_ID)
public class GhostsForge {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Ghosts.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ghosts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Ghosts.MOD_ID);

    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public GhostsForge() {

        IEventBus modBusEvent = FMLJavaModLoadingContext.get().getModEventBus();

        ConfigManager.init(FMLPaths.CONFIGDIR.get(), GhostsConfig.class);

        ENTITIES.register(modBusEvent);
        ITEMS.register(modBusEvent);
        SOUNDS.register(modBusEvent);

        GhostsSpawnBiomeModifier.BIOME_MODIFIER.register(modBusEvent);

        GhostsSpawnBiomeModifier.BIOME_MODIFIER.register("ghosts_mob_spawns", GhostsSpawnBiomeModifier::makeCodec);

        Ghosts.init();
    }

}