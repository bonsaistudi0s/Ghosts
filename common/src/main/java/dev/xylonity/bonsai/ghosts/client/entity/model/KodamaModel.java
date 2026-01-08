package dev.xylonity.bonsai.ghosts.client.entity.model;

import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class KodamaModel extends GeoModel<KodamaEntity> {

    private final float yawMultiply = 0.5f;
    private final float maxYawDegrees = 60f;
    private final float maxPitchDegrees = 45f;

    @Override
    public ResourceLocation getModelResource(KodamaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "geo/kodama_" + animatable.getVariant() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KodamaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "textures/entity/kodama_" + animatable.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(KodamaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ghosts.MOD_ID, "animations/kodama.animation.json");
    }

    @Override
    public void setCustomAnimations(KodamaEntity animatable, long instanceId, AnimationState<KodamaEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head == null) {
            return;
        }

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        boolean headAnimActive = animatable.getRattlingTicks() > 0;

        // Geckolib leaves some kind of "residue" between render calls, so the head rotation on certain animations is passed between
        // entities using the same model. Here I clean that "residue" to avoid the head glitch bug
        if (entityData == null) {
            if (!headAnimActive) {
                head.setRotX(0f);
                head.setRotY(0f);
                head.setRotZ(0f);
            }

            return;
        }

        float pitchDegrees = Mth.clamp(entityData.headPitch(), -maxPitchDegrees, maxPitchDegrees);
        float yawDegrees = Mth.wrapDegrees(entityData.netHeadYaw());
        yawDegrees = Mth.clamp(yawDegrees, -maxYawDegrees, maxYawDegrees) * yawMultiply;

        float pitchRadians = pitchDegrees * Mth.DEG_TO_RAD;
        float yawRadians = yawDegrees * Mth.DEG_TO_RAD;

        if (!headAnimActive) {
            head.setRotX(pitchRadians);
            head.setRotY(yawRadians);
            head.setRotZ(0f);
            return;
        }

        head.setRotX(head.getRotX() + pitchRadians);
        head.setRotY(head.getRotY() + yawRadians);
    }

}
