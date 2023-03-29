package dev.demon.base.check.impl.misc.scaffold;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "Scaffold",
        subName = "D",
        checkType = CheckType.MISC,
        description = "Impossible vector set while placing blocks")

public class ScaffoldD extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_PLACE: {

                if (getUser().generalCancel()) return;

                WrappedInBlockPlacePacket packet =
                        new WrappedInBlockPlacePacket(event.getPacketObject(), getUser().getPlayer());

                boolean invalid = packet.getVecX() == 0.0
                        || packet.getVecY() == 0.0
                        || packet.getVecZ() == 0.0;

                //Ngl i didnt even really test this
                if (packet.getItemStack().getType().isBlock() && invalid) {

                    if (getUser().getProcessorManager().getBlockProcessor()
                            .getLastConfirmedBlockPlaceTimer().hasNotPassedNoPing(1)) {

                        if (++this.threshold > 5.0) {
                            this.fail("Impossible placement vector");
                        }
                    } else {
                        this.threshold = 0;
                    }
                }

                break;
            }
        }
    }
}
