package dev.xylonity.bonsai.ghosts.platform;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface GhostsPlatform {

    <X extends Item> Supplier<X> registerItem(String id, Supplier<X> item);
    <X extends Block> Supplier<X> registerBlock(String id, Supplier<X> block);
    <X extends CreativeModeTab> Supplier<X> registerCreativeTab(String id, Supplier<X> creativeModeTab);
    <T extends Item, X extends LivingEntity> Supplier<T> registerSpawnEgg(String id, Supplier<? extends EntityType<? extends Mob>> entity, int color1, int color2, Item.Properties properties);
    <X extends Entity> Supplier<EntityType<X>> registerEntity(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable List<Consumer<EntityType.Builder<X>>> properties);
    <X extends SoundEvent> Supplier<X> registerSound(String id, Supplier<X> sound);
    <U extends TrunkPlacer> Supplier<TrunkPlacerType<U>> registerTrunkPlacer(String id, Codec<U> codec);
    <U extends TreeDecorator> Supplier<TreeDecoratorType<U>> registerTreeDecorator(String id, Codec<U> codec);
    <U extends FoliagePlacer> Supplier<FoliagePlacerType<U>> registerFoliagePlacer(String id, Codec<U> codec);

    CreativeModeTab.Builder creativeTabBuilder();

}