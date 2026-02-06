package dev.xylonity.bonsai.ghosts;

import dev.xylonity.bonsai.ghosts.client.ClientProxy;
import dev.xylonity.bonsai.ghosts.common.CommonProxy;
import dev.xylonity.bonsai.ghosts.common.biome.GhostsSpawnBiomeModifier;
import dev.xylonity.bonsai.ghosts.config.ConfigManager;
import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import dev.xylonity.bonsai.ghosts.proxy.IProxy;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(Ghosts.MOD_ID)
public class GhostsNeoForge {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Ghosts.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Ghosts.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Ghosts.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Ghosts.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ghosts.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Ghosts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Ghosts.MOD_ID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Ghosts.MOD_ID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_TYPES = DeferredRegister.create(BuiltInRegistries.FOLIAGE_PLACER_TYPE, Ghosts.MOD_ID);

    public static final IProxy PROXY = FMLLoader.getDist() == Dist.CLIENT ? new ClientProxy() : new CommonProxy();

    public GhostsNeoForge(IEventBus modBusEvent) {

        ConfigManager.init(FMLPaths.CONFIGDIR.get(), GhostsConfig.class);

        ENTITIES.register(modBusEvent);
        ITEMS.register(modBusEvent);
        BLOCKS.register(modBusEvent);
        PARTICLES.register(modBusEvent);
        BLOCKENTITIES.register(modBusEvent);
        CREATIVE_TABS.register(modBusEvent);
        SOUNDS.register(modBusEvent);

        TRUNK_PLACER_TYPES.register(modBusEvent);
        FOLIAGE_TYPES.register(modBusEvent);

        GhostsSpawnBiomeModifier.BIOME_MODIFIER.register(modBusEvent);

        Ghosts.init();
    }

}