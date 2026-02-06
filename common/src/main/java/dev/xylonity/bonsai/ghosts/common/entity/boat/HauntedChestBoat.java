package dev.xylonity.bonsai.ghosts.common.entity.boat;

import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class HauntedChestBoat extends ChestBoat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(HauntedChestBoat.class, EntityDataSerializers.INT);

    public HauntedChestBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public HauntedChestBoat(Level level, double x, double y, double z) {
        this(GhostsEntities.HAUNTED_CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public Item getDropItem() {
        return GhostsItems.HAUNTED_CHEST_BOAT.get();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", this.getBoatVariant().getSerializedName());
        this.addChestVehicleSaveData(compound, this.registryAccess());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Type", 8)) {
            this.setBoatVariant(HauntedBoat.Type.byName(compound.getString("Type")));
        }

        this.readChestVehicleSaveData(compound, this.registryAccess());
    }

    public void setBoatVariant(HauntedBoat.Type variant) {
        this.entityData.set(DATA_ID_TYPE, variant.ordinal());
    }

    public HauntedBoat.Type getBoatVariant() {
        return HauntedBoat.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_TYPE, HauntedBoat.Type.HAUNTED.ordinal());
    }

}
