package dev.xylonity.bonsai.ghosts.common.entity.kodama;

import dev.xylonity.bonsai.ghosts.common.entity.PassiveEntity;
import dev.xylonity.bonsai.ghosts.registry.GhostsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class KodamaEntity extends PassiveEntity {

    private final AnimatableInstanceCache cache = new InstancedAnimatableInstanceCache(this);

    private final RawAnimation RATTLE = RawAnimation.begin().thenPlay("rattle");
    private final RawAnimation RATTLE_2 = RawAnimation.begin().thenPlay("rattle_2");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(KodamaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RATTLING_TICKS = SynchedEntityData.defineId(KodamaEntity.class, EntityDataSerializers.INT);

    private static final int ANIMATION_RATTLING_TICKS = 38;

    private float flashAlpha;
    private int rattleAnimationType;

    public KodamaEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.flashAlpha = 0;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return AbstractGolem.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.FLYING_SPEED, 0.3F)
                .add(Attributes.MOVEMENT_SPEED, 0.24f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F, 1f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(RATTLING_TICKS, 0);
    }

    public void setFlashAlpha(float flashAlpha) {
        this.flashAlpha = flashAlpha;
    }

    public float getFlashAlpha() {
        return flashAlpha;
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setRattlingTicks(int variant) {
        this.entityData.set(RATTLING_TICKS, variant);
    }

    public int getRattlingTicks() {
        return this.entityData.get(RATTLING_TICKS);
    }

    protected boolean shouldPanic() {
        return this.getLastHurtByMob() != null || this.isFreezing() || this.isOnFire();
    }

    @Override
    public void tick() {
        Vec3 currentPos = position();

        super.tick();

        if (!level().isClientSide) {
            long dayTime = level().getDayTime() % 24000;

            if (!shouldPanic() && getRattlingTicks() <= 0) {
                if (dayTime >= 13000 && dayTime <= 13200) {
                    startRattling(0.3f);
                }
                else {
                    startRattling(0.001f);
                }

            }

            boolean ok = !shouldPanic() && getRattlingTicks() > 0 && onGround();
            if (!ok) {
                setRattlingTicks(0);
            }

            if (getRattlingTicks() > 0) setRattlingTicks(getRattlingTicks() - 1);

            if (getRattlingTicks() > 0) {
                getNavigation().stop();
                setPos(currentPos);
            }

        }

    }

    private void startRattling(float chance) {
        if (level().random.nextFloat() > chance) {
            return;
        }

        setRattlingTicks(ANIMATION_RATTLING_TICKS);
        level().playSound(null, blockPosition(), GhostsSounds.KODAMA_RATTLE.get(), SoundSource.AMBIENT, 1, 1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Variant")) {
            setVariant(compound.getInt("Variant"));
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", getVariant());
    }

    public static boolean checkKodamaSpawnRules(EntityType<? extends Animal> kodama, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return isDarkEnoughToSpawn(level, pos, random) && checkMobSpawnRules(kodama, level, spawnType, pos, random);
    }

    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
        if (level.getBrightness(LightLayer.SKY, pos) > random.nextInt(32)) {
            return false;
        }
        else {
            DimensionType dimensiontype = level.dimensionType();
            int i = dimensiontype.monsterSpawnBlockLightLimit();
            if (i < 15 && level.getBrightness(LightLayer.BLOCK, pos) > i) {
                return false;
            }
            else {
                int j = level.getLevel().isThundering() ? level.getMaxLocalRawBrightness(pos, 10) : level.getMaxLocalRawBrightness(pos);
                return j <= dimensiontype.monsterSpawnLightTest().sample(random);
            }
        }

    }

    @Override
    public int getExperienceReward() {
        return level().random.nextInt(4) + 2;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag) {
        if (getVariant() == 0) {
            setVariant(level.getLevel().random.nextInt(4) + 1);
        }

        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return GhostsSounds.KODAMA_IDLE.get();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getRattlingTicks() == ANIMATION_RATTLING_TICKS - 1) {
            rattleAnimationType = level().random.nextInt(2);
        }

        if (getRattlingTicks() > 0) {
            event.setAnimation(rattleAnimationType == 0 ? RATTLE : RATTLE_2);
        }
        else if (event.isMoving()) {
            event.setAnimation(WALK);
        }
        else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}