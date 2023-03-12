package dev.demon.base.check.impl.movement.speed;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.MathHelper;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.location.CustomLocation;

import java.util.Arrays;
import java.util.List;

@Data(name = "Speed",
        subName = "B",
        checkType = CheckType.MOVEMENT,
        description = "Checks if the player is following the proper friction and acceleration kinda")

public class SpeedB extends Check {

    private double threshold;

    private final boolean[] trueFalse = new boolean[]{true, false};
    private final List<Integer> strafeForward = Arrays.asList(1, -1, 0);

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {

            WrappedInFlyingPacket packet = new WrappedInFlyingPacket(event.getPacketObject(), getUser().getPlayer());

            if (!packet.isPos()
                    || getUser().getProcessorManager().getCollisionProcessor().getLiquidTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getIceTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().isHalfBlock()
                    || getUser().getProcessorManager().getCollisionProcessor().getSoulSandTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSlimeTicks() > 0
                    || getUser().generalCancel()
                    || getUser().getProcessorManager().getActionProcessor().getLastVelocityTimer().hasNotPassed(9)
                    || getUser().getProcessorManager().getActionProcessor()
                    .getServerTeleportTimer().hasNotPassed(3)) return;

            if (getUser().getProcessorManager().getCollisionProcessor().isCollideHorizontal()) {
                this.threshold -= Math.min(this.threshold, 5);
                return;
            }

            CustomLocation from = getUser().getProcessorManager().getMovementProcessor().getFrom();

            boolean lastGround = from.isOnGround();

            double deltaXZ = getUser().getProcessorManager().getMovementProcessor().getDeltaXZ();

            double deltaX = getUser().getProcessorManager().getMovementProcessor().getDeltaX();
            double deltaZ = getUser().getProcessorManager().getMovementProcessor().getDeltaZ();


            float attributeSpeed;

            attributeSpeed = this.getUser().getProcessorManager().getAbilitiesProcessor().getWalkSpeed();


            if (this.getUser().getProcessorManager().getPotionProcessor().isSpeed()) {
                attributeSpeed += this.getUser().getProcessorManager().getPotionProcessor()
                        .getSpeedAmp() * 0.2D * attributeSpeed;
            }

            if (this.getUser().getProcessorManager().getPotionProcessor().isSlow()) {
                attributeSpeed += this.getUser().getProcessorManager().getPotionProcessor()
                        .getSlowAmp() * -.15D * attributeSpeed;
            }

            double lowestOffset = Double.MAX_VALUE;

            i:
            {
                for (boolean sprint : this.trueFalse) {
                    for (boolean attacking : this.trueFalse) {
                        for (boolean jump : this.trueFalse) {
                            for (boolean g : this.trueFalse) {
                                for (boolean sneak : this.trueFalse) {
                                    for (boolean useItem : this.trueFalse) {
                                        for (int strafe : this.strafeForward) {
                                            for (int forward : this.strafeForward) {

                                                float fm = forward;
                                                float sm = strafe;

                                                fm *= 0.98F;
                                                sm *= 0.98F;

                                                if (useItem) {
                                                    fm *= 0.2D;
                                                    sm *= 0.2D;
                                                }

                                                if (sneak) {
                                                    fm *= (float) 0.3D;
                                                    sm *= (float) 0.3D;
                                                }

                                                double lastDeltaX = getUser().getProcessorManager().getMovementProcessor().getLastDeltaX();
                                                double lastDeltaZ = getUser().getProcessorManager().getMovementProcessor().getLastDeltaZ();

                                                if (g) {
                                                    lastDeltaX *= (0.91F * 0.6F);
                                                    lastDeltaZ *= (0.91F * 0.6F);
                                                } else {
                                                    lastDeltaX *= 0.91F;
                                                    lastDeltaZ *= 0.91F;
                                                }

                                                if (attacking) {
                                                    lastDeltaX *= 0.6D;
                                                    lastDeltaZ *= 0.6D;
                                                }

                                                if (Math.abs(lastDeltaX) < 0.005D) {
                                                    lastDeltaX = 0.0;
                                                }

                                                if (Math.abs(lastDeltaZ) < 0.005D) {
                                                    lastDeltaZ = 0.0;
                                                }

                                                if (jump && sprint) {
                                                    float radians = getUser().getProcessorManager()
                                                            .getMovementProcessor().getTo().getYaw()
                                                            * 0.017453292F;


                                                    lastDeltaX -= (Math.sin(radians) * 0.2F);
                                                    lastDeltaZ += (Math.cos(radians) * 0.2F);
                                                }


                                                float slipperiness = 0.91F;

                                                if (lastGround) {
                                                    slipperiness = 0.6F * 0.91F;
                                                }

                                                float moveSpeed = (float) attributeSpeed;

                                                if (sprint) moveSpeed += moveSpeed * 0.30000001192092896D;

                                                float moveFriction;

                                                if (lastGround) {
                                                    float moveSpeedMultiplier = 0.16277136F /
                                                            (slipperiness * slipperiness * slipperiness);

                                                    moveFriction = moveSpeed * moveSpeedMultiplier;
                                                } else {
                                                    moveFriction = (float)
                                                            (sprint ? ((double) 0.02F + (double) 0.02F * 0.3D) : 0.02F);
                                                }


                                                float diagonal = sm * sm + fm * fm;

                                                float flyingFactorX = 0.0F;
                                                float flyingFactorZ = 0.0F;

                                                if (diagonal >= 1.0E-4F) {
                                                    diagonal = MathHelper.sqrt_float(diagonal);

                                                    if (diagonal < 1.0F) {
                                                        diagonal = 1.0F;
                                                    }

                                                    diagonal = moveFriction / diagonal;

                                                    float strafeS = sm * diagonal;
                                                    float forwardS = fm * diagonal;

                                                    float sin = (float) Math.sin(getUser().getProcessorManager().getMovementProcessor().getTo().getYaw()
                                                            * (float) Math.PI / 180.0F);

                                                    float cos = (float) Math.cos(getUser().getProcessorManager().getMovementProcessor().getTo().getYaw()
                                                            * (float) Math.PI / 180.0F);

                                                    float factorX = strafeS * cos - forwardS * sin;
                                                    float factorZ = forwardS * cos + strafeS * sin;

                                                    flyingFactorX = factorX;
                                                    flyingFactorZ = factorZ;
                                                }

                                                lastDeltaX += flyingFactorX;
                                                lastDeltaZ += flyingFactorZ;

                                                double distance = Math.pow(deltaX - lastDeltaX, 2) +
                                                        Math.pow(deltaZ - lastDeltaZ, 2);

                                                if (distance < lowestOffset) {
                                                    lowestOffset = distance;

                                                    if (lowestOffset < 1e-20) {
                                                        break i;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (deltaXZ > 0.2 && lowestOffset >= 1e-6) {
                if (++this.threshold > 5) {
                    this.fail("Invalid movement from the player",
                            "offset=" + lowestOffset,
                            "deltaXZ=" + deltaXZ);
                }
            } else {
                this.threshold -= Math.min(this.threshold, .00003);
            }
        }
    }
}
