package dev.xylonity.bonsai.ghosts.common.biome;

import com.mojang.serialization.MapCodec;
import dev.xylonity.bonsai.ghosts.Ghosts;
import dev.xylonity.bonsai.ghosts.registry.GhostsSpawns;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class GhostsSpawnBiomeModifier implements BiomeModifier {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Ghosts.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<GhostsSpawnBiomeModifier>> SERIALIZER =
            BIOME_MODIFIER.register("ghosts_mob_spawns", GhostsSpawnBiomeModifier::makeCodec);

    @Override
    public void modify(Holder<Biome> holder, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            GhostsSpawns.addBiomeSpawns(holder, builder);
        }

    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static MapCodec<GhostsSpawnBiomeModifier> makeCodec() {
        return MapCodec.unit(GhostsSpawnBiomeModifier::new);
    }

}