package com.wanmine.ghosts.entities;

import com.wanmine.ghosts.entities.goals.GhostsWanderGoal;
import com.wanmine.ghosts.entities.variants.SmallGhostVariant;
import com.wanmine.ghosts.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;

public class SmallGhostEntity extends TamableAnimal implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CD_FULL_HIDE = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_STAYING = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SLEEPING = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> HOLD_ITEM = SynchedEntityData.defineId(SmallGhostEntity.class, EntityDataSerializers.ITEM_STACK);

    private int treeCd = 0;

    public SmallGhostEntity(EntityType<? extends TamableAnimal> entity, Level world) {
        super(entity, world);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setTame(false);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.BLOCKED, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, -1.0F);
    }

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        return MobCategory.MONSTER;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new GhostsWanderGoal(this, 1.0D));
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level p_29417_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_29417_);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public static AttributeSupplier setAttributes() {
        return AbstractGolem.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.FLYING_SPEED, 0.22f)
                .add(Attributes.MOVEMENT_SPEED, 0.22F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .build();
    }

    public boolean causeFallDamage(float p_148989_, float p_148990_, DamageSource p_148991_) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel p_146743_, @NotNull AgeableMob p_146744_) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_STAYING, false);
        this.entityData.define(IS_SLEEPING, false);
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
        this.entityData.define(CD_FULL_HIDE, 0);
        this.entityData.define(HOLD_ITEM, ItemStack.EMPTY);
    }

    public void setIsStaying(boolean isStaying) {
        this.entityData.set(IS_STAYING, isStaying);
    }

    public boolean getIsStaying() {
        return this.entityData.get(IS_STAYING);
    }

    public void setHoldItem(ItemStack holdItem) {
        this.entityData.set(HOLD_ITEM, holdItem.copy());
    }

    public ItemStack getHoldItem() {
        return this.entityData.get(HOLD_ITEM).copy();
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
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", getVariant().getId());
        compoundTag.putInt("CdFullHide", getCdFullHide());
        compoundTag.putBoolean("IsStaying", getIsStaying());
        compoundTag.putBoolean("IsHiding", getIsSleeping());
        if (getHoldItem() != ItemStack.EMPTY)
            compoundTag.put("InvSlot", getHoldItem().save(new CompoundTag()));
    }

    @Override
    protected int getExperienceReward(Player p_27590_) {
        return 1 + this.level.random.nextInt(2, 4);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide()) {
            if (getCdFullHide() > 0) {
                setCdFullHide(getCdFullHide() - 1);
            }

            if (!(getVariant() == SmallGhostVariant.NORMAL_40 || getVariant() == SmallGhostVariant.NORMAL_80)) {
                if (!level.isDay() && level.getBlockState(this.blockPosition().below()).getBlock() != Blocks.AIR && (level.getBlockState(this.blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK || level.getBlockState(this.blockPosition().below()).getBlock() == Blocks.DIRT)) {
                    if (!getIsSleeping())
                        setCdFullHide(36);

                    setIsSleeping(true);
                } else {
                    setIsSleeping(false);
                }
            }
        }

        if (treeCd == 0) {
            if (getHoldItem() != ItemStack.EMPTY) {
                if (level.getBlockState(this.blockPosition().below()).getBlock() == Blocks.DIRT || level.getBlockState(this.blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK) {
                    if (level.getBlockState(this.blockPosition()).getBlock() == Blocks.AIR) {
                        switch (Objects.requireNonNull(getHoldItem().getItem().getRegistryName()).getPath()) {
                            default -> level.setBlock(this.blockPosition(), Blocks.OAK_SAPLING.defaultBlockState(), 0);
                            case "birch_sapling" -> level.setBlock(this.blockPosition(), Blocks.BIRCH_SAPLING.defaultBlockState(), 0);
                            case "jungle_sapling" -> level.setBlock(this.blockPosition(), Blocks.JUNGLE_SAPLING.defaultBlockState(), 0);
                            case "dark_oak_sapling" -> level.setBlock(this.blockPosition(), Blocks.DARK_OAK_SAPLING.defaultBlockState(), 0);
                            case "acacia_sapling" -> level.setBlock(this.blockPosition(), Blocks.ACACIA_SAPLING.defaultBlockState(), 0);
                        }

                        setHoldItem(ItemStack.EMPTY);

                        treeCd = random.nextInt(60, 120);
                    }
                }
            }
        } else
            treeCd--;

        List<ItemEntity> itemEntity = level.getEntitiesOfClass(ItemEntity.class, new AABB(this.blockPosition().offset(-10, -10, -10), this.blockPosition().offset(10, 10, 10)));

        if (!itemEntity.isEmpty() && getHoldItem().getCount() < 64) {
            itemEntity.iterator().forEachRemaining((entity) -> {
                if ((entity.getItem().getItem() == Items.OAK_SAPLING || entity.getItem().getItem() == Items.SPRUCE_SAPLING || entity.getItem().getItem() == Items.DARK_OAK_SAPLING || entity.getItem().getItem() == Items.BIRCH_SAPLING || entity.getItem().getItem() == Items.ACACIA_SAPLING || entity.getItem().getItem() == Items.JUNGLE_SAPLING) && getHoldItem() == ItemStack.EMPTY && getHoldItem().isEmpty()) {
                    Path path = this.navigation.createPath(entity.blockPosition(), 1);

                    if (path != null)
                        this.navigation.moveTo(path, 2.0D);

                    if (this.blockPosition().distToCenterSqr(entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ()) <= 1 && this.blockPosition().distToCenterSqr(entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ()) >= -1) {
                        ItemStack stack = entity.getItem().copy();

                        if (entity.getItem().getCount() > 1) {
                            entity.getItem().setCount(entity.getItem().getCount() - 1);

                            stack.setCount(1);
                        } else {
                            entity.discard();
                        }

                        setHoldItem(stack.copy());
                    }
                }
            });
        }
    }

    @Override
    public boolean hurt(DamageSource p_27567_, float p_27568_) {
        if (p_27567_.getEntity() != null) {
            Vec3 vec = AirRandomPos.getPosTowards(this, 32, 32, 32, new Vec3(32, 32, 32), 32);

            if (vec != null) {
                Path path = this.navigation.createPath(new BlockPos(vec.x, vec.y, vec.z), 2);

                if (path != null)
                    this.navigation.moveTo(path, 2.0D);
            }
        }

        if (this.getIsSleeping())
            return false;
        else
            return super.hurt(p_27567_, p_27568_);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
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
        if (compoundTag.contains("InvSlot")) {
            this.setHoldItem(ItemStack.of((CompoundTag) Objects.requireNonNull(compoundTag.get("InvSlot"))));
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SMALL_GHOST_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SMALL_GHOST_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.SMALL_GHOST_HURT.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        int rand = new Random().nextInt(20) + 1;

        if (rand == 1) {
            int rand1 = new Random().nextInt(20) + 1;

            if (rand1 == 1)
                setVariant(SmallGhostVariant.NORMAL_80);
            else
                setVariant(SmallGhostVariant.NORMAL_40);
        }
        else {
            int rand1 = new Random().nextInt(20) + 1;

            if (rand1 == 1)
                setVariant(SmallGhostVariant.PLANT_80);
            else
                setVariant(SmallGhostVariant.PLANT_40);
        }

        return super.finalizeSpawn(levelAccessor, difficulty, mobSpawnType, spawnGroupData, compoundTag);
    }

    private <E extends IAnimatable> PlayState bodyAC(AnimationEvent<E> event) {
        if (event.isMoving() && !getIsSleeping()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.ghost_move", true));

            return PlayState.CONTINUE;
        }

        if (getIsSleeping()) {
            if (getCdFullHide() > 0)
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.ghost_bury"));
            else
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.mini_ghost_buried"));

            return PlayState.CONTINUE;
        }

        if (!getIsSleeping() && !event.isMoving())
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.ghost_idle", true));

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState armsAC(AnimationEvent<E> event) {
        if (event.isMoving()) {
            if (getHoldItem() != ItemStack.EMPTY) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.mini_ghost_arms_hold", true));
            } else
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.ghost_move_arms", true));
            return PlayState.CONTINUE;
        }

        if (getHoldItem() != ItemStack.EMPTY) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.mini_ghost_arms_hold", true));
        } else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.small_ghost.ghost_idle_arms", true));

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "small_ghost_animation_controller_body", 0, this::bodyAC));
        data.addAnimationController(new AnimationController<>(this, "small_ghost_animation_controller_arms", 0, this::armsAC));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
