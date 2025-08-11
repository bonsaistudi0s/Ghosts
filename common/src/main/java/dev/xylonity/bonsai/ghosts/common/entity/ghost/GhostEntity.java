package dev.xylonity.bonsai.ghosts.common.entity.ghost;

import dev.xylonity.bonsai.ghosts.common.entity.MainGhostEntity;
import dev.xylonity.bonsai.ghosts.common.entity.ai.control.GhostMoveControl;
import dev.xylonity.bonsai.ghosts.common.entity.ai.generic.*;
import dev.xylonity.bonsai.ghosts.common.entity.variant.GhostVariant;
import dev.xylonity.bonsai.ghosts.registry.GhostsSounds;
import dev.xylonity.bonsai.ghosts.tag.GhostsTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EnchantmentTags;
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
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GhostEntity extends MainGhostEntity {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_RESET_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BLINK_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BLINK_ANIM_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_UNENCHANT = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.BOOLEAN);

    private int cdUnenchant = 0;

    public GhostEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.BLOCKED, -1.0F);
        this.setPathfindingMalus(PathType.LEAVES, -1.0F);

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
        this.goalSelector.addGoal(6, new GhostFollowOwnerGoal(this, 0.6D, 3.0F, 7.0F, 0.2f));
        this.goalSelector.addGoal(7, new GhostPlaceGoal(this, Ingredient.of(GhostsTags.GHOST_PLACEABLE), state -> true, 6, 10));
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_TYPE_VARIANT, 0);
        builder.define(SHOULD_RESET_CD, false);
        builder.define(BLINK_CD, 0);
        builder.define(BLINK_ANIM_CD, 0);
        builder.define(SHOULD_UNENCHANT, false);
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

    public int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    public GhostVariant getVariant() {
        return GhostVariant.byId(this.getTypeVariant() & 255);
    }

    public void setVariant(GhostVariant variant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant & 255);
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
        compound.putInt("Variant", getVariant().getId());
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
                else if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && getEquipmentSlotForItem(stack) == EquipmentSlot.HEAD) {
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
    protected int getBaseExperienceReward() {
        return 1 + level().random.nextInt(2, 4);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        if (level().isClientSide)
            return;

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

        if (getCdUnenchant() > 0) setCdUnenchant(getCdUnenchant() - 1);

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

    private void startUnenchantAnim() {
        this.setCdUnenchant(82);

        this.setShouldUnenchant(true);
    }

    private ItemStack removeEnchants(ItemStack item) {
        ItemStack stack = item.copy();

        if (level() instanceof ServerLevel srv) {
            ExperienceOrb.award(srv, this.getPosition(0), getExperienceFromItem(stack));
        }

        ItemEnchantments normal = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

        boolean storedComp = !stored.isEmpty();
        ItemEnchantments target = storedComp ? stored : normal;

        if (!target.isEmpty()) {
            // A bit cryptic ngl
            List<Holder<Enchantment>> keys = new ArrayList<>(target.keySet());
            Holder<Enchantment> rem = keys.get(random.nextInt(keys.size()));

            ItemEnchantments.Mutable mut = new ItemEnchantments.Mutable(target);

            mut.set(rem, 0);

            ItemEnchantments imm = mut.toImmutable();
            if (storedComp) {
                if (imm.isEmpty()) stack.remove(DataComponents.STORED_ENCHANTMENTS);
                else stack.set(DataComponents.STORED_ENCHANTMENTS, imm);
            } else {
                if (imm.isEmpty()) stack.remove(DataComponents.ENCHANTMENTS);
                else stack.set(DataComponents.ENCHANTMENTS, imm);
            }
        }

        stack.remove(DataComponents.STORED_ENCHANTMENTS);

        stack.set(DataComponents.REPAIR_COST, AnvilMenu.calculateIncreasedRepairCost(stack.getOrDefault(DataComponents.REPAIR_COST, 0)));

        return stack.copy();
    }

    private int getExperienceFromItem(ItemStack stack) {
        return xpFrom(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)) + xpFrom(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));
    }

    private int xpFrom(ItemEnchantments ench) {
        int xp = 0;
        for (Holder<Enchantment> holder : ench.keySet()) {

            if (holder.is(EnchantmentTags.CURSE)) continue;

            int lvl = ench.getLevel(holder);
            if (lvl > 0) xp += holder.value().getMinCost(lvl);
        }

        return xp;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Variant")) {
            this.setVariant(compound.getInt("Variant"));
        }
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData spawnGroupData) {
        setVariant(level.getRandom().nextBoolean() ? GhostVariant.MUSHROOM : GhostVariant.NORMAL);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "bodyController", 2, this::bodyAC).setOverrideEasingType(EasingType.LINEAR));
        registrar.add(new AnimationController<>(this, "armsController", 2, this::armsAC));
        registrar.add(new AnimationController<>(this, "blinkController", 2, this::blinkAC));
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
