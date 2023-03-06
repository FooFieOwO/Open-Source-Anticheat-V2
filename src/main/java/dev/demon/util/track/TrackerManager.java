package dev.demon.util.track;

import dev.demon.base.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TrackerManager {
    private final List<Tracker> trackers = new ArrayList<>();

    public void setup(User user) {
        this.trackers.add(new TransactionTracker());

        this.trackers.forEach(tracker -> tracker.setUser(user));
    }
}
