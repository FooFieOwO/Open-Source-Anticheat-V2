package dev.demon.base.check.impl.misc.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "BadPackets",
        subName = "C",
        checkType = CheckType.MISC,
        description = "Out of order blocking check (autoblock)")

public class BadPacketsC extends Check {

    private int attack, dig;

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_DIG: {

                WrappedInBlockDigPacket packet =
                        new WrappedInBlockDigPacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getAction() != WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) return;
                this.dig++;
                break;
            }


            case CLIENT_USE_ENTITY: {

                WrappedInUseEntityPacket packet =
                        new WrappedInUseEntityPacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    this.attack++;
                }

                break;
            }

            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                boolean attack = this.attack == 1;
                boolean dig = this.dig == 1;


                if (attack && dig) {
                    if (++this.threshold > 5) {
                        this.fail("Dig packet sent while attacking");
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, .001);
                }

                this.attack = this.dig = 0;

                break;
            }
        }
    }
}