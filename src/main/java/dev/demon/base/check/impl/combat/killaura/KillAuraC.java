package dev.demon.base.check.impl.combat.killaura;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.user.User;
import dev.demon.util.PacketUtil;

@Data(name = "KillAura",
        subName = "C",
        checkType = CheckType.COMBAT,
        description = "Checks if the player is moving too quickly in a fight")

public class KillAuraC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION_LOOK:
            case CLIENT_LOOK:
            case CLIENT_POSITION: {

                if (getUser().generalCancel()
                        || getUser().getProcessorManager().getActionProcessor().getServerTeleportTimer().hasNotPassed(5)) {
                    return;
                }

                if (getUser().getProcessorManager().getCombatProcessor().getLastAttackTimer().hasNotPassedNoPing(1)) {

                    double deltaXZ = getUser().getProcessorManager().getMovementProcessor().getDeltaXZ();

                    double speed = getBaseSpeed(getUser());

                    double yaw = getUser().getProcessorManager().getMovementProcessor().getDeltaYawAbs();

                    if (deltaXZ > speed && yaw > 0.1) {
                        if (++this.threshold > 7.5) {
                            this.fail("Moving too quickly when attacking (keep sprint?)",
                                    "dxz="+deltaXZ,
                                    "max="+speed,
                                    "yaw="+yaw);
                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, .5);
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, .05);
                }

                break;
            }
        }
    }

    public static double getBaseSpeed(User player) {
        if (player.getProcessorManager().getPotionProcessor().isSpeed()) {
            return 0.25 + (player.getProcessorManager().getPotionProcessor().getSpeedAmp() * 0.045);
        } else return 0.25;
    }
}