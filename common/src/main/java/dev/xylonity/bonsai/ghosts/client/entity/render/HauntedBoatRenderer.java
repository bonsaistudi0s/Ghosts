package dev.xylonity.bonsai.ghosts.client.entity.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.boat.HauntedBoat;
import dev.xylonity.bonsai.ghosts.common.entity.boat.HauntedChestBoat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Map;
import java.util.stream.Stream;

public class HauntedBoatRenderer extends BoatRenderer {

    private final Map<HauntedBoat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public HauntedBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);
        this.boatResources = Stream.of(HauntedBoat.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type,
                type -> Pair.of(Ghosts.of(getTextureLocation(type, chestBoat)), this.createBoatModel(context, type, chestBoat))));
    }

    private static String getTextureLocation(HauntedBoat.Type pType, boolean pChestBoat) {
        return pChestBoat ? "textures/entity/chest_boat/" + pType.getName() + ".png" : "textures/entity/boat/" + pType.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context pContext, HauntedBoat.Type pType, boolean pChestBoat) {
        ModelLayerLocation modellayerlocation = pChestBoat ? HauntedBoatRenderer.createChestBoatModelName(pType) : HauntedBoatRenderer.createBoatModelName(pType);
        ModelPart modelpart = pContext.bakeLayer(modellayerlocation);
        return pChestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    public static ModelLayerLocation createBoatModelName(HauntedBoat.Type pType) {
        return createLocation("boat/" + pType.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(HauntedBoat.Type pType) {
        return createLocation("chest_boat/" + pType.getName(), "main");
    }

    private static ModelLayerLocation createLocation(String pPath, String pModel) {
        return new ModelLayerLocation(Ghosts.of(pPath), pModel);
    }

    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat rawBoat) {
        if (rawBoat instanceof HauntedBoat boat) {
            return this.boatResources.get(boat.getBoatVariant());
        }
        else if (rawBoat instanceof HauntedChestBoat boat) {
            return this.boatResources.get(boat.getBoatVariant());
        }
        else {
            return null;
        }

    }

}