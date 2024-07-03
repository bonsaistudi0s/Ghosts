package com.wanmine.ghosts.registries;

import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registry.ENTITY_TYPE_REGISTRY, Ghosts.MODID);

    public static final RegistryObject<EntityType<GhostEntity>> GHOST = ENTITIES.register("ghost", () -> EntityType.Builder.of(GhostEntity::new, MobCategory.CREATURE).sized(0.6f, 0.6f).build(new ResourceLocation(Ghosts.MODID, "ghost").toString()));
    public static final RegistryObject<EntityType<SmallGhostEntity>> SMALL_GHOST = ENTITIES.register("small_ghost", () -> EntityType.Builder.of(SmallGhostEntity::new, MobCategory.CREATURE).sized(0.4f, 0.4f).build(new ResourceLocation(Ghosts.MODID, "small_ghost").toString()));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
