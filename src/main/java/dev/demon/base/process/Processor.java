package dev.demon.base.process;

import dev.demon.Anticheat;
import dev.demon.base.event.Event;
import dev.demon.base.user.User;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class Processor extends Event {

    private final String name;

    private final User user;

    public Processor(final User user) {
        this.name = getClass().getAnnotation(ProcessorInfo.class).name();
        this.user = user;
    }

    public Material getMaterial(User user, double x, double y, double z) {
        return Anticheat.getInstance().getNmsManager().getInstance().getType(
                user.getPlayer().getWorld(), x, y, z);
    }

    public void fail(String info) {
        String alert = (Anticheat.getInstance().getConfigValues().getPrefix()
                + " " + info).replace("%PLAYER%", user.getPlayer().getName());

        Anticheat.getInstance().getUserManager().getUserMap().entrySet().stream().filter(uuidUserEntry ->
                        uuidUserEntry.getValue().getPlayer().isOp() && uuidUserEntry.getValue().isAlerts())
                .forEach(uuidUserEntry -> uuidUserEntry.getValue().getPlayer().sendMessage(alert));

        if (Anticheat.getInstance().getConfigValues().isConsoleAlerts()) {
            Anticheat.getInstance().getServer().getConsoleSender().sendMessage(alert);
        }
    }
}
