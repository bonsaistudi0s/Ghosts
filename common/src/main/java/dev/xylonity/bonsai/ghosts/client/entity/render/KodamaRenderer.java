package dev.xylonity.bonsai.ghosts.client.entity.render;

import dev.xylonity.bonsai.ghosts.client.entity.layer.KodamaGlowLayer;
import dev.xylonity.bonsai.ghosts.client.entity.model.KodamaModel;
import dev.xylonity.bonsai.ghosts.client.entity.render.core.BaseGhostRenderer;
import dev.xylonity.bonsai.ghosts.common.entity.kodama.KodamaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KodamaRenderer extends BaseGhostRenderer<KodamaEntity> {

    public KodamaRenderer(EntityRendererProvider.Context context) {
        super(context, new KodamaModel());
        this.renderLayers.addLayer(new KodamaGlowLayer(this));
    }

}
