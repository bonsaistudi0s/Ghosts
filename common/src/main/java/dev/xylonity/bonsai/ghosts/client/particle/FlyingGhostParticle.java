package dev.xylonity.bonsai.ghosts.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

import java.util.Random;

public class FlyingGhostParticle extends TextureSheetParticle {

    private static final int FRAME_TICKS = 4;
    private static final int FRAMES = 8;

    private final SpriteSet spritesset;
    private int lastFrame = -1;

    private final double startY;
    private final float floatSpeed;
    private final float floatAmplitude;

    FlyingGhostParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ) {
        super(world, x, y + 0.5, z, 0.0, 0.0, 0.0);

        this.quadSize = 0.2f;
        this.rCol = 1F;
        this.gCol = 1F;
        this.bCol = 1F;
        this.lifetime = new Random().nextInt(800, 1200);
        this.setSpriteFromAge(sprites);
        this.spritesset = sprites;

        this.gravity = 0.03F;
        this.friction = 0.99f;

        this.startY = this.y;
        this.floatSpeed = 0.03f + random.nextFloat() * 0.02f;
        this.floatAmplitude = 2.0f;

        int frame = 0;
        this.setSprite(this.spritesset.get(frame, FRAMES));
        this.lastFrame = frame;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        int frame = (this.age / FRAME_TICKS) % FRAMES;
        if (frame != this.lastFrame) {
            this.setSprite(this.spritesset.get(frame, FRAMES));
            this.lastFrame = frame;
        }

        float offset = Mth.sin(this.age * this.floatSpeed) * this.floatAmplitude;

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.yd = 0.0;

        this.setPos(this.x, this.startY + offset, this.z);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new FlyingGhostParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }

    }

}