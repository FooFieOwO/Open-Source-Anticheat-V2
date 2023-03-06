package dev.demon.util.time;

import dev.demon.base.user.User;
import lombok.Getter;

@Getter
public class EventTimer {
    private int tick;
    private final int max;
    private final User user;

    public EventTimer(int max, User user) {
        this.tick = 0;
        this.max = max;
        this.user = user;
    }

    public int getDelta() {
        return (this.user.getProcessorManager().getMovementProcessor().getTick() - tick);
    }

    public boolean hasNotPassed(int ctick) {
        int maxTick = this.user.getProcessorManager().getLagProcessor().getPingTicks() + ctick;
        int connectedTick = this.user.getProcessorManager().getMovementProcessor().getTick();

        return ((connectedTick - tick) <= maxTick);
    }

    public boolean hasNotPassedNoPing(int cTick) {
        return ((this.user.getProcessorManager().getMovementProcessor().getTick() - tick) <= cTick);
    }

    public boolean hasNotPassed() {
        return ((this.user.getProcessorManager().getMovementProcessor().getTick() - tick) <=
                (this.user.getProcessorManager().getLagProcessor().getPingTicks() + this.max));
    }

    public boolean hasNotPassedNoPing() {
        return ((this.user.getProcessorManager().getMovementProcessor().getTick() - tick) <= this.max);
    }

    public boolean passed() {
        return ((this.user.getProcessorManager().getMovementProcessor().getTick() - tick) >=
                (this.max + this.user.getProcessorManager().getLagProcessor().getPingTicks()));
    }

    public boolean passed(int cTick) {
        return ((this.user.getProcessorManager().getMovementProcessor().getTick() - tick) >=
                (cTick + this.user.getProcessorManager().getLagProcessor().getPingTicks()));
    }

    public boolean passedNoPing() {
        return ((this.user.getProcessorManager().getMovementProcessor().getTick() - tick) >= this.max);
    }

    public boolean passedNoPing(int cTick) {
        return ((this.user.getProcessorManager().getMovementProcessor().getTick() - tick) >= cTick);
    }

    public void reset() {
        this.tick = this.user.getProcessorManager().getMovementProcessor().getTick();
    }

    public void fullReset() {
        this.tick = -1;
    }
}