package dev.xylonity.bonsai.ghosts.common.entity.ghost;

import dev.xylonity.bonsai.ghosts.common.entity.MainGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ai.control.GhostMoveControl;
import dev.xylonity.bonsai.ghosts.common.entity.ai.generic.*;
import dev.xylonity.bonsai.ghosts.registry.GhostsSounds;
import dev.xylonity.bonsai.ghosts.tag.GhostsTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Map;

public class GhostEntity extends MainGhostEntity {

    private static final EntityDataAccessor<Boolean> SHOULD_RESET_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BLINK_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BLINK_ANIM_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_UNENCHANT = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.BOOLEAN);

    private int cdUnenchant = 0;

    public GhostEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.BLOCKED, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, -1.0F);

        this.moveControl = new GhostMoveControl(this);
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
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FLYING_SPEED, 0.3F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new StayWhenOrderedToGoal(this));
        this.goalSelector.addGoal(7, new GhostFollowOwnerGoal(this, 0.6D, 3.0F, 7.0F, 0.2f));
        this.goalSelector.addGoal(6, new GhostPlaceGoal(this, Ingredient.of(GhostsTags.GHOST_PLACEABLE), state -> true, 6, 10, 0.75, 2));
        this.goalSelector.addGoal(9, new GhostWanderGoal(this, 0.43f));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOULD_RESET_CD, false);
        this.entityData.define(BLINK_CD, 0);
        this.entityData.define(BLINK_ANIM_CD, 0);
        this.entityData.define(SHOULD_UNENCHANT, false);
    }

    public void setHoldItem(ItemStack holdItem) {
        if (this.shouldUnechant()) {
            this.setShouldUnenchant(false);
            this.setCdUnenchant(0);
        }
        if (holdItem.isEnchanted()) {
            this.setShouldUnenchant(true);
            this.setCdUnenchant(82);
        }

        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, holdItem);
    }

    public int getCdUnenchant() {
        return this.cdUnenchant;
    }

    public void setCdUnenchant(int cd) {
        this.cdUnenchant = cd;
    }

    public int getBlinkCd() {
        return this.entityData.get(BLINK_CD);
    }

    public void setBlinkCd(int cd) {
        this.entityData.set(BLINK_CD, cd);
    }

    public int getBlinkAnimCd() {
        return this.entityData.get(BLINK_ANIM_CD);
    }

    public void setBlinkAnimCd(int cd) {
        this.entityData.set(BLINK_ANIM_CD, cd);
    }

    public void setShouldResetCd(boolean shouldResetCd) {
        this.entityData.set(SHOULD_RESET_CD, shouldResetCd);
    }

    public boolean getShouldResetCd() {
        return this.entityData.get(SHOULD_RESET_CD);
    }

    private void setShouldUnenchant(boolean shouldUnenchant) {
        this.entityData.set(SHOULD_UNENCHANT, shouldUnenchant);
    }

    private boolean shouldUnechant() {
        return this.entityData.get(SHOULD_UNENCHANT);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CdUnenchant", getCdUnenchant());
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Main tame handler
        if (!isTame() && stack.getItem() == Items.GLOW_BERRIES) {

            if (!player.getAbilities().instabuild) stack.shrink(1);

            if (this.random.nextInt(3) == 0) {
                this.tame(player);

                this.setPersistenceRequired();
                this.navigation.stop();
                this.setOrderedToSit(true);

                level().broadcastEntityEvent(this, (byte) 7);

                return InteractionResult.SUCCESS;
            }
            else {
                level().broadcastEntityEvent(this, (byte) 6);

                return InteractionResult.FAIL;
            }
        }

        if (level().isClientSide) return InteractionResult.SUCCESS;

        // Heal or cycle owner interaction state (per priority order)
        if (isTame() && player == getOwner()) {
            if (player.isShiftKeyDown()) {
                // Healing
                if (stack.getItem() == Items.GLOW_BERRIES && getHealth() < getMaxHealth()) {
                    this.heal(4f);
                }
                // Armor equipped
                else if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && (Mob.getEquipmentSlotForItem(stack) == EquipmentSlot.HEAD || stack.is(Items.BROWN_MUSHROOM) || stack.is(Items.RED_MUSHROOM))) {
                    ItemStack copy = stack.copy();
                    copy.setCount(1);
                    this.setItemSlotAndDropWhenKilled(EquipmentSlot.HEAD, copy);

                    if (!player.getAbilities().instabuild) stack.shrink(1);

                    return InteractionResult.SUCCESS;
                }
                // Item equipped
                else if (!stack.isEmpty() && getHoldItem().isEmpty()) {
                    this.setHoldItem(stack.copy());
                    stack.setCount(0);
                }
                // Item retrieval
                else if (!getHoldItem().isEmpty()) {
                    this.spawnAtLocation(this.getHoldItem(), 0.5F);
                    setHoldItem(ItemStack.EMPTY);
                }
                // Armor unequipped
                else if (!this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.HEAD), 0.5F);
                    this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                }
            }
            else {
                cycleMainInteraction(player);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public int getExperienceReward() {
        return 1 + level().random.nextInt(2, 4);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        if (level().isClientSide)
            return;

        rotateBody();

        if (getBlinkCd() > 0) {
            setBlinkCd(getBlinkCd() - 1);
        } else {
            if (getBlinkAnimCd() > 0)
                setBlinkAnimCd(getBlinkAnimCd() - 1);
            else {
                if (getShouldResetCd()) {
                    setShouldResetCd(false);

                    setBlinkCd(this.random.nextInt(80, 120));
                } else {
                    setShouldResetCd(true);

                    setBlinkAnimCd(6);
                }
            }
        }

        if (getCdUnenchant() > 0)
            setCdUnenchant(getCdUnenchant() - 1);

        ItemStack heldItemStack = getHoldItem();

        if (heldItemStack.isEnchanted()) {
            if (getCdUnenchant() == 0) {
                if (!this.shouldUnechant()) {
                    startUnenchantAnim();
                } else {
                    this.spawnAtLocation(removeEnchants(heldItemStack), 0.5F);

                    setHoldItem(ItemStack.EMPTY);

                    this.setShouldUnenchant(false);
                }
            }
        }
    }

    /**
     * Handles internal ghost body rotation to match its movement direction
     */
    private void rotateBody() {
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

    private void startUnenchantAnim() {
        this.setCdUnenchant(82);

        this.setShouldUnenchant(true);
    }

    private ItemStack removeEnchants(ItemStack item) {
        ItemStack itemstack = item.copy();

        if (level() instanceof ServerLevel level)
            ExperienceOrb.award(level, this.getPosition(0), getExperienceFromItem(itemstack));

        if (!itemstack.getEnchantmentTags().isEmpty()) {
            int i = random.nextInt(itemstack.getEnchantmentTags().size());

            itemstack.getEnchantmentTags().remove(i);
        }

        itemstack.removeTagKey("StoredEnchantments");

        itemstack.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(itemstack.getBaseRepairCost()));

        return itemstack.copy();
    }

    private int getExperienceFromItem(ItemStack p_39637_) {
        int l = 0;
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_39637_);

        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer integer = entry.getValue();
            if (!enchantment.isCurse()) {
                l += enchantment.getMinCost(integer);
            }
        }

        return l;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("CdUnenchant")) {
            this.setCdUnenchant(compound.getInt("CdUnenchant"));
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return GhostsSounds.GHOST_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return GhostsSounds.GHOST_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return GhostsSounds.GHOST_HURT.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        return super.finalizeSpawn(levelAccessor, difficulty, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "bodyController", 4, this::bodyAC).setOverrideEasingType(EasingType.LINEAR));
        registrar.add(new AnimationController<>(this, "armsController", 4, this::armsAC));
        registrar.add(new AnimationController<>(this, "blinkController", 2, this::blinkAC));
        registrar.add(new AnimationController<>(this, "torch_place_controller", 2, state -> PlayState.STOP).triggerableAnim("torch_place", RawAnimation.begin().thenPlay("torch_place")));
    }

    private <E extends GeoAnimatable> PlayState bodyAC(AnimationState<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_move"));
        }
        else if (isInSittingPose()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_sitting"));
        }
        else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_idle"));
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState blinkAC(AnimationState<E> event) {
        if (getBlinkCd() == 0) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("ghost_blink"));
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState armsAC(AnimationState<E> event) {
        if (!getHoldItem().isEmpty()) {
            if (this.shouldUnechant()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("ghost_unenchant"));
            }
            else {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("ghost_arms_hold"));
            }
        }
        else if (!isInSittingPose()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop(event.isMoving() ? "ghost_move_arms" : "ghost_idle_arms"));
        }

        return PlayState.CONTINUE;
    }

}
