package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class GhostsCreativeTabs {

    public static void init() { ;; }

    public static final Supplier<CreativeModeTab> CREATIVE_TAB =
            Ghosts.PLATFORM.registerCreativeTab("ghosts_creative_tab",
                    () -> Ghosts.PLATFORM.creativeTabBuilder().icon(() -> new ItemStack(GhostsBlocks.HAUNTED_SAPLING.get()))
                            .title(Component.translatable("creativetab.ghosts.title"))
                            .displayItems((itemDisplayParameters, output) -> {
                                output.accept(GhostsBlocks.HAUNTED_SAPLING.get());
                                output.accept(GhostsBlocks.HAUNTED_LOG.get());
                                output.accept(GhostsBlocks.HAUNTED_PLANKS.get());
                                output.accept(GhostsBlocks.HAUNTED_LEAVES.get());
                                output.accept(GhostsBlocks.HAUNTED_EYE_LOG.get());
                                //output.accept(GhostsBlocks.HAUNTED_DOOR.get());
                                output.accept(GhostsItems.GHOST_SPAWN_EGG.get());
                                output.accept(GhostsItems.SMALL_GHOST_SPAWN_EGG.get());
                                output.accept(GhostsItems.KODAMA_SPAWN_EGG.get());
                            })
                            .build());

}
