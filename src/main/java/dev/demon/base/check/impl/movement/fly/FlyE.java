package dev.demon.base.check.impl.movement.fly;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.location.CustomLocation;
import org.bukkit.Bukkit;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;

@Data(name = "Fly",
        subName = "E",
        checkType = CheckType.MOVEMENT,
        description = "Air distance check xd")

public class FlyE extends Check {

    private CustomLocation location;

    private int threshold;

    //lmao

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {

            WrappedInFlyingPacket flyingPacket =
                    new WrappedInFlyingPacket(event.getPacketObject(), getUser().getPlayer());

            if (getUser().getProcessorManager().getCollisionProcessor().getLiquidTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSoulSandTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSlimeTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().isHalfBlock()
                    || getUser().getPlayer().getWorld() == null
                    || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0
                    || getUser().generalCancel()) {
                return;
            }

            if (getUser().getProcessorManager().getActionProcessor().getLastVelocityTimer().hasNotPassed(20)) {
                this.location = null;
                return;
            }

            if (getUser().getProcessorManager().getActionProcessor()
                    .getServerTeleportTimer().hasNotPassed(3)) {
                this.location = new CustomLocation(getUser().getPlayer().getWorld(),
                        flyingPacket.getX(), flyingPacket.getY(), flyingPacket.getZ());
                return;
            }

            boolean ground = getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround();
            boolean positionGround = getUser().getProcessorManager().getMovementProcessor().isPositionGround();
            boolean serverGround = getUser().getProcessorManager().getCollisionProcessor().isServerGround();
            boolean lastServerGround = getUser().getProcessorManager().getCollisionProcessor().isLastServerGround();

            CustomLocation currentLocation = getUser().getProcessorManager().getMovementProcessor().getTo();

            if (ground && serverGround) {
                this.location = new CustomLocation(getUser().getPlayer().getWorld(),
                        flyingPacket.getX(), flyingPacket.getY(), flyingPacket.getZ());
            }

            int airTick = getUser().getProcessorManager().getCollisionProcessor().getServerAirTicks();

            if (!serverGround && airTick > 8 && !positionGround && !ground && this.location != null) {

                double distance = currentLocation.distanceSquaredXZ(this.location);
                double distanceY = Math.abs(currentLocation.getPosY() - this.location.getPosY());

                double maxDistance = Double.MAX_VALUE;

                if (distanceY >= 25) {
                    maxDistance = 3000;
                }

                if (distanceY < 25 && distanceY >= 20) {
                    maxDistance = 102.0;
                }

                if (distanceY < 20 && distanceY >= 10) {
                    maxDistance = 80;
                }

                if (distanceY < 10 && distanceY >= 5) {
                    maxDistance = 47;
                }

                if (distanceY < 5 && distanceY >= 2) {
                    maxDistance = 32;
                }

                if (distanceY < 2) {
                    maxDistance = 30;
                }

                if (distance >= maxDistance) {
                    if (++this.threshold > 4) {
                        this.fail("Moving too far way from ground location.",
                                "distance="+distance,
                                "max="+maxDistance);
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, 0.03);
                }
            }
        }
    }
}
