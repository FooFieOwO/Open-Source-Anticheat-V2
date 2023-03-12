package dev.demon.base.check.impl.misc.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;
import org.bukkit.entity.Player;

@Data(name = "BadPackets",
        subName = "D",
        checkType = CheckType.MISC,
        description = "Checks if the player blocks but never unblocks afterwards.")

public class BadPacketsD extends Check {

    private double threshold;
    private boolean blocking;

    private int slotChanged;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_DIG: {

                WrappedInBlockDigPacket packet =
                        new WrappedInBlockDigPacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getAction() != WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) return;

                if (packet.getDirection().b() == -1) {
                    this.blocking = false;
                }

                break;
            }

            case CLIENT_BLOCK_PLACE: {

                WrappedInBlockPlacePacket packet =
                        new WrappedInBlockPlacePacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getFace().b() == 3 && getUser().isSword(packet.getItemStack())) {
                    this.blocking = true;
                }

                break;
            }


            case CLIENT_USE_ENTITY: {

                WrappedInUseEntityPacket packet =
                        new WrappedInUseEntityPacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (packet.getEntity() instanceof Player) {

                        if (this.blocking) {

                            if (++this.threshold > 10) {
                                this.fail("Blocking while attacking (no release packet?)",
                                        "threshold=" + this.threshold);
                            }

                        } else {
                            this.threshold -= Math.min(this.threshold, 2);
                        }
                    }
                }

                break;
            }

            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                this.slotChanged -= Math.min(this.slotChanged, 1);

                if (this.slotChanged > 0 && this.blocking) {
                    this.blocking = false;
                }

                break;
            }

            case CLIENT_HELD_ITEM_SLOT: {

                this.slotChanged = 20;

                break;
            }
        }
    }
}