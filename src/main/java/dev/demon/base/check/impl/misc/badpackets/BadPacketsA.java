package dev.demon.base.check.impl.misc.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "BadPackets",
        checkType = CheckType.MISC,
        description = "Checks if the players pitch is past the game limit")

public class BadPacketsA extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                if (getUser().generalCancel()
                        || getUser().getProcessorManager().getActionProcessor().getServerTeleportTimer().hasNotPassed(3)
                        || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0) {
                    return;
                }

                double pitch = Math.abs(getUser().getProcessorManager().getMovementProcessor().getTo().getPitch());

                if (pitch > 90.0F) {
                    this.fail("Impossible pitch limit",
                            "pitch=" + pitch);
                }

                break;
            }
        }
    }
}