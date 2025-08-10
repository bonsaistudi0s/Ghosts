package dev.xylonity.bonsai.ghosts.common.entity.ai.control;

import dev.xylonity.bonsai.ghosts.common.entity.MainGhostEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class GhostMoveControl extends MoveControl {
    private final MainGhostEntity ghost;

    public GhostMoveControl(MainGhostEntity ghost) {
        super(ghost);
        this.ghost = ghost;
    }

    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            Vec3 v = new Vec3(this.wantedX - ghost.getX(), this.wantedY - ghost.getY(), this.wantedZ - ghost.getZ());
            double dist = v.length();
            if (dist < ghost.getBoundingBox().getSize()) {
                this.operation = Operation.WAIT;
                ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.5));
            } else {
                ghost.setDeltaMovement(ghost.getDeltaMovement().add(v.scale(this.speedModifier * 0.05 / dist)));
                if (ghost.getTarget() == null) {
                    Vec3 to = ghost.getDeltaMovement();
                    ghost.setYRot(-((float) Mth.atan2(to.x, to.z)) * 57.295776F);
                } else {
                    double x = ghost.getTarget().getX() - ghost.getX();
                    double z = ghost.getTarget().getZ() - ghost.getZ();
                    ghost.setYRot(-((float)Mth.atan2(x, z)) * 57.295776F);
                }
                ghost.yBodyRot = ghost.getYRot();
            }

        }
    }

}