package dev.xylonity.bonsai.ghosts.common.block;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class HauntedButton extends ButtonBlock {

    public HauntedButton(Properties properties, BlockSetType type, int ticksToStayPressed, boolean arrowsCanPress) {
        super(properties, type, ticksToStayPressed, arrowsCanPress);
    }

}
