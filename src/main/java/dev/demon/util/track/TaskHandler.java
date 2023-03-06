package dev.demon.util.track;

import dev.demon.Anticheat;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class TaskHandler extends BukkitRunnable {

    private int serverTick;
    private short preTick;
    private short postTick;

    @Override
    public void run() {

        ++this.serverTick;

        this.preTick = (short) (-(this.serverTick * 2) - 1 % Short.MIN_VALUE);
        this.postTick = (short) (-(this.serverTick * 2) % Short.MIN_VALUE);

        if (this.preTick > 0 || this.postTick > 0) {
            this.serverTick = 0;
        }

        long now = System.currentTimeMillis();

        // loops through all users, then through each tracker that has been set up
        Anticheat.getInstance().getUserManager().getUserMap().forEach((key, value) ->
                value.getTrackerManager().getTrackers().forEach(tracker -> tracker.onPreServerTick(now)));
    }

    public void start() {
        // hook pre server tick
        this.runTaskTimer(Anticheat.getInstance(), 0L, 0L);

        // hook post server tick
        Anticheat.getInstance().getNmsManager().getInstance().createPostHook(() -> {
            long now = System.currentTimeMillis();

            Anticheat.getInstance().getUserManager().getUserMap().forEach((key, value) ->
                    value.getTrackerManager().getTrackers().forEach(tracker -> tracker.onPostServerTick(now)));
        });
    }

    public short nextPreTick() {
        return this.preTick;
    }

    public short nextPostTick() {
        return this.postTick;
    }
}
