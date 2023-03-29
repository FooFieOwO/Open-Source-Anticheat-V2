package dev.demon.base.check.impl.misc.scaffold;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "Scaffold",
        subName = "C",
        checkType = CheckType.MISC,
        description = "Impossible vector limit")

public class ScaffoldC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_PLACE: {

                if (getUser().generalCancel()) return;

                WrappedInBlockPlacePacket packet =
                        new WrappedInBlockPlacePacket(event.getPacketObject(), getUser().getPlayer());

                //impossible for the vector to go over 1.0 or negative
                boolean invalid = packet.getVecX() > 1.0
                        || packet.getVecY() > 1.0
                        || packet.getVecZ() > 1.0
                        || packet.getVecX() < 0.0
                        || packet.getVecY() < 0.0
                        || packet.getVecZ() < 0.0;

                if (packet.getItemStack().getType().isBlock() && invalid) {

                    if (++this.threshold > 3.0) {
                        this.fail("Impossible placement vector");
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, .01);
                }

                break;
            }
        }
    }
}
