package dev.demon.base.check.impl.movement.fly;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.user.User;
import org.bukkit.Bukkit;

@Data(name = "Fly",
        checkType = CheckType.MOVEMENT,
        description = "Uses minecraft's code to determined if the player is following the proper gravity.")

public class FlyA extends Check {

    private double threshold;
    private double MAX_EXPECTED = 1E-12;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {

            WrappedInFlyingPacket packet = new WrappedInFlyingPacket(event.getPacketObject(), getUser().getPlayer());

            if (!packet.isPos()
                    || getUser().getProcessorManager().getCollisionProcessor().getLiquidTicks() > 0
                    || getUser().getProcessorManager().getActionProcessor().getLastVelocityTimer().hasNotPassed(9)
                    || getUser().getProcessorManager().getCollisionProcessor().getIceTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSoulSandTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSlimeTicks() > 0
                    || getUser().generalCancel()
                    || getUser().getProcessorManager().getActionProcessor()
                    .getServerTeleportTimer().hasNotPassed(3)) return;

            double deltaY = this.getUser().getProcessorManager().getMovementProcessor().getDeltaY();

            double lastDeltaY = this.getUser().getProcessorManager().getMovementProcessor().getLastDeltaY();

            double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;

            if (Math.abs(prediction) < 0.005D) {
                prediction = 0.0D;
            }

            double total = Math.abs(deltaY - prediction);

            if (!this.getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround()
                    && !this.getUser().getProcessorManager().getMovementProcessor().getFrom().isOnGround()) {

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
