package dev.xylonity.bonsai.ghosts;

import dev.xylonity.bonsai.ghosts.client.event.GhostsClientEvents;
import dev.xylonity.bonsai.ghosts.common.event.GhostsCommonEvents;
import dev.xylonity.bonsai.ghosts.common.event.GhostsServerEvents;
import dev.xylonity.bonsai.ghosts.config.ConfigManager;
import dev.xylonity.bonsai.ghosts.config.GhostsConfig;
import dev.xylonity.bonsai.ghosts.registry.GhostsSpawns;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class GhostsFabric implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        Ghosts.init();
        GhostsSpawns.init();
        GhostsServerEvents.init();
        GhostsCommonEvents.init();
        ConfigManager.init(FabricLoader.getInstance().getConfigDir(), GhostsConfig.class);
    }

    @Override
    public void onInitializeClient() {
        GhostsClientEvents.init();
    }

}
