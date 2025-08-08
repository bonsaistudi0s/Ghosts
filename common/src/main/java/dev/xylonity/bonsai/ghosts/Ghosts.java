package dev.xylonity.bonsai.ghosts;

import dev.xylonity.bonsai.ghosts.platform.GhostsPlatform;
import dev.xylonity.bonsai.ghosts.registry.GhostsEntities;
import dev.xylonity.bonsai.ghosts.registry.GhostsItems;
import dev.xylonity.bonsai.ghosts.registry.GhostsSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class Ghosts {

    public static final String MOD_ID = "ghosts";
    public static final Logger LOGGER = LoggerFactory.getLogger("Ghosts");

    public static final GhostsPlatform PLATFORM = ServiceLoader.load(GhostsPlatform.class).findFirst().orElseThrow();

    public static void init() {
        GhostsItems.init();
        GhostsEntities.init();
        GhostsSounds.init();
    }

}