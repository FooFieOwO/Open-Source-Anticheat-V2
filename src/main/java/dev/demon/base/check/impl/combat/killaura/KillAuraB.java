package dev.demon.base.check.impl.combat.killaura;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;
import org.bukkit.Bukkit;

@Data(name = "KillAura",
        subName = "B",
        checkType = CheckType.COMBAT,
        description = "Checks if the player never stops sprinting in a fight")

public class KillAuraB extends Check {

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
                    double lastDeltaXZ = getUser().getProcessorManager().getMovementProcessor().getLastDeltaXZ();

                    double accel = Math.abs(deltaXZ - lastDeltaXZ);

                    double yaw = getUser().getProcessorManager().getMovementProcessor().getDeltaYawAbs();

                    if (accel < 0.001 && yaw > .2F) {
                        if (++this.threshold > 12) {
                            this.fail("Keep sprinting/Consistent movements",
                                    "accel="+accel,
                                    "dxz="+deltaXZ,
                                    "ldxz="+lastDeltaXZ);
                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, 0.5);
                    }

                }

                break;
            }
        }
    }
}