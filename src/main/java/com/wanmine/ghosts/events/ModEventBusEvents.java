package com.wanmine.ghosts.events;

import com.wanmine.ghosts.Ghosts;
import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import com.wanmine.ghosts.registries.ModEntityTypes;
import com.wanmine.ghosts.registries.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Ghosts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.GHOST.get(), GhostEntity.setAttributes());
        event.put(ModEntityTypes.SMALL_GHOST.get(), SmallGhostEntity.setAttributes());
    }

    @SubscribeEvent
    public void onCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.GHOST_SPAWN_EGG);
            event.accept(ModItems.SMALL_GHOST_SPAWN_EGG);
        }
    }
}

