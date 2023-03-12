package dev.demon.base.user;

import dev.demon.base.event.EventBus;
import dev.demon.base.process.ProcessorManager;
import dev.demon.util.box.BoundingBox;
import dev.demon.util.track.TrackerManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
public class User {

    private final Player player;
    private final UUID uuid;

    private boolean banned = false;

    private BoundingBox boundingBox = new BoundingBox(0,0,0,0,0,0);

    private EventBus eventBus;

    private ProcessorManager processorManager;

    private UserCheckManager checkManager;

    private final TrackerManager trackerManager = new TrackerManager();

    private boolean alerts = true;

    public User(Player player) {

        this.player = player;
        this.uuid = player.getUniqueId();

        //construct packets
        this.eventBus = new EventBus(this);

        //run processors
        this.processorManager = new ProcessorManager(this);

        //run checks after processors are complete
        this.checkManager = new UserCheckManager();

        this.checkManager.register(this);

        this.trackerManager.setup(this);

    }

    public boolean generalCancel() {
        return processorManager.getMovementProcessor().getTick() < 60
                || player.getAllowFlight()
                || !getProcessorManager().getCollisionProcessor().isChunkLoaded()
                || player.isFlying()
                || player.getGameMode() == GameMode.SPECTATOR
                || player.getGameMode() == GameMode.CREATIVE;
    }

    public boolean isSword(ItemStack itemStack) {
        return itemStack.getType() == Material.WOOD_SWORD
                || itemStack.getType() == Material.STONE_SWORD
                || itemStack.getType() == Material.GOLD_SWORD
                || itemStack.getType() == Material.IRON_SWORD
                || itemStack.getType() == Material.DIAMOND_SWORD;
    }
}
