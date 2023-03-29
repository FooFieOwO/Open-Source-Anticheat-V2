package dev.demon.base.check.impl.misc.scaffold;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;

@Data(name = "Scaffold",
        subName = "B",
        checkType = CheckType.MISC,
        experimental = true,
        description = "Invalid placement vector while not placing blocks but sending the placement packet")

public class ScaffoldB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_PLACE: {

                if (getUser().generalCancel()) return;

                WrappedInBlockPlacePacket packet =
                        new WrappedInBlockPlacePacket(event.getPacketObject(), getUser().getPlayer());

                boolean invalid = packet.getVecX() != 0.0
                        || packet.getVecY() != 0.0
                        || packet.getVecZ() != 0.0;

                if (packet.getItemStack().getType() == Material.AIR || packet.getItemStack() == null) return;

                if (packet.getItemStack().getType().isBlock() && invalid) {

                    if (getUser().getProcessorManager().getBlockProcessor()
                            .getLastConfirmedBlockPlaceTimer().hasNotPassed(3)) {
                        return;
                    }

                    if (++this.threshold > 1.0) {
                        this.fail("Impossible placement vector");
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, .1);
                }

                break;
            }
        }
    }
}
