package dev.xylonity.bonsai.ghosts.platform;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.GhostsForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GhostsPlatformForge implements GhostsPlatform {

    @Override
    public <X extends Item> Supplier<X> registerItem(String id, Supplier<X> item) {
        return GhostsForge.ITEMS.register(id, item);
    }

    @Override
    public <X extends Entity> Supplier<EntityType<X>> registerEntity(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable List<Consumer<EntityType.Builder<X>>> properties) {
        return GhostsForge.ENTITIES.register(name, () -> {
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
        return GhostsForge.SOUNDS.register(id, sound);
    }

}