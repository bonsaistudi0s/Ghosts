package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.AbstractGhostEntity;
import dev.xylonity.bonsai.ghosts.util.GhostOwnerTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ghosts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GhostsCommonEvents {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.level() instanceof ServerLevel serverLevel) {
                AbstractGhostEntity ghost = GhostOwnerTracker.getInstance().findGhostWithTotem(serverLevel, player.getUUID());
                if (ghost != null) {
                    defaultTotemBehavior(player);
                    ghost.getMainHandItem().shrink(1);
                    event.setCanceled(true);
                }

            }

        }

    }

    private static void defaultTotemBehavior(LivingEntity entity) {
        entity.setHealth(1F);
        entity.removeAllEffects();
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        entity.level().broadcastEntityEvent(entity, (byte) 35);
    }

}
