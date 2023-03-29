package dev.demon.base.check.impl.movement.fly;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import org.bukkit.Bukkit;

import java.util.Arrays;

@Data(name = "Fly",
        checkType = CheckType.MOVEMENT,
        description = "Uses minecraft's code to determined if the player is following the proper gravity.")

public class FlyA extends Check {

    private double threshold;
    private int airTick;

    private boolean blockCheck;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {

            if (getUser().getProcessorManager().getCollisionProcessor().getLiquidTicks() > 0
                    || getUser().getProcessorManager().getActionProcessor().getLastVelocityTimer().hasNotPassed(10)
                    || getUser().getProcessorManager().getCollisionProcessor().getSoulSandTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSlimeTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().isHalfBlock()
                    || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0
                    || getUser().generalCancel()) return;

            if (getUser().getProcessorManager().getCollisionProcessor().getBlockAboveTicks() > 0) {
                this.airTick = 6;
            } else {
                this.airTick = 0;
            }

            if (getUser().getProcessorManager().getActionProcessor()
                    .getServerTeleportTimer().hasNotPassed(3)) {
                this.airTick = 16;
                return;
            }

            int airTick = this.getUser().getProcessorManager().getMovementProcessor().getAirTicks();

            boolean ground = this.getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround();
            boolean lastGround = this.getUser().getProcessorManager().getMovementProcessor().getFrom().isOnGround();

            double deltaXZ = this.getUser().getProcessorManager().getMovementProcessor().getDeltaXZ();
            double deltaY = this.getUser().getProcessorManager().getMovementProcessor().getDeltaY();


            boolean blockPlace = this.getUser().getProcessorManager()
                    .getBlockProcessor().getLastConfirmedBlockPlaceTimer().hasNotPassed(9);

            if (blockPlace
                    && getUser().getProcessorManager()
                    .getCollisionProcessor().getBlockAboveTicks() > 0) {
                return;
            }

            if (blockPlace && deltaXZ < 0.12) {
                this.blockCheck = true;
            }

            if (deltaXZ > 0.12 && this.blockCheck || deltaXZ <= 0.1 && this.blockCheck && airTick > 2) {
                this.blockCheck = false;
            }

            double offset = Double.MAX_VALUE;
            boolean retard = false;

            for (boolean jump : Arrays.asList(true, false)) {
                double lastDeltaY = this.getUser().getProcessorManager().getMovementProcessor().getLastDeltaY();

                double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;

                if (Math.abs(prediction) < 0.005D) {
                    prediction = 0.0D;
                }

                if (!ground && lastGround) {
                    if (deltaY > 0.0) {
                        if (jump && deltaXZ < 0.12 && (blockPlace || this.blockCheck)) {
                            prediction = 0.40444491418477924F;
                            retard = true;
                        } else {
                            prediction = 0.42F;
                        }
                    }
                }

                //Fixes 0.404 retard issue.
                if (jump && deltaXZ < 0.12 && (blockPlace || this.blockCheck)) {
                    if (Math.abs(prediction - deltaY) > 1.0E-5) {

                        prediction = 0.42F;

                        double fixedMotion = (prediction - 0.08) * 0.98F;

                        if (Math.abs(fixedMotion) < 0.005) fixedMotion = 0;

                        if (Math.abs(fixedMotion - deltaY) < 1.0E-5) {
                            prediction = fixedMotion;
                            retard = true;
                        }
                    }
                }

                if (Math.abs(prediction - deltaY) > 1.0E-5) {
                    double fixedMotion = (prediction - 0.08) * 0.98F;

                    if (Math.abs(fixedMotion) < 0.005) fixedMotion = 0;

                    if (Math.abs(fixedMotion - deltaY) < 1.0E-5) {
                        prediction = fixedMotion;
                    }
                }

                double total = Math.abs(deltaY - prediction);

                if (total < offset) {
                    offset = total;
                }
            }

            if (offset == Double.MAX_VALUE) return;

            if (!ground && airTick > this.airTick) {

                double expected = retard ? 1.2e-8 : 1e-12;

                if (offset >= expected) {

                    if (++this.threshold > 2.5) {
                        this.fail("Player is not following proper gravity",
                                "total=" + offset,
                                "expected=" + expected);
                    }

                }
            } else {
                this.threshold -= Math.min(this.threshold, 0.0003);
            }
        }
    }
}
