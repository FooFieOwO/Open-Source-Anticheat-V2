package dev.demon.base.check.impl.combat.aim;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;
import dev.demon.util.math.StreamUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Data(name = "Aim",
        subName = "D",
        checkType = CheckType.COMBAT,
        description = "Consistency in the pitch")

public class AimD extends Check {

    private double threshold;
    private final List<Double> pitchListArray = new ArrayList<>();

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

                if (getUser().getProcessorManager().getCombatProcessor().getLastAttackTimer().getDelta() < 3) {

                    double deltaPitch = getUser().getProcessorManager().getMovementProcessor().getDeltaPitchAbs();

                    this.pitchListArray.add(deltaPitch);

                    if (this.pitchListArray.size() >= 20) {

                        double std = StreamUtil.getStandardDeviation(this.pitchListArray);

                        if (std < .1) {
                            if (++this.threshold > 4.5) {
                                this.fail("std="+std);
                            }
                        } else {
                            this.threshold -= Math.min(this.threshold, .5);
                        }

                        this.pitchListArray.clear();
                    }
                }

                break;
            }
        }
    }
}
