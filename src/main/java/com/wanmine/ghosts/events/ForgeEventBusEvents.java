package com.wanmine.ghosts.events;

import com.wanmine.ghosts.entities.GhostEntity;
import com.wanmine.ghosts.entities.SmallGhostEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.*;

public class ForgeEventBusEvents {
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        Level world = living.level;

        if (living instanceof Player player) {
            List<GhostEntity> ghosts = world.getEntitiesOfClass(GhostEntity.class, new AABB(living.blockPosition().offset(-10, -10, -10), living.blockPosition().offset(10, 10, 10)), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

            for (GhostEntity ghost : ghosts) {
                if (ghost.isOwnedBy(living)) {
                    if (ghost.getHoldItem().getItem() == Items.TOTEM_OF_UNDYING) {
                        defaultTotemBehavior(event, player);

                        ghost.setHoldItem(ItemStack.EMPTY);

                        break;
                    }
                }
            }
        }
    }

    public void defaultTotemBehavior(LivingDeathEvent event, LivingEntity entity) {
        event.setCanceled(true);

        entity.setHealth(1.0F);
        entity.removeAllEffects();
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

        if (FMLLoader.getDist() == Dist.CLIENT)
            Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);

        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        entity.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);

        if (FMLLoader.getDist() == Dist.CLIENT)
            Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(Items.TOTEM_OF_UNDYING));
    }
}