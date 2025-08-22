package dev.xylonity.bonsai.ghosts.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

    /**
     * Handles internal ghost body rotation to match its movement direction
     */
    protected void rotateBody() {
        Vec3 vel = this.getDeltaMovement();
        if (vel.lengthSqr() < 1.0E-4) return;

        float yaw = (float) (Mth.atan2(vel.z, vel.x) * (180f / Math.PI)) - 90F;
        float pitch = (float) (-(Mth.atan2(vel.y, Math.sqrt(vel.x * vel.x + vel.z * vel.z)) * (180F / Math.PI)));

        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.yBodyRot = yaw;
        this.yRotO = yaw;
        this.yBodyRotO = yaw;

        this.setXRot(pitch);
        this.xRotO = pitch;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_WALL)) return false;
        return super.hurt(source, amount);
    }

    public void setMainInteraction(int interaction) {
        this.entityData.set(MAIN_INTERACTION, interaction);
    }

    public int getMainInteraction() {
        return this.entityData.get(MAIN_INTERACTION);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setMainInteraction(compound.getInt("MainInteraction"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MainInteraction", getMainInteraction());
    }

    public ItemStack getHoldItem() {
        return this.getItemBySlot(EquipmentSlot.MAINHAND);
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
