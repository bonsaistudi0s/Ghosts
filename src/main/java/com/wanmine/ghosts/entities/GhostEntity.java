package com.wanmine.ghosts.entities;

import com.wanmine.ghosts.entities.goals.GhostsWanderGoal;
import com.wanmine.ghosts.entities.variants.GhostVariant;
import com.wanmine.ghosts.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GhostEntity extends TamableAnimal implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> HOLD_ITEM = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> CD_UNENCHANT = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> SHOULD_RESET_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BLINK_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BLINK_ANIM_CD = SynchedEntityData.defineId(GhostEntity.class, EntityDataSerializers.INT);

    private int cdTorch = 100;

    public GhostEntity(EntityType<? extends TamableAnimal> entity, Level world) {
        super(entity, world);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setTame(false);
        this.isSensitiveToWater();
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
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 15.0F, 2.0F, false));
        this.goalSelector.addGoal(9, new GhostsWanderGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
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
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FLYING_SPEED, 0.3F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .build();
    }

    @Override
    public boolean causeFallDamage(float p_148989_, float p_148990_, @NotNull DamageSource p_148991_) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel p_146743_, @NotNull AgeableMob p_146744_) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
        this.entityData.define(CD_UNENCHANT, 0);
        this.entityData.define(HOLD_ITEM, ItemStack.EMPTY);
        this.entityData.define(SHOULD_RESET_CD, false);
        this.entityData.define(BLINK_CD, 0);
        this.entityData.define(BLINK_ANIM_CD, 0);
    }

    public void setHoldItem(ItemStack holdItem) {
        this.entityData.set(HOLD_ITEM, holdItem.copy());
    }

    public ItemStack getHoldItem() {
        return this.entityData.get(HOLD_ITEM).copy();
    }

    public int getCdUnenchant() {
        return this.entityData.get(CD_UNENCHANT);
    }

    public void setCdUnenchant(int cd) {
        this.entityData.set(CD_UNENCHANT, cd);
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

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", getVariant().getId());
        compoundTag.putInt("CdUnenchant", getCdUnenchant());
        if (getHoldItem() != ItemStack.EMPTY)
            compoundTag.put("InvSlot", getHoldItem().save(new CompoundTag()));
    }

    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);

        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(player) && this.isTame();

            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (itemstack.is(Items.GLOW_BERRIES) && !this.isTame()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.setPersistenceRequired();
                    this.navigation.stop();
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }

                return InteractionResult.SUCCESS;
            } else if (!itemstack.is(Items.GLOW_BERRIES) && this.isTame() && this.isOwnedBy(player) && getHoldItem() == ItemStack.EMPTY && !itemstack.is(ItemStack.EMPTY.getItem())) {
                ItemStack stack = itemstack.copy();

                setHoldItem(stack);

                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(itemstack.getCount());
                }

                return InteractionResult.SUCCESS;
            } else if (itemstack.is(ItemStack.EMPTY.getItem()) && this.isTame() && player.isShiftKeyDown() && this.isOwnedBy(player) && getHoldItem() != ItemStack.EMPTY) {
                ItemEntity myItemEntity = new ItemEntity(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), getHoldItem());
                this.level.addFreshEntity(myItemEntity);

                setHoldItem(ItemStack.EMPTY);

                return InteractionResult.SUCCESS;
            } else if (!player.isShiftKeyDown() && this.isTame() && this.isOwnedBy(player)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.navigation.stop();

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected int getExperienceReward(Player p_27590_) {
        return 1 + this.level.random.nextInt(2, 4);
    }

    private boolean shouldUnenchant = false;

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide()) {
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
        }

        if (getCdUnenchant() > 0)
            setCdUnenchant(getCdUnenchant() - 1);

        ItemStack heldItemStack = getHoldItem();
        if (heldItemStack.is(Items.TORCH) && cdTorch > 0)
            cdTorch--;

        if (heldItemStack.isEnchanted()) {
            if (getCdUnenchant() == 0) {
                if (!shouldUnenchant) {
                    setCdUnenchant(82);

                    shouldUnenchant = true;
                } else {
                    setHoldItem(removeEnchants(heldItemStack));

                    ItemEntity myItemEntity = new ItemEntity(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), heldItemStack);
                    this.level.addFreshEntity(myItemEntity);

                    setHoldItem(ItemStack.EMPTY);

                    shouldUnenchant = false;
                }
            }
        } else if (heldItemStack.is(Items.TORCH) && cdTorch == 0) {
            for (BlockPos pos : BlockPos.spiralAround(this.blockPosition(), 1, Direction.EAST, Direction.SOUTH)) {
                int below = 0;
                while (this.level.getBlockState(pos).isAir()) {
                    pos = pos.below();
                    below++;
                    if (below == 5)
                        break;
                }

                if (below == 5)
                    continue;

                pos = pos.above();
                if (level.getBrightness(LightLayer.BLOCK, pos) >= 4)
                    continue;

                BlockPos belowPos = pos.below();
                if (level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP) && level.getBlockState(pos).isAir()) {
                    level.setBlock(pos, Blocks.TORCH.defaultBlockState(), 0);

                    if (heldItemStack.getCount() == 1) {
                        setHoldItem(ItemStack.EMPTY);
                    } else {
                        heldItemStack.setCount(heldItemStack.getCount() - 1);
                        setHoldItem(heldItemStack.copy());
                    }

                    break;
                }
            }

            cdTorch = 100;
        }
    }

    private ItemStack removeEnchants(ItemStack item) {
        ItemStack itemstack = item.copy();

        if (!this.getLevel().isClientSide())
            ExperienceOrb.award((ServerLevel) this.getLevel(), this.getPosition(0), getExperienceFromItem(itemstack));

        if (itemstack.getEnchantmentTags().size() > 0) {
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
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("Variant")) {
            this.setVariant(compoundTag.getInt("Variant"));
        }
        if (compoundTag.contains("CdUnenchant")) {
            this.setCdUnenchant(compoundTag.getInt("CdUnenchant"));
        }
        if (compoundTag.contains("InvSlot")) {
            this.setHoldItem(ItemStack.of((CompoundTag) Objects.requireNonNull(compoundTag.get("InvSlot"))));
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GHOST_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GHOST_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.GHOST_HURT.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType mobSpawnType,
            @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        setVariant(levelAccessor.getRandom().nextBoolean() ? GhostVariant.MUSHROOM : GhostVariant.NORMAL);

        return super.finalizeSpawn(levelAccessor, difficulty, mobSpawnType, spawnGroupData, compoundTag);
    }

    private <E extends IAnimatable> PlayState bodyAC(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_move", true));

            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_idle", true));

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState blinkAC(AnimationEvent<E> event) {
        if (getBlinkCd() == 0) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_blink"));
        }

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState armsAC(AnimationEvent<E> event) {
        if (event.isMoving()) {
            if (getHoldItem() != ItemStack.EMPTY) {
                if (shouldUnenchant)
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_unenchant"));
                else
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_arms_hold", true));
            } else
                event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_move_arms", true));
            return PlayState.CONTINUE;
        }

        if (getHoldItem() != ItemStack.EMPTY) {
            if (shouldUnenchant)
                event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_unenchant"));
            else
                event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_arms_hold", true));
        } else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("ghost_idle_arms", true));

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "ghost_animation_controller_body", 1, EasingType.Linear, this::bodyAC));
        data.addAnimationController(new AnimationController<>(this, "ghost_animation_controller_arms", 1, this::armsAC));
        data.addAnimationController(new AnimationController<>(this, "ghost_animation_controller_blink", 1, this::blinkAC));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
