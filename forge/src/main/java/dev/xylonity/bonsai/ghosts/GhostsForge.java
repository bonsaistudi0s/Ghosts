package dev.xylonity.bonsai.ghosts;

import net.minecraftforge.fml.common.Mod;

@Mod(Ghosts.MOD_ID)
public class GhostsForge {

    public static final String MOD_ID = Ghosts.MOD_ID;

    public GhostsForge() {


        Ghosts.init();
    }

}