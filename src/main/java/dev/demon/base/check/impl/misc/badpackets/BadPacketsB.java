package dev.demon.base.check.impl.misc.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "BadPackets",
        subName = "B",
        checkType = CheckType.MISC,
        description = "Checks if the player is attacking and blocking at the same time (autoblock)")

public class BadPacketsB extends Check {

    private boolean block, dig;

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_DIG: {

                WrappedInBlockDigPacket packet =
                        new WrappedInBlockDigPacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getAction() != WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) return;

                this.dig = true;
                break;
            }

            case CLIENT_BLOCK_PLACE: {
                this.block = true;
                break;
            }


            case CLIENT_USE_ENTITY: {

                WrappedInUseEntityPacket packet =
                        new WrappedInUseEntityPacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (this.block || this.dig) {

                        if (++this.threshold > 3) {

                            this.fail("Blocking or digging while attacking at the same time",
                                    "dig=" + this.dig,
                                    "block=" + this.block);

                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, .001);
                    }
                }

                break;
            }

            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                this.block = this.dig = false;


                break;
            }
        }
    }
}