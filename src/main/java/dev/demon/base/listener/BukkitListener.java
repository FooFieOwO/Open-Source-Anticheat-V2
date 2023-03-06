package dev.demon.base.listener;


import dev.demon.Anticheat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    public BukkitListener() {
        Bukkit.getPluginManager().registerEvents(this, Anticheat.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        Anticheat.getInstance().getUserManager().addPlayer(event.getPlayer());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(final PlayerQuitEvent event) {
        Anticheat.getInstance().getUserManager().removePlayer(event.getPlayer());
    }
}