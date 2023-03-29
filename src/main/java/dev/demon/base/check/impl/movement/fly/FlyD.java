package dev.demon.base.check.impl.movement.fly;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;

@Data(name = "Fly",
        subName = "D",
        checkType = CheckType.MOVEMENT,
        experimental = true,
        description = "YPort/rapid motion y movements check (omg phoenix haven check!!!!)")

public class FlyD extends Check {

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


            double deltaY = getUser().getProcessorManager().getMovementProcessor().getDeltaY();
            double lastDeltaY = getUser().getProcessorManager().getMovementProcessor().getLastDeltaY();

            if (deltaY > 0.0 && lastDeltaY < 0.0) {
                if (++this.threshold > 6.5) {
                    this.fail("Improper movements when going up and down.");
                }
            } else {
                this.threshold -= Math.min(this.threshold, .25);
            }
        }
    }
}
