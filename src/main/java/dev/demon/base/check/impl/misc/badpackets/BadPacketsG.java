package dev.demon.base.check.impl.misc.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.Buffer;
import dev.demon.util.PacketUtil;

@Data(name = "BadPackets",
        subName = "G",
        checkType = CheckType.MISC,
        experimental = true,
        description = "Checks if the player sends weird flying packets.")

public class BadPacketsG extends Check {

    private final Buffer bufferPos = new Buffer(5);

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {
                final WrappedInFlyingPacket packet = new WrappedInFlyingPacket(event.getPacketObject(), getUser().getPlayer());

                if (getUser().getProcessorManager().getMovementProcessor().getTick() < 20)
                    return;

                if (packet.isPos() &&
                        getUser().getProcessorManager().getMovementProcessor().getDeltaXZ() == 0 &&
                        getUser().getProcessorManager().getMovementProcessor().getDeltaY() == 0) {
                    if (this.bufferPos.add() > 3) {
                        fail("pos");
                    }
                } else {
                    this.bufferPos.reduce();
                }
                break;
            }
        }
    }
}