package dev.demon.base.check.impl.misc.badpackets;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

@Data(name = "BadPackets",
        subName = "F",
        checkType = CheckType.MISC,
        experimental = true,
        description = "Checks if the player cancels transactions for too long.")

public class BadPacketsF extends Check {

    private double threshold;

    private long lastPacket = -1337;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                if (getUser().getProcessorManager().getLagProcessor().getTransactionQueue().isEmpty()) return;

                this.getUser().getProcessorManager().getLagProcessor().getTransactionQueue().forEach((shortKey, longKey) -> {

                    long now = System.currentTimeMillis();

                    this.lastPacket = Math.abs(now - longKey);
                });

                //We should kick the player for this but ehh ill add kick method later.
                if (this.lastPacket != -1337 && this.lastPacket >= 15000L) {
                    if (++this.threshold > 2.5) {
                        this.fail("Too much time since the last transaction sent.");
                    }
                } else {
                    this.threshold -= Math.min(this.threshold, 0.35);
                }

                break;
            }
        }
    }
}