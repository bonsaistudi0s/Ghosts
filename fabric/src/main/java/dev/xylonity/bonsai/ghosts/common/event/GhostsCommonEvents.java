package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.common.entity.AbstractGhostEntity;
import dev.xylonity.bonsai.ghosts.util.GhostOwnerTracker;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class GhostsCommonEvents {

    public static void init() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((living, source, amount) -> {
            if (living instanceof Player player) {
                if (player.level() instanceof ServerLevel serverLevel) {
                    AbstractGhostEntity ghost = GhostOwnerTracker.getInstance().findGhostWithTotem(serverLevel, player.getUUID());
                    if (ghost != null) {
                        defaultTotemBehavior(player);
                        ghost.getMainHandItem().shrink(1);
                        return false;
                    }

                }

            }

            return true;
        });

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
