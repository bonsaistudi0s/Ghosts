package dev.xylonity.bonsai.ghosts.common.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;

public abstract class MainGhostEntity extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = new InstancedAnimatableInstanceCache(this);

    // 0 sit, 1 follow, 2 idle
    private static final EntityDataAccessor<Integer> MAIN_INTERACTION = SynchedEntityData.defineId(MainGhostEntity.class, EntityDataSerializers.INT);

    public MainGhostEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MAIN_INTERACTION, 0);
    }

    public void cycleMainInteraction(Player player) {
        int interaction = (getMainInteraction() + 1) % 3;

        setOrderedToSit(interaction == 0);

        if (player != null) {
            switch (interaction) {
                case 0 -> player.displayClientMessage(Component.translatable("entity.ghosts.client_message.interaction_0", this.getName()), true);
                case 1 -> player.displayClientMessage(Component.translatable("entity.ghosts.client_message.interaction_1", this.getName()), true);
                case 2 -> player.displayClientMessage(Component.translatable("entity.ghosts.client_message.interaction_2", this.getName()), true);
            }

        }

        this.entityData.set(MAIN_INTERACTION, interaction);
    }

    public void setMainInteraction(int interaction) {
        this.entityData.set(MAIN_INTERACTION, interaction);
    }

    public int getMainInteraction() {
        return this.entityData.get(MAIN_INTERACTION);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
