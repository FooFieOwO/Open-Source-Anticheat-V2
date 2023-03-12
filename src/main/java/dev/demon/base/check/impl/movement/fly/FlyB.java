package dev.demon.base.check.impl.movement.fly;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;

@Data(name = "Fly",
        subName = "B",
        checkType = CheckType.MOVEMENT,
        description = "Checks if the player is spoofing ground when not on the ground.")

public class FlyB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {

            if (getUser().getProcessorManager().getCollisionProcessor().getLiquidTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getIceTicks() > 0
                    || getUser().getProcessorManager().getActionProcessor().getLastVelocityTimer().hasNotPassed(9)
                    || getUser().getProcessorManager().getCollisionProcessor().getSoulSandTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSlimeTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0
                    || getUser().generalCancel()
                    || getUser().getProcessorManager().getActionProcessor()
                    .getServerTeleportTimer().hasNotPassed(3)) return;

            boolean clientGround = getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround();
            boolean serverGround = getUser().getProcessorManager().getCollisionProcessor().isServerGround();
            boolean positionGround = getUser().getProcessorManager().getMovementProcessor().isPositionGround();

            if (clientGround && !serverGround && !positionGround) {
                if (++this.threshold > 2.5) {
                    this.fail("Spoofing ground state in the air");
                }
            } else {
                this.threshold -= Math.min(this.threshold, .0001);
            }
        }
    }
}
