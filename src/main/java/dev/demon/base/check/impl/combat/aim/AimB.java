package dev.demon.base.check.impl.combat.aim;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

import static dev.demon.base.process.processors.MovementProcessor.gcd;

@Data(name = "Aim",
        subName = "B",
        checkType = CheckType.COMBAT,
        description = "AntiSkid made it first! (GCD)")

public class AimB extends Check {

    private double threshold;
    private final double gcdOffset = Math.pow(2.0, 24.0);

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                if (getUser().generalCancel()) {
                    return;
                }

                if (getUser().getProcessorManager().getCombatProcessor().getLastAttackTimer().getDelta() < 3
                        || getUser().getProcessorManager().getActionProcessor()
                        .getServerTeleportTimer().hasNotPassed(3)) {

                    double deltaYaw = getUser().getProcessorManager().getMovementProcessor().getDeltaYawAbs(),
                            deltaPitch = getUser().getProcessorManager().getMovementProcessor().getDeltaPitchAbs();

                    double pitchGCD = gcd(
                            (long) (getUser().getProcessorManager().getMovementProcessor().getDeltaPitch()
                                    * this.gcdOffset),
                            (long) (Math.abs(getUser().getProcessorManager().getMovementProcessor().getLastDeltaPitch())
                                    * this.gcdOffset)
                    );

                    if (deltaPitch > 0.0 && deltaYaw > 0.0) {
                        if (pitchGCD <= 131072L) {
                            if (++this.threshold > 7) {
                                this.fail("Improper GCD",
                                        "gcd=" + pitchGCD);
                            }
                        } else {
                            this.threshold -= Math.min(this.threshold, .5);
                        }
                    }
                }
                break;
            }
        }
    }
}
