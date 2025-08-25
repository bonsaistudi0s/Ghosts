package dev.xylonity.bonsai.ghosts.common.entity.ai.generic;

import dev.xylonity.bonsai.ghosts.common.entity.MainGhostEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class GhostApproachHeldGlowBerriesGoal extends Goal {

    private final MainGhostEntity ghost;
    private final double maxSpeed;
    private final double minDistance;
    private final double startDistance;
    private final float lerpFactor;
    private final int searchRadius;

    private Player target;
    private boolean forcedNoPhysics = false;

    public GhostApproachHeldGlowBerriesGoal(MainGhostEntity ghost, double maxSpeed, double minDistance, double startDistance, float lerpFactor, int searchRadius) {
        this.ghost = ghost;
        this.maxSpeed = maxSpeed;
        this.minDistance = minDistance;
        this.startDistance = startDistance;
        this.lerpFactor = lerpFactor;
        this.searchRadius = searchRadius;

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (ghost.isTame()) return false;
        if (ghost.isVehicle() || ghost.isInSittingPose()) return false;

        List<Player> players = ghost.level().getEntitiesOfClass(Player.class, ghost.getBoundingBox().inflate(searchRadius), p -> p.isAlive() && !p.isSpectator() && (p.getMainHandItem().is(Items.GLOW_BERRIES) || p.getOffhandItem().is(Items.GLOW_BERRIES)));
        if (players.isEmpty()) return false;

        this.target = players.stream().min(Comparator.comparingDouble(p -> p.distanceToSqr(ghost))).orElse(null);

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (ghost.isTame()) return false;
        if (ghost.isVehicle() || ghost.isInSittingPose()) return false;
        if (!target.isAlive()) return false;
        if (target.distanceToSqr(ghost) > (double) (searchRadius * searchRadius)) return false;

        return target.getMainHandItem().is(Items.GLOW_BERRIES) || target.getOffhandItem().is(Items.GLOW_BERRIES);
    }

    @Override
    public void start() {
        this.forcedNoPhysics = false;
    }

    @Override
    public void stop() {
        if (this.forcedNoPhysics) {
            ghost.noPhysics = false;
            this.forcedNoPhysics = false;
        }

        this.target = null;

        ghost.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) return;

        Vec3 hover = target.getEyePosition().add(0, 0.6, 0);

        double dx = hover.x - ghost.getX();
        double dy = hover.y - ghost.getY();
        double dz = hover.z - ghost.getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 1.0E-6) return;

        ghost.lookAt(target, 30f, 30f);

        Vec3 k = hover.subtract(dx / distance * minDistance, dy / distance * minDistance, dz / distance * minDistance);
        Vec3 veloc = k.subtract(ghost.position());

        double len = veloc.length();
        if (len > 1.0E-6) veloc = veloc.scale(1.0 / len).scale(maxSpeed * (distance >= startDistance ? 1.0 : Mth.clamp((distance - minDistance) / Math.max(0.1, (startDistance - minDistance)), 0.0, 1.0)));

        Vec3 current = ghost.getDeltaMovement();
        ghost.setDeltaMovement(new Vec3(Mth.lerp(lerpFactor, current.x, veloc.x), Mth.lerp(lerpFactor, current.y, veloc.y), Mth.lerp(lerpFactor, current.z, veloc.z)));

        if (distance <= minDistance + 0.25) {
            ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.85));
        }

        boolean blocked = canGoThrough(ghost.level(), ghost.getEyePosition(), k);

        ghost.noPhysics = blocked;
        this.forcedNoPhysics = blocked;
    }

    private boolean canGoThrough(Level lvl, Vec3 from, Vec3 to) {
        return lvl.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, ghost)).getType() != HitResult.Type.MISS;
    }

}
