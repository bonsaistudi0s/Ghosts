package dev.xylonity.bonsai.ghosts.tag;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class GhostsWoodTypes {

    public static void init() {
        ;;
    }

    public static final WoodType HAUNTED = Ghosts.PLATFORM.registerWoodType(Ghosts.of("haunted"), BlockSetType.ACACIA);

}