package dev.demon.base.check.impl.combat.hitbox;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;
import org.bukkit.entity.Player;

@Data(name = "HitBox",
        checkType = CheckType.COMBAT,
        description = "Checks if the player sends an interaction outside the hitbox.")

public class HitBoxA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        if (PacketUtil.toPacket(event) == PacketUtil.Packets.CLIENT_USE_ENTITY) {
            WrappedInUseEntityPacket packet =
                    new WrappedInUseEntityPacket(event.getPacketObject(), getUser().getPlayer());

            if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                if (packet.getEntity() instanceof Player) {

                    boolean present = packet.getVec() != null;

                    if (!present) return;

                    double x = packet.getVec().a;
                    double y = packet.getVec().b;
                    double z = packet.getVec().c;

                    if (x > .4044 || y > 2.0 || z > .4044) {
                        if (++this.threshold > 1.5) {
                            this.fail("Interaction vector is set to impossible numbers",
                                    "x=" + x,
                                    "y=" + y,
                                    "z=" + z);
                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, 0.003);
                    }
                }
            }
        }
    }
}