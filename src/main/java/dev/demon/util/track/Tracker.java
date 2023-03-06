package dev.demon.util.track;

import dev.demon.base.user.User;
import lombok.Getter;

@Getter
public class Tracker implements ITracker {

    public User user;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onPreServerTick(long now) {
        //
    }

    @Override
    public void onPostServerTick(long now) {
        //
    }
}
