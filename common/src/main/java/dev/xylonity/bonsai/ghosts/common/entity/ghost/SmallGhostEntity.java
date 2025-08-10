package dev.xylonity.bonsai.ghosts.common.entity.ghost;

import dev.xylonity.bonsai.ghosts.common.entity.MainGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ai.control.GhostMoveControl;
import dev.xylonity.bonsai.ghosts.common.entity.ai.generic.GhostWanderGoal;
import dev.xylonity.bonsai.ghosts.common.entity.variant.SmallGhostVariant;
import dev.xylonity.bonsai.ghosts.registry.GhostsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.List;

public class SmallGhostEntity extends MainGhostEntity {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CD_FULL_HIDE = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_STAYING = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SLEEPING = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.BOOLEAN);

    public SmallGhostEntity(EntityType<? extends TamableAnimal> entity, Level world) {
        super(entity, world);

        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.BLOCKED, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, -1.0F);

        this.moveControl = new GhostMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(9, new GhostWanderGoal(this, 0.43D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !getIsSleeping();
            }
        });
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, level);

        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        navigator.setCanPassDoors(true);

        return navigator;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return AbstractGolem.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.FLYING_SPEED, 0.22f)
                .add(Attributes.MOVEMENT_SPEED, 0.22F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(IS_STAYING, false);
        this.entityData.define(IS_SLEEPING, false);
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
        this.entityData.define(CD_FULL_HIDE, 0);
    }

    public void setIsStaying(boolean isStaying) {
        this.entityData.set(IS_STAYING, isStaying);
    }

    public boolean getIsStaying() {
        return this.entityData.get(IS_STAYING);
    }

    public void setHoldItem(ItemStack holdItem) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, holdItem);
    }

    public ItemStack getHoldItem() {
        return this.getItemBySlot(EquipmentSlot.MAINHAND);
    }

    public void setIsSleeping(boolean isSleeping) {
        this.entityData.set(IS_SLEEPING, isSleeping);
    }

    public boolean getIsSleeping() {
        return this.entityData.get(IS_SLEEPING);
    }

    public int getCdFullHide() {
        return this.entityData.get(CD_FULL_HIDE);
    }

    public void setCdFullHide(int cd) {
        this.entityData.set(CD_FULL_HIDE, cd);
    }

    public int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    public SmallGhostVariant getVariant() {
        return SmallGhostVariant.byId(this.getTypeVariant() & 255);
    }

    public void setVariant(SmallGhostVariant variant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant & 255);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", getVariant().getId());
        compoundTag.putInt("CdFullHide", getCdFullHide());
        compoundTag.putBoolean("IsStaying", getIsStaying());
        compoundTag.putBoolean("IsHiding", getIsSleeping());
    }

    @Override
    public int getExperienceReward() {
        return 1 + level().random.nextInt(2, 4);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (!this.getIsSleeping()) {
            super.push(entity);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.getIsSleeping()) {
            super.doPush(entity);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        if (level().isClientSide) return;

        if (getCdFullHide() > 0) {
            setCdFullHide(getCdFullHide() - 1);
        }

        SmallGhostVariant variant = getVariant();
        if (variant == SmallGhostVariant.PLANT) {
            BlockState belowBlockState = level().getBlockState(this.blockPosition().below());
            if (!level().isDay() && (belowBlockState.is(Blocks.GRASS_BLOCK) || belowBlockState.is(Blocks.DIRT))) {
                if (!getIsSleeping())
                    setCdFullHide(36);

                setIsSleeping(true);
            } else {
                setIsSleeping(false);
            }
        }

        tryPickupItems();
    }

    private void tryPickupItems() {
        var level = level();

        if (level.isClientSide || this.tickCount % 20 != 0 || !getHoldItem().isEmpty() || this.getIsSleeping())
            return;

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(this.blockPosition().offset(-10, -10, -10), this.blockPosition().offset(10, 10, 10)));

        if (items.isEmpty() || getHoldItem().getCount() >= 64)
            return;

        items.forEach(itemEntity -> {
            ItemStack itemStack = itemEntity.getItem();
            if (!itemStack.is(ItemTags.SAPLINGS))
                return;

            double distSqr = this.blockPosition().distToCenterSqr(itemEntity.blockPosition().getX(), itemEntity.blockPosition().getY(), itemEntity.blockPosition().getZ());
            if (distSqr <= 1) {
                ItemStack pickupCopy = itemStack.copy();

                if (itemStack.getCount() > 1) {
                    itemStack.shrink(1);
                    pickupCopy.setCount(1);
                    this.take(itemEntity, 1);
                } else {
                    itemEntity.discard();
                }

                setHoldItem(pickupCopy);
            } else {
                Path path = this.navigation.createPath(itemEntity.blockPosition(), 1);

                if (path != null)
                    this.navigation.moveTo(path, 2.0D);
            }
        });
    }

    @Override
    public boolean hurt(DamageSource source, float p) {
        if (source.getEntity() != null) {
            Vec3 vec = AirRandomPos.getPosTowards(this, 32, 32, 32, new Vec3(32, 32, 32), 32);

            if (vec != null) {
                Path path = this.navigation.createPath(vec.x, vec.y, vec.z, 2);

                if (path != null)
                    this.navigation.moveTo(path, 2.0D);
            }
        }

        if (this.getIsSleeping())
            return false;
        else
            return super.hurt(source, p);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("Variant")) {
            this.setVariant(compoundTag.getInt("Variant"));
        }
        if (compoundTag.contains("CdFullHide")) {
            this.setCdFullHide(compoundTag.getInt("CdFullHide"));
        }
        if (compoundTag.contains("IsStaying")) {
            this.setIsStaying(compoundTag.getBoolean("IsStaying"));
        }
        if (compoundTag.contains("IsSleeping")) {
            this.setIsSleeping(compoundTag.getBoolean("IsSleeping"));
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return GhostsSounds.SMALL_GHOST_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return GhostsSounds.SMALL_GHOST_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return GhostsSounds.SMALL_GHOST_HURT.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        setVariant(levelAccessor.getRandom().nextBoolean() ? SmallGhostVariant.PLANT : SmallGhostVariant.NORMAL);

        return super.finalizeSpawn(levelAccessor, difficulty, mobSpawnType, spawnGroupData, compoundTag);
    }

    private <E extends GeoAnimatable> PlayState bodyAC(AnimationState<E> event) {
        if (event.isMoving() && !getIsSleeping()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_move"));

            return PlayState.CONTINUE;
        }

        if (getIsSleeping()) {
            if (getCdFullHide() > 0)
                event.getController().setAnimation(RawAnimation.begin().thenPlay("ghost_bury"));
            else
                event.getController().setAnimation(RawAnimation.begin().thenPlay("mini_ghost_buried"));

            return PlayState.CONTINUE;
        }

        if (!getIsSleeping() && !event.isMoving())
            event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_idle"));

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState armsAC(AnimationState<E> event) {
        if (!getHoldItem().isEmpty()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("mini_ghost_arms_hold"));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop(event.isMoving() ? "ghost_move_arms" : "ghost_idle_arms"));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "small_ghost_animation_controller_body", 1, this::bodyAC));
        registrar.add(new AnimationController<>(this, "small_ghost_animation_controller_arms", 1, this::armsAC));
    }

}
