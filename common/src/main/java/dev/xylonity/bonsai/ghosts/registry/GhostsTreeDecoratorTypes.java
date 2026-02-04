package dev.xylonity.bonsai.ghosts.registry;

import com.mojang.serialization.Codec;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.configurations.tree.HangingLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.function.Supplier;

public final class GhostsTreeDecoratorTypes {

    public static void init() { ;; }

    public static Supplier<TreeDecoratorType<HangingLeavesDecorator>> HAUNTED_TREE_HANGING_LEAVES = register("hanging_leaves", HangingLeavesDecorator.CODEC);

    private static <U extends TreeDecorator> Supplier<TreeDecoratorType<U>> register(String id, Codec<U> codec) {
        return Ghosts.PLATFORM.registerTreeDecorator(id, codec);
    }

}
