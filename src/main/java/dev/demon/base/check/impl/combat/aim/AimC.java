package dev.demon.base.check.impl.combat.aim;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

import static dev.demon.base.process.processors.MovementProcessor.gcd;

@Data(name = "Aim",
        subName = "C",
        checkType = CheckType.COMBAT,
        description = "Snap check v1")

public class AimC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                WrappedInFlyingPacket flyingPacket =
                        new WrappedInFlyingPacket(event.getPacketObject(), getUser().getPlayer());

                if (getUser().generalCancel()
                        || getUser().getProcessorManager().getActionProcessor()
                        .getServerTeleportTimer().hasNotPassed(3)) {
                    this.threshold = 0;
                    return;
                }

                double deltaYawAbs = getUser().getProcessorManager().getMovementProcessor().getDeltaYawAbs();
                double lastDeltaYawAbs = getUser().getProcessorManager().getMovementProcessor().getLastDeltaYawAbs();

                if (flyingPacket.getYaw() > -360
                        && flyingPacket.getYaw() < 360
                        && deltaYawAbs > 320
                        && lastDeltaYawAbs < 30) {

                    if (++this.threshold > 1) {
                        this.fail("Snapping head improperly");
                    }

                }

                this.threshold -= Math.min(this.threshold, 0.005);

                break;
            }
        }
    }
}
