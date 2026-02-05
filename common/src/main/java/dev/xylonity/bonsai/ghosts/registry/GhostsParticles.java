package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.SmallGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GhostsParticles {

    public static void init() { ;; }

    public static final Supplier<EntityType<GhostEntity>> GHOST = registerEntity("ghost", GhostEntity::new, MobCategory.CREATURE, 0.6f, 0.6f, null);

    private static <X extends Entity> Supplier<EntityType<X>> registerEntity(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable List<Consumer<EntityType.Builder<X>>> properties) {
        return Ghosts.PLATFORM.registerEntity(name, entity, category, width, height, properties);
    }

}
