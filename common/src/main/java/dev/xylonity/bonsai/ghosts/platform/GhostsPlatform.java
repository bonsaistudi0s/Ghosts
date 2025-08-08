package dev.xylonity.bonsai.ghosts.platform;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface GhostsPlatform {
    <X extends Item> Supplier<X> registerItem(String id, Supplier<X> item);
    <X extends Entity> Supplier<EntityType<X>> registerEntity(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable List<Consumer<EntityType.Builder<X>>> properties);
    <X extends SoundEvent> Supplier<X> registerSound(String id, Supplier<X> sound);
}