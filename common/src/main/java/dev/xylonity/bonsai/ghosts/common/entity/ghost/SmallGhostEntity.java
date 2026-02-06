package dev.xylonity.bonsai.ghosts.common.entity.ghost;

import dev.xylonity.bonsai.ghosts.common.entity.AbstractGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ai.control.GhostMoveControl;
import dev.xylonity.bonsai.ghosts.common.entity.ai.generic.GhostWanderGoal;
import dev.xylonity.bonsai.ghosts.common.entity.ai.smallghost.SmallGhostPickupSaplingGoal;
import dev.xylonity.bonsai.ghosts.common.entity.ai.smallghost.SmallGhostPlantSaplingGoal;
import dev.xylonity.bonsai.ghosts.common.entity.ai.smallghost.SmallGhostTakeSaplingBlockGoal;
import dev.xylonity.bonsai.ghosts.common.entity.variant.SmallGhostVariant;
import dev.xylonity.bonsai.ghosts.registry.GhostsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

import javax.annotation.Nullable;

public class SmallGhostEntity extends AbstractGhostEntity {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CD_FULL_HIDE = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_STAYING = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SLEEPING = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.BOOLEAN);

    public SmallGhostEntity(EntityType<? extends TamableAnimal> entity, Level world) {
        super(entity, world);

        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.BLOCKED, -1.0F);
        this.setPathfindingMalus(PathType.LEAVES, -1.0F);

        this.moveControl = new GhostMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new SmallGhostPlantSaplingGoal(this, 0.43D, 0.1f, 60, 8));
        this.goalSelector.addGoal(3, new SmallGhostTakeSaplingBlockGoal(this, 10, 0.43D, 0.1f));
        this.goalSelector.addGoal(4, new SmallGhostPickupSaplingGoal(this, 0.43D, 0.1f, 10));

        this.goalSelector.addGoal(9, new GhostWanderGoal(this, 0.43f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !getIsSleeping();
            }

            @Override
            public void start() {
                this.targetPos = new Vec3(wantedX, wantedY, wantedZ);
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
                .add(Attributes.FLYING_SPEED, 0.3F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_STAYING, false);
        builder.define(IS_SLEEPING, false);
        builder.define(DATA_ID_TYPE_VARIANT, 0);
        builder.define(CD_FULL_HIDE, 0);
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
        compoundTag.putBoolean("IsSleeping", getIsSleeping());
    }

    @Override
    protected int getBaseExperienceReward() {
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

        if (level().isClientSide) {
            return;
        }

        if (!getHoldItem().isEmpty() && getHoldItem().is(ItemTags.SAPLINGS)) {
            if (getIsSleeping()) {
                setIsSleeping(false);
            }
            if (getCdFullHide() > 0) {
                setCdFullHide(0);
            }

        }

        if (getIsSleeping() && getCdFullHide() == 36) {
            BlockPos targetBlock = this.blockPosition().below();
            BlockState belowState = level().getBlockState(targetBlock);

            if (!belowState.isAir() && level() instanceof ServerLevel) {
                this.noPhysics = true;
                this.setDeltaMovement(Vec3.ZERO);

                this.setPos(getX(), targetBlock.getY() + 1.0 + 0.15 - (this.getBbHeight() / 2.0), getZ());
            }

        }

        rotateBody();

        if (getCdFullHide() > 0) {
            setCdFullHide(getCdFullHide() - 1);
        }

        SmallGhostVariant variant = getVariant();
        if (variant == SmallGhostVariant.PLANT) {

            if (getHoldItem().isEmpty()) {
                BlockState belowBlockState = level().getBlockState(this.blockPosition().below());
                if (!level().isDay() && (belowBlockState.is(Blocks.GRASS_BLOCK) || belowBlockState.is(Blocks.DIRT))) {
                    if (!getIsSleeping()) {
                        setCdFullHide(36);
                    }

                    setIsSleeping(true);
                }
                else {
                    setIsSleeping(false);
                }

            }
            else {
                setIsSleeping(false);
            }

        }

        if (this.noPhysics && this.getDeltaMovement().lengthSqr() < 1.0E-4) {
            this.noPhysics = false;
            this.setDeltaMovement(Vec3.ZERO);
        }

        if (getIsSleeping() && getCdFullHide() == 26) {
            BlockPos belowPos = this.blockPosition().below();
            BlockState belowState = level().getBlockState(belowPos);

            if (!belowState.isAir() && level() instanceof ServerLevel sl) {
                for (int i = 0; i < 10; i++) {
                    double ox = (random.nextDouble() - 0.5) * 0.6;
                    double oz = (random.nextDouble() - 0.5) * 0.6;
                    double oy = random.nextDouble() * 0.15;
                    sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, belowState), getX() + ox, getY() + 0.05 + oy, getZ() + oz, 1, 0, 0.02, 0, 0.0);
                }

                sl.playSound(null, getX(), getY(), getZ(), belowState.getSoundType().getBreakSound(), getSoundSource(), 0.8f, 0.9f + random.nextFloat() * 0.2f);
            }

        }

    }

    private void moveToPos(Vec3 target, double speed, float lerp) {
        Vec3 tetha = target.subtract(this.position());
        if (tetha.length() < 1.0E-3) return;

        Vec3 vel = tetha.scale(1.0 / tetha.length()).scale(speed);
        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(Mth.lerp(lerp, v.x, vel.x), Mth.lerp(lerp, v.y, vel.y), Mth.lerp(lerp, v.z, vel.z));

        this.getLookControl().setLookAt(target.x, target.y, target.z);
    }

    @Override
    public boolean hurt(DamageSource source, float p) {
        if (source.getEntity() != null) {
            Vec3 vec = AirRandomPos.getPosTowards(this, 32, 32, 32, new Vec3(32, 32, 32), 32);
            if (vec != null) {
                moveToPos(vec, 0.55D, 0.6f);
            }

        }

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
        else if (compoundTag.contains("IsHiding")) {
            this.setIsSleeping(compoundTag.getBoolean("IsHiding"));
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData) {
        setVariant(level.getRandom().nextBoolean() ? SmallGhostVariant.PLANT : SmallGhostVariant.NORMAL);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private <E extends GeoAnimatable> PlayState bodyAC(AnimationState<E> event) {
        if (event.isMoving() && !getIsSleeping()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_move"));
            return PlayState.CONTINUE;
        }

        if (getIsSleeping()) {
            if (getCdFullHide() > 0) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("ghost_bury"));
            }
            else {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("mini_ghost_buried"));
            }

            return PlayState.CONTINUE;
        }

        if (!getIsSleeping() && !event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_idle"));
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState armsAC(AnimationState<E> event) {
        if (!getHoldItem().isEmpty()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("mini_ghost_arms_hold"));
        }
        else {
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
