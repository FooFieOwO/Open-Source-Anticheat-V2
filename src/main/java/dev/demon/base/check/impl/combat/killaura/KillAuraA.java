package dev.demon.base.check.impl.combat.killaura;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "KillAura",
        checkType = CheckType.COMBAT,
        description = "Checks if the player sends a rotation on post.")

public class KillAuraA extends Check {

    private double threshold;
    private long lastPositionLook;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_POSITION_LOOK: {

                this.lastPositionLook = System.currentTimeMillis();

                break;
            }

            case CLIENT_USE_ENTITY: {
                WrappedInUseEntityPacket packet =
                        new WrappedInUseEntityPacket(event.getPacketObject(), getUser().getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (getUser().generalCancel()
                            || getUser().getProcessorManager().getActionProcessor().getServerTeleportTimer().hasNotPassed(5)
                            || getUser().getProcessorManager().getLagProcessor().getPacketDrop() > 35L) {
                        this.threshold = 0;
                        return;
                    }

                    long now = System.currentTimeMillis();
                    long delta = Math.abs(now - this.lastPositionLook);

                    if (delta < 10L) {
                        if (++this.threshold > 9.5) {
                            this.fail("Player head rotation update was late.",
                                    "delta=" + delta);
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