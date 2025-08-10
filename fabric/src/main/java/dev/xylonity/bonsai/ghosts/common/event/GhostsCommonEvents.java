package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GhostsCommonEvents {

    public static void init() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((living, source, amount) -> {

            if (!(living instanceof Player player)) return true;

            ServerLevel level = (ServerLevel) player.level();
            List<GhostEntity> ghosts = level.getEntitiesOfClass(GhostEntity.class, new AABB(player.blockPosition()).inflate(10, 10, 10), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

            for (GhostEntity ghost : ghosts) {
                if (ghost.isOwnedBy(player) && ghost.getHoldItem().is(Items.TOTEM_OF_UNDYING)) {
                    defaultTotemBehavior(player);

                    ghost.setHoldItem(ItemStack.EMPTY);

                    return false;
                }
            }

            return true;
        });

    }

    private static void defaultTotemBehavior(LivingEntity entity) {
        entity.setHealth(1.0F);

        entity.removeAllEffects();
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

        if (entity.level() instanceof ServerLevel sLevel) {
            sLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, entity.getX(), entity.getY() + 1.0, entity.getZ(), 30, 0.5, 0.5, 0.5, 0.0);

            sLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (entity instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundEntityEventPacket(entity, (byte) 35));
        }

    }

}
