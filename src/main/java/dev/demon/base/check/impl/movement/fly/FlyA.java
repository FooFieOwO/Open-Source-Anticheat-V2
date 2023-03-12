package dev.demon.base.check.impl.movement.fly;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;

@Data(name = "Fly",
        checkType = CheckType.MOVEMENT,
        description = "Uses minecraft's code to determined if the player is following the proper gravity.")

public class FlyA extends Check {

    private double threshold;
    private double MAX_EXPECTED = 1E-12;
    private int airTick;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {

            if (getUser().getProcessorManager().getCollisionProcessor().getLiquidTicks() > 0
                    || getUser().getProcessorManager().getActionProcessor().getLastVelocityTimer().hasNotPassed(9)
                    || getUser().getProcessorManager().getCollisionProcessor().getSoulSandTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSlimeTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0
                    || getUser().generalCancel()
                    || getUser().getProcessorManager().getActionProcessor()
                    .getServerTeleportTimer().hasNotPassed(3)) return;

            if (getUser().getProcessorManager().getCollisionProcessor().getBlockAboveTicks() > 0) {
                this.airTick = 6;
            } else {
                this.airTick = 0;
            }

            int airTick = getUser().getProcessorManager().getMovementProcessor().getAirTicks();

            boolean ground = this.getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround();
            boolean lastServerGround = this.getUser().getProcessorManager().getCollisionProcessor().isLastServerGround();
            boolean serverGround = this.getUser().getProcessorManager().getCollisionProcessor().isServerGround();

            double deltaY = this.getUser().getProcessorManager().getMovementProcessor().getDeltaY();

            double lastDeltaY = this.getUser().getProcessorManager().getMovementProcessor().getLastDeltaY();

            double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;

            if (Math.abs(prediction) < 0.005D) {
                prediction = 0.0D;
            }

            if (Math.abs(prediction - deltaY) > 1.0E-5) {
                double fixedMotion = (prediction - 0.08) * 0.98F;

                if (Math.abs(fixedMotion) < 0.005) fixedMotion = 0;

                if (Math.abs(fixedMotion - deltaY) < 1.0E-5) {
                    prediction = fixedMotion;
                }
            }

            double total = Math.abs(deltaY - prediction);

            if (!ground && !serverGround && !lastServerGround && airTick > this.airTick) {

                if (total > this.MAX_EXPECTED) {

                    if (++this.threshold > 3.5) {
                        this.fail("Player is not following proper gravity",
                                "total=" + total,
                                "expected=" + this.MAX_EXPECTED);
                    }

                }
            } else {
                this.threshold -= Math.min(this.threshold, 0.0003);
            }
        }
    }
}
