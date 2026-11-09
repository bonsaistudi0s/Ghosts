package dev.xylonity.bonsai.ghosts.common.entity;

import dev.xylonity.bonsai.ghosts.util.GhostOwnerTracker;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public abstract class AbstractGhostEntity extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 0 sit, 1 follow, 2 idle
    private static final EntityDataAccessor<Integer> MAIN_INTERACTION = SynchedEntityData.defineId(AbstractGhostEntity.class, EntityDataSerializers.INT);

    private boolean chunkForced = false;
    private long forcedChunk = Long.MIN_VALUE;

    private boolean isTracked;

    public static final int WANDER_RADIUS = 30;

    @Nullable
    private BlockPos wanderAnchor;

    public AbstractGhostEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.isTracked = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (getOwnerUUID() != null) {
                if (!isTracked) {
                    GhostOwnerTracker.getInstance().addGhost(this);
                    isTracked = true;
                }

                ensureChunkForced();
            }

        }

    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && isTracked) {
            GhostOwnerTracker.getInstance().removeGhost(this);
            isTracked = false;
        }

        super.remove(reason);
    }

    private void ensureChunkForced() {
        if (level() instanceof ServerLevel serverLevel) {
            ChunkPos chunkPosition = new ChunkPos(blockPosition());
            long now = chunkPosition.toLong();

            if (chunkForced && forcedChunk != now) {
                ChunkPos old = new ChunkPos(forcedChunk);
                serverLevel.setChunkForced(old.x, old.z, false);
                chunkForced = false;
            }

            if (!chunkForced) {
                serverLevel.setChunkForced(chunkPosition.x, chunkPosition.z, true);
                forcedChunk = now;
                chunkForced = true;
            }

        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MAIN_INTERACTION, 0);
    }

    public void cycleMainInteraction(Player player) {
        int interaction = (getMainInteraction() + 1) % 3;

        setOrderedToSit(interaction == 0);
        this.wanderAnchor = interaction == 2 ? this.blockPosition() : null;

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

    protected boolean tryHealWithGlowBerries(Player player, ItemStack stack) {
        if (!stack.is(Items.GLOW_BERRIES) || getHealth() >= getMaxHealth()) {
            return false;
        }

        if (!level().isClientSide) {
            this.heal(4F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            this.gameEvent(GameEvent.EAT, this);
        }

        return true;
    }

    public void setMainInteraction(int interaction) {
        this.entityData.set(MAIN_INTERACTION, interaction);
    }

    public int getMainInteraction() {
        return this.entityData.get(MAIN_INTERACTION);
    }

    @Nullable
    public BlockPos getWanderAnchor() {
        return isTame() && getMainInteraction() == 2 ? this.wanderAnchor : null;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setMainInteraction(compound.getInt("MainInteraction"));

        if (compound.contains("WanderAnchor")) {
            this.wanderAnchor = BlockPos.of(compound.getLong("WanderAnchor"));
        }
        else if (getMainInteraction() == 2) {
            this.wanderAnchor = this.blockPosition();
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MainInteraction", getMainInteraction());

        if (this.wanderAnchor != null) {
            compound.putLong("WanderAnchor", this.wanderAnchor.asLong());
        }
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
