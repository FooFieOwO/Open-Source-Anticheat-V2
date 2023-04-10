package dev.demon.base.check.impl.misc.scaffold;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import cc.funkemunky.api.utils.math.IntVector;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.Buffer;
import dev.demon.util.PacketUtil;
import dev.demon.util.location.CustomLocation;
import dev.demon.util.math.MathUtil;

@Data(name = "Scaffold",
        subName = "E",
        checkType = CheckType.MISC,
        description = "Invalid place distance",
        experimental = true)

public class ScaffoldE extends Check {

    private final Buffer buffer = new Buffer(10);

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_PLACE: {
                final WrappedInBlockPlacePacket packet = new WrappedInBlockPlacePacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getItemStack().getType().isBlock()) {
                    final IntVector oldBlockPos = packet.getBlockPosition();

                    //Invalid block position
                    if (oldBlockPos.getY() < 0)
                        return;

                    final CustomLocation to = getUser().getProcessorManager().getMovementProcessor().getTo();
                    final double distance = MathUtil.getPlaceDistance(to, oldBlockPos, packet.getFace());

                    if (distance < 0.76 && distance > 0 &&
                            getUser().getProcessorManager().getMovementProcessor().getDeltaXZ() > 0.18) {
                        if (this.buffer.add() > 5) {
                            this.fail("Invalid place distance", "Distance=" + distance);
                        }
                    } else {
                        this.buffer.reduce(0.1);
                    }
                }
                break;
            }

            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {
                this.buffer.reduce(0.025);
            }
        }
    }

}
