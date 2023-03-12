package dev.demon.base.check.impl.combat.aim;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "Aim",
        checkType = CheckType.COMBAT,
        description = "Checks for rounded rotations in the pitch.")

public class AimA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                if (getUser().generalCancel()
                        || getUser().getProcessorManager().getCombatProcessor().getLastAttackTimer().hasNotPassed(1)
                        || getUser().getProcessorManager().getActionProcessor()
                        .getServerTeleportTimer().hasNotPassed(3)) return;

                double deltaPitchAbs = getUser().getProcessorManager().getMovementProcessor().getDeltaPitchAbs();

                boolean invalidPitch = deltaPitchAbs % 1.5 != 0.0 && deltaPitchAbs == Math.round(deltaPitchAbs);

                if (invalidPitch) {
                    if (++this.threshold > 4.5) {
                        this.fail("Consistently rounded pitch",
                                "dpa=" + deltaPitchAbs);
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, .07);
                }
                break;
            }
        }
    }
}
