package dev.demon.base.check.impl.misc.scaffold;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "Scaffold",
        checkType = CheckType.MISC,
        experimental = true,
        description = "Basic snap head to block check")

public class ScaffoldA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                if (getUser().generalCancel()
                        || getUser().getProcessorManager().getMovementProcessor().getDeltaXZ() < .12
                        || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0
                        || getUser().getProcessorManager().getBlockProcessor().getLastConfirmedBlockPlaceTimer().passed(1)
                        || getUser().getProcessorManager().getActionProcessor()
                        .getServerTeleportTimer().hasNotPassed(3)) return;


                double deltaYaw = this.getUser().getProcessorManager().getMovementProcessor().getDeltaYawAbs();

                // generate the angel of the yaw and mouse x
                double angle = Math.abs(deltaYaw /
                        Math.abs(this.getUser().getProcessorManager().getMovementProcessor().getYawGcdX()));

                double round = Math.round(angle);

                if (angle > 50 && deltaYaw > 10 || round == angle) {
                    if (++this.threshold > 5) {
                        this.fail("Snapping head towards the block while scaffolding");
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, .3);
                }

                break;
            }
        }
    }
}
