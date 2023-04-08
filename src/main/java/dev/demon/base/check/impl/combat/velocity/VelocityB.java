package dev.demon.base.check.impl.combat.velocity;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;
import org.bukkit.util.Vector;

@Data(name = "Velocity",
        subName = "B",
        checkType = CheckType.COMBAT,
        description = "Checks for 99% vertical velocity")

public class VelocityB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                Vector vector = getUser().getProcessorManager().getActionProcessor().getVelocityVector();

                //TODO: make this not actual dog shit.
                if (vector != null) {

                    if (getUser().getProcessorManager().getActionProcessor().getLastConfirmedVelocityTick() == 1) {

                        double deltaY = getUser().getProcessorManager().getMovementProcessor().getDeltaY();
                        double velocityY = vector.getY();

                        double total = deltaY - velocityY;

                        if (total > 0.078375 || total < -0.00001) {

                            if (++this.threshold > 4.5) {
                                this.fail("No vertical velocity", "DeltaY=" + deltaY, "VelocityY=" + velocityY);
                            }

                        } else {
                            this.threshold -= Math.min(this.threshold, 1);
                        }
                    }
                }

                break;
            }
        }
    }
}
