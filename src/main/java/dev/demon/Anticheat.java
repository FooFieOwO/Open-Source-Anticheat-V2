package dev.demon;

import dev.demon.base.check.api.CheckManager;
import dev.demon.base.listener.BukkitListener;
import dev.demon.base.user.User;
import dev.demon.base.user.UserManager;
import dev.demon.packet.PacketHandler;
import dev.demon.util.config.ConfigLoader;
import dev.demon.util.config.ConfigValues;
import dev.demon.util.nms.InstanceManager;
import dev.demon.util.track.TaskHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public class Anticheat extends JavaPlugin {

    @Getter
    @Setter
    public static Anticheat instance;

    public InstanceManager nmsManager = new InstanceManager();
    public UserManager userManager = new UserManager();

    public PacketHandler handler;
    private CheckManager checkManager;

    private final TaskHandler taskHandler = new TaskHandler();

    private final ConfigValues configValues = new ConfigValues();
    private final ConfigLoader loader = new ConfigLoader();

    @Override
    public void onEnable() {

        try {

            instance = this;

            new BukkitListener();

            this.nmsManager.create();

            this.handler = new PacketHandler();

            this.checkManager = new CheckManager();
            this.checkManager.loadChecks();

            this.taskHandler.start();

            this.loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        for (User player : this.userManager.getUserMap().values()) {
            this.userManager.removePlayer(player.getPlayer());
        }

        this.userManager = null;
        this.handler = null;
        this.nmsManager = null;

        instance = null;
    }
}
