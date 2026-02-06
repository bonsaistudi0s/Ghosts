package dev.xylonity.bonsai.ghosts.platform;

import com.mojang.serialization.Codec;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.GhostsFabric;
import dev.xylonity.bonsai.ghosts.registry.GhostsBlockEntities;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class GhostsPlatformFabric implements GhostsPlatform {

    @Override
    public <X extends Item> Supplier<X> registerItem(String id, Supplier<X> item) {
        return registerSupplier(BuiltInRegistries.ITEM, id, item);
    }

    @Override
    public <X extends Block> Supplier<X> registerBlock(String id, Supplier<X> block, boolean registerItem) {
        Supplier<X> blockSupplier = registerSupplier(BuiltInRegistries.BLOCK, id, block);

        if (registerItem) {
            registerItem(id, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
        }

        return blockSupplier;
    }

    @Override
    public <X extends BlockEntity> Supplier<BlockEntityType<X>> registerBlockEntity(String id, GhostsBlockEntities.BlockEntityFactory<X> supplier, Supplier<Block>... blocks) {
        return registerSupplier(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> {
            Block[] blockArray = Arrays.stream(blocks)
                    .map(Supplier::get)
                    .toArray(Block[]::new);

            return BlockEntityType.Builder.of(supplier::create, blockArray).build(null);
        });

    }

    @Override
    public <X extends CreativeModeTab> Supplier<X> registerCreativeTab(String id, Supplier<X> creativeModeTab) {
        return registerSupplier(BuiltInRegistries.CREATIVE_MODE_TAB, id, creativeModeTab);
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return registerSupplier(BuiltInRegistries.PARTICLE_TYPE, id, () -> (T) FabricParticleTypes.simple());
    }

    @Override
    public <T extends Item, X extends LivingEntity> Supplier<T> registerSpawnEgg(String id, Supplier<? extends EntityType<? extends Mob>> entity, int color1, int color2, Item.Properties properties) {
        return (Supplier<T>) registerItem(id, () -> new SpawnEggItem(entity.get(), color1, color2, properties));
    }

    @Override
    public <X extends Entity> Supplier<EntityType<X>> registerEntity(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable List<Consumer<EntityType.Builder<X>>> properties) {
        return registerSupplier(BuiltInRegistries.ENTITY_TYPE, name, () -> {
            EntityType.Builder<X> builder = EntityType.Builder.of(entity, category).sized(width, height);

            if (properties != null) {
                for (Consumer<EntityType.Builder<X>> property : properties) {
                    property.accept(builder);
                }
            }

            return builder.build(new ResourceLocation(Ghosts.MOD_ID, name).toString());
        });
    }

    @Override
    public <X extends SoundEvent> Supplier<X> registerSound(String id, Supplier<X> sound) {
        return registerSupplier(BuiltInRegistries.SOUND_EVENT, id, sound);
    }

    @Override
    public <U extends TrunkPlacer> Supplier<TrunkPlacerType<U>> registerTrunkPlacer(String id, Codec<U> codec) {
        return registerSupplier(BuiltInRegistries.TRUNK_PLACER_TYPE, id, () -> new TrunkPlacerType<>(codec));
    }

    @Override
    public <U extends FoliagePlacer> Supplier<FoliagePlacerType<U>> registerFoliagePlacer(String id, Codec<U> codec) {
        return registerSupplier(BuiltInRegistries.FOLIAGE_PLACER_TYPE, id, () -> new FoliagePlacerType<>(codec));
    }

    @Override
    public WoodType registerWoodType(ResourceLocation id, BlockSetType setType) {
        return WoodTypeRegistry.register(id, setType);
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return FabricItemGroup.builder();
    }

    private static <T, R extends Registry<? super T>> Supplier<T> registerSupplier(R registry, String id, Supplier<T> factory) {
        T value = factory.get();
        Registry.register((Registry<T>) registry, new ResourceLocation(Ghosts.MOD_ID, id), value);
        return () -> value;
    }

}