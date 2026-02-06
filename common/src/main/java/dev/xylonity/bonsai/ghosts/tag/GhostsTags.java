package dev.xylonity.bonsai.ghosts.tag;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class GhostsTags {
    public static final TagKey<Item> GHOST_PLACEABLE = TagKey.create(Registries.ITEM, Ghosts.of("placeable"));
}