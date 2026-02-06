package dev.xylonity.bonsai.ghosts.common.entity.boat;

import dev.xylonity.bonsai.ghosts.registry.GhostsBlocks;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.IntFunction;

public class HauntedBoat extends Boat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(HauntedBoat.class, EntityDataSerializers.INT);

    public HauntedBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public HauntedBoat(Level level, double x, double y, double z) {
        this(GhostsEntities.HAUNTED_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public Item getDropItem() {
        return GhostsItems.HAUNTED_BOAT.get();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", this.getBoatVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Type", 8)) {
            this.setBoatVariant(Type.byName(compound.getString("Type")));
        }

    }

    public void setBoatVariant(Type variant) {
        this.entityData.set(DATA_ID_TYPE, variant.ordinal());
    }

    public Type getBoatVariant() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_TYPE, Type.HAUNTED.ordinal());
    }

    public enum Type implements StringRepresentable {
        HAUNTED(GhostsBlocks.HAUNTED_PLANKS.get(), "haunted");

        private final String name;
        private final Block planks;
        public static final EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private static final IntFunction<Type> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        Type(Block planks, String name) {
            this.name = name;
            this.planks = planks;
        }

        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.planks;
        }

        public String toString() {
            return this.name;
        }

        public static Type byId(int id) {
            return BY_ID.apply(id);
        }

        public static Type byName(String name) {
            return CODEC.byName(name, HAUNTED);
        }

    }

}
