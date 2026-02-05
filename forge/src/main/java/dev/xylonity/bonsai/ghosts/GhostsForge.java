package dev.xylonity.bonsai.ghosts;

import dev.xylonity.bonsai.ghosts.client.ClientProxy;
import dev.xylonity.bonsai.ghosts.common.CommonProxy;
import dev.xylonity.bonsai.ghosts.common.biome.GhostsSpawnBiomeModifier;
import dev.xylonity.bonsai.ghosts.config.ConfigManager;
import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import dev.xylonity.bonsai.ghosts.proxy.IProxy;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ghosts.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ghosts.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ghosts.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Ghosts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Ghosts.MOD_ID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, Ghosts.MOD_ID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Ghosts.MOD_ID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_TYPES = DeferredRegister.create(ForgeRegistries.FOLIAGE_PLACER_TYPES, Ghosts.MOD_ID);

    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public GhostsForge() {

        IEventBus modBusEvent = FMLJavaModLoadingContext.get().getModEventBus();

        ConfigManager.init(FMLPaths.CONFIGDIR.get(), GhostsConfig.class);

        ENTITIES.register(modBusEvent);
        ITEMS.register(modBusEvent);
        BLOCKS.register(modBusEvent);
        PARTICLES.register(modBusEvent);
        BLOCKENTITIES.register(modBusEvent);
        CREATIVE_TABS.register(modBusEvent);
        SOUNDS.register(modBusEvent);

        TREE_DECORATOR_TYPES.register(modBusEvent);
        TRUNK_PLACER_TYPES.register(modBusEvent);
        FOLIAGE_TYPES.register(modBusEvent);

        GhostsSpawnBiomeModifier.BIOME_MODIFIER.register(modBusEvent);

        GhostsSpawnBiomeModifier.BIOME_MODIFIER.register("ghosts_mob_spawns", GhostsSpawnBiomeModifier::makeCodec);

        Ghosts.init();
    }

}