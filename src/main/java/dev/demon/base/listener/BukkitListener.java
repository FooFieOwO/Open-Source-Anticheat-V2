package dev.demon.base.listener;


import dev.demon.Anticheat;
import dev.demon.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

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

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.process(event);
    }

    void processEvent(Event event) {
        if (event instanceof InventoryClickEvent
                || event instanceof PlayerChangedWorldEvent) {
            process(event);
        } else {
            Anticheat.getInstance().getExecutorService().execute(() -> this.process(event));
        }
    }

    void process(Event event) {
        if (event instanceof PlayerInteractEvent) {

            User user = Anticheat.getInstance().getUserManager().getPlayer(((PlayerInteractEvent) event).getPlayer());

            if (user == null) return;

            if (((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_AIR) {

                if (((PlayerInteractEvent) event).getItem() == null
                        || ((PlayerInteractEvent) event).getItem().getType() == null) return;

                if (((PlayerInteractEvent) event).getItem().getType() == Material.WATER_BUCKET) {

                    if (!user.getProcessorManager().getMovementProcessor().isPositionGround()
                            && user.getProcessorManager().getMovementProcessor().getFrom().getPosY()
                            >= user.getProcessorManager().getMovementProcessor().getTo().getPosY()
                            && user.getProcessorManager().getLagProcessor().getTransactionPing() > 4000L
                            && !user.getProcessorManager().getMovementProcessor().getTo().isOnGround()) {
                        user.getProcessorManager().getMovementProcessor().setTick(20);
                    }
                }
            }
        }
    }
}