package dev.xylonity.bonsai.ghosts.tag;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.lang.reflect.Method;

public class GhostsWoodTypes {

    public static final WoodType HAUNTED = registerWoodType(
            Ghosts.MOD_ID + ":haunted",
            BlockSetType.ACACIA
    );

    private static WoodType registerWoodType(String name, BlockSetType blockSetType) {
        try {
            Method registerMethod = WoodType.class.getDeclaredMethod("register", WoodType.class);
            registerMethod.setAccessible(true);

            WoodType woodType = new WoodType(name, blockSetType);
            return (WoodType) registerMethod.invoke(null, woodType);
        }
        catch (Exception exception) {
            throw new RuntimeException("[Ghosts] Cannot register WoodType: " + name, exception);
        }

    }

}