package dev.xylonity.bonsai.ghosts.common.event;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.GhostsForge;
import dev.xylonity.bonsai.ghosts.common.entity.ghost.GhostEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

@EventBusSubscriber(modid = Ghosts.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GhostsCommonEvents {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        Level world = living.level();

        if (living instanceof Player player) {
            List<GhostEntity> ghosts = world.getEntitiesOfClass(GhostEntity.class, new AABB(living.blockPosition().offset(-10, -10, -10).getCenter(), living.blockPosition().offset(10, 10, 10).getCenter()), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

            for (GhostEntity ghost : ghosts) {
                if (ghost.isOwnedBy(living)) {
                    if (ghost.getHoldItem().getItem() == Items.TOTEM_OF_UNDYING) {
                        event.setCanceled(true);
                        defaultTotemBehavior(player);

                        ghost.setHoldItem(ItemStack.EMPTY);

                        break;
                    }
                }
            }

        }

    }

    private static void defaultTotemBehavior(LivingEntity entity) {
        entity.setHealth(1.0F);
        entity.removeAllEffects();
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

        GhostsForge.PROXY.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);

        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        entity.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);

        GhostsForge.PROXY.displayItemActivation(new ItemStack(Items.TOTEM_OF_UNDYING));
    }

}
