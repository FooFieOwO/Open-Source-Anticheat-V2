package dev.demon.base.check.impl.combat.velocity;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;
import org.bukkit.util.Vector;

@Data(name = "Velocity",
        subName = "C",
        checkType = CheckType.COMBAT,
        description = "Checks for 99% vertical velocity second tick")

public class VelocityC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                Vector vector = getUser().getProcessorManager().getActionProcessor().getVelocityVector();

                if (vector != null) {

                    if (getUser().getProcessorManager().getActionProcessor().getLastConfirmedVelocityTick() == 2) {

                        boolean ground = getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround();
                        boolean lastGround = getUser().getProcessorManager().getMovementProcessor().getFrom().isOnGround();
                        boolean lastLastGround = getUser().getProcessorManager()
                                .getMovementProcessor().getFromFrom().isOnGround();

                        double deltaY = getUser().getProcessorManager().getMovementProcessor().getDeltaY();

                        double velocityY = vector.getY();

                        velocityY -= 0.08D;
                        velocityY *= 0.98F;

                        double total = Math.abs(deltaY / velocityY);

                        if (total <= 0.99 && lastLastGround && !lastGround && !ground) {
                            if (++this.threshold > 3) {
                                this.fail("Invalid vertical velocity", "t="+total);
                            }
                        } else {
                            this.threshold -= Math.min(this.threshold, .003);
                        }
                    }
                }

                break;
            }
        }
    }
}
