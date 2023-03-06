package dev.demon.util.track;

public interface ITracker {

    /*
     * This gets called when the start of the server tick is called
     */

    void onPreServerTick(long now);

    /*
     * This gets called when the end of the server tick is called, we can use this for reach, velocity & hitbox
     */

    void onPostServerTick(long now);
}
