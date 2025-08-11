package dev.xylonity.bonsai.ghosts;

import dev.xylonity.bonsai.ghosts.client.ClientProxy;
import dev.xylonity.bonsai.ghosts.common.CommonProxy;
import dev.xylonity.bonsai.ghosts.common.biome.GhostsSpawnBiomeModifier;
import dev.xylonity.bonsai.ghosts.config.ConfigManager;
import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import dev.xylonity.bonsai.ghosts.proxy.IProxy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Ghosts.MOD_ID)
public class GhostsForge {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Ghosts.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Ghosts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Ghosts.MOD_ID);

    public static final IProxy PROXY = FMLLoader.getDist() == Dist.CLIENT ? new ClientProxy() : new CommonProxy();

    public GhostsForge(IEventBus modBusEvent) {

        ConfigManager.init(FMLPaths.CONFIGDIR.get(), GhostsConfig.class);

        ENTITIES.register(modBusEvent);
        ITEMS.register(modBusEvent);
        SOUNDS.register(modBusEvent);

        GhostsSpawnBiomeModifier.BIOME_MODIFIER.register(modBusEvent);

        Ghosts.init();
    }

}