package dev.xylonity.bonsai.ghosts.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.GhostsNeoForge;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class GhostsPlatformNeoforge implements GhostsPlatform {

    @Override
    public <X extends Item> Supplier<X> registerItem(String id, Supplier<X> item) {
        return GhostsNeoForge.ITEMS.register(id, item);
    }

    @Override
    public <X extends Block> Supplier<X> registerBlock(String id, Supplier<X> block, boolean registerItem) {
        Supplier<X> blockSupplier = GhostsNeoForge.BLOCKS.register(id, block);

        if (registerItem) {
            registerItem(id, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
        }

        return blockSupplier;
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return GhostsNeoForge.PARTICLES.register(id, () -> (T) new SimpleParticleType(overrideLimiter));
    }

    @Override
    public <X extends BlockEntity> Supplier<BlockEntityType<X>> registerBlockEntity(String id, GhostsBlockEntities.BlockEntityFactory<X> supplier, Supplier<Block>... blocks) {
        return GhostsNeoForge.BLOCKENTITIES.register(id, () -> {
            Block[] blockArray = Arrays.stream(blocks)
                    .map(Supplier::get)
                    .toArray(Block[]::new);

            return BlockEntityType.Builder.of(supplier::create, blockArray).build(null);
        });

    }

    @Override
    public <X extends CreativeModeTab> Supplier<X> registerCreativeTab(String id, Supplier<X> creativeModeTab) {
        return GhostsNeoForge.CREATIVE_TABS.register(id, creativeModeTab);
    }

    @Override
    public <T extends Item, X extends LivingEntity> Supplier<T> registerSpawnEgg(String id, Supplier<? extends EntityType<? extends Mob>> entity, int color1, int color2, Item.Properties properties) {
        return (Supplier<T>) registerItem(id, () -> new DeferredSpawnEggItem(entity, color1, color2, properties));
    }

    @Override
    public <X extends Entity> Supplier<EntityType<X>> registerEntity(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable List<Consumer<EntityType.Builder<X>>> properties) {
        return GhostsNeoForge.ENTITIES.register(name, () -> {
            EntityType.Builder<X> builder = EntityType.Builder.of(entity, category).sized(width, height);

            if (properties != null) {
                for (Consumer<EntityType.Builder<X>> property : properties) {
                    property.accept(builder);
                }
            }

            return builder.build(Ghosts.of(name).toString());
        });
    }

    @Override
    public <X extends SoundEvent> Supplier<X> registerSound(String id, Supplier<X> sound) {
        return GhostsNeoForge.SOUNDS.register(id, sound);
    }

    @Override
    public <U extends TrunkPlacer> Supplier<TrunkPlacerType<U>> registerTrunkPlacer(String id, MapCodec<U> codec) {
        return GhostsNeoForge.TRUNK_PLACER_TYPES.register(id, () -> new TrunkPlacerType<>(codec));
    }

    @Override
    public <U extends FoliagePlacer> Supplier<FoliagePlacerType<U>> registerFoliagePlacer(String id, MapCodec<U> codec) {
        return GhostsNeoForge.FOLIAGE_TYPES.register(id, () -> new FoliagePlacerType<>(codec));
    }

    @Override
    public WoodType registerWoodType(ResourceLocation id, BlockSetType setType) {
        WoodType type = new WoodType(id.toString(), setType);
        WoodType.register(type);
        return type;
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }

}