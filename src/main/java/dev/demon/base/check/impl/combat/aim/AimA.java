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

                if (getUser().generalCancel()) return;

                if (getUser().getProcessorManager().getCombatProcessor().getLastAttackTimer().getDelta() < 3
                        || getUser().getProcessorManager().getActionProcessor()
                        .getServerTeleportTimer().hasNotPassed(3)) {

                    //Make this into one check just for keksi! <3
                    double deltaPitchAbs = getUser().getProcessorManager().getMovementProcessor().getDeltaPitchAbs();
                    double deltaYawAbs = getUser().getProcessorManager().getMovementProcessor().getDeltaYawAbs();

                    boolean invalidYaw = deltaYawAbs % 1.5 != 0.0
                            && deltaYawAbs == Math.round(deltaYawAbs)
                            && deltaYawAbs > 0.0;

                    boolean invalidPitch = deltaPitchAbs % 1.5 != 0.0
                            && deltaPitchAbs == Math.round(deltaPitchAbs)
                            && deltaPitchAbs > 0.0;

                    if (invalidPitch || invalidYaw) {
                        if (++this.threshold > 7.5) {
                            this.fail("Consistently rounded rotation",
                                    "deltaPitch=" + deltaPitchAbs,
                                    "deltaYaw=" + deltaYawAbs);
                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, .07);
                    }
                }
                break;
            }
        }
    }
}
