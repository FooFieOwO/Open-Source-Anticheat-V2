package dev.demon.base.check.impl.combat.aim;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "Aim",
        subName = "D",
        checkType = CheckType.COMBAT,
        description = "Snap check v2")

public class AimD extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                if (getUser().generalCancel()
                        || getUser().getProcessorManager()
                        .getActionProcessor().getServerTeleportTimer().hasNotPassed(3)) {
                    this.threshold = 0;
                    return;
                }

                if (getUser().getProcessorManager().getCombatProcessor().getLastAttackTimer().getDelta() < 3
                        || getUser().getProcessorManager().getActionProcessor()
                        .getServerTeleportTimer().hasNotPassed(3)) {

                    double deltaYaw = this.getUser().getProcessorManager().getMovementProcessor().getDeltaYawAbs();

                    double mouseX = this.getUser().getProcessorManager().getMovementProcessor().getYawGcdX();

                    double mouseYaw = Math.abs(deltaYaw - mouseX);
                    double snap = Math.abs(deltaYaw - mouseYaw);

                    if ((snap > 3000 || snap < 1.01 && snap > 0.98) && mouseYaw > 4000) {
                        if (++this.threshold > 2) {
                            this.fail("Head snapping in a fight");
                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, 0.03);
                    }
                }

                break;
            }
        }
    }
}
