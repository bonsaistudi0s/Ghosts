package dev.xylonity.bonsai.ghosts.registry;

import dev.xylonity.bonsai.ghosts.Ghosts;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public class GhostsParticles {

    public static void init() { ;; }

    public static final Supplier<SimpleParticleType> FLYING_GHOST = registerParticle("flying_ghost", true);

    private static <T extends SimpleParticleType> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return Ghosts.PLATFORM.registerParticle(id, overrideLimiter);
    }

}
