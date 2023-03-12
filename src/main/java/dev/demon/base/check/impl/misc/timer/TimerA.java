package dev.demon.base.check.impl.misc.timer;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.PacketUtil;

import java.util.concurrent.TimeUnit;

@Data(name = "Timer",
        description = "Balance timer check, checks if the player speeds up their game.",
        checkType = CheckType.MISC)
public class TimerA extends Check {

    /**
     * Credits to Rhys for making this check.
     */

    private final long maxDelay = 50000000L;
    private final long maxValue = 45000000L;

    private double threshold;

    private long lastPacket = -1337L;
    private long balance;

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION_LOOK:
            case CLIENT_LOOK:
            case CLIENT_POSITION: {

                if (getUser().getProcessorManager().getPotionProcessor().getJumpAmp() > 90) {
                    return;
                }

                long now = System.nanoTime();
                long delta = (this.maxDelay - (now - this.lastPacket));

                if (!getUser().generalCancel()
                        && getUser().getProcessorManager().getMovementProcessor().getTick() > 20
                        && this.lastPacket > -1337L) {

                    this.balance += delta;

                    if (this.balance > this.maxValue) {

                        if (++this.threshold > 3) {
                            this.fail(
                                    "balance=" + this.balance,
                                    "packetDelta=" + delta
                            );
                        }
                        this.balance = 0;
                    }
                }

                this.lastPacket = now;
                break;
            }

            case SERVER_POSITION: {
                int pingTick = getUser().getProcessorManager().getLagProcessor().getPingTicks();

                //Prevent ping-spoof exploits
                if (pingTick > 20) pingTick = 10;

                //Add + 10 to be safe on teleport, possibly can change to a lower value other than 250L

                this.balance -= TimeUnit.MILLISECONDS.toNanos(
                        250L + (pingTick + 10)
                );

                break;
            }
        }
    }
}
