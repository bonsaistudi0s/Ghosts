package dev.xylonity.bonsai.ghosts.client.entity.model;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class KodamaModel extends GeoModel<KodamaEntity> {

    @Override
    public ResourceLocation getModelResource(KodamaEntity animatable) {
        return new ResourceLocation(Ghosts.MOD_ID, "geo/kodama_" + animatable.getVariant() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KodamaEntity animatable) {
        return new ResourceLocation(Ghosts.MOD_ID, "textures/entity/kodama_" + animatable.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(KodamaEntity animatable) {
        return new ResourceLocation(Ghosts.MOD_ID, "animations/kodama.animation.json");
    }

    @Override
    public void setCustomAnimations(KodamaEntity animatable, long instanceId, AnimationState<KodamaEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

    }

}
