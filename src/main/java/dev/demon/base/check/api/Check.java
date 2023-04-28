package dev.demon.base.check.api;

import dev.demon.Anticheat;
import dev.demon.base.event.Event;
import dev.demon.base.user.User;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public abstract class Check extends Event implements Cloneable {

    @Setter
    private User user;
    private Data data;
    private double violations;
    private double punishmentVL;
    private String checkName, checkType;
    private CheckType checkCategory;
    private boolean enabled;
    private boolean punishable, experimental;
    private long lastVerbose;

    public Check() {
        if (getClass().isAnnotationPresent(Data.class)) {
            this.data = getClass().getAnnotation(Data.class);

            this.punishmentVL = this.data.punishmentVL();
            this.checkName = this.data.name();
            this.checkType = this.data.subName();
            this.enabled = this.data.enabled();
            this.punishable = this.data.punishable();
            this.experimental = this.data.experimental();
            this.checkCategory = this.data.checkType();
        }
    }

    public Check clone() {
        try {
            return (Check) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void fail(final String... data) {
        if (this.user == null) return;

        if (getUser().getPlayer().isOp() && Anticheat.getInstance().getConfigValues().isAllowOp()
                || getUser().isBanned()) {
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();

        for (final String s : data) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }

            stringBuilder.append(s);
        }

        String checkType = this.checkType;

        if (this.experimental) {
            checkType += "*";
        }

        final String alert = Anticheat.getInstance().getConfigValues().getAlertsMessage().replace("%VL%",
                        Double.toString(violations)).replace("%PLAYER%", getUser().getPlayer().getName())
                .replace("%CHECK%", checkName).replace("%CHECKTYPE%", checkType).
                replace("%MAX-VL%", Double.toString(punishmentVL))
                .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix());

        final TextComponent textComponent = new TextComponent(alert);

        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(stringBuilder.toString().trim()).create()));

        Anticheat.getInstance().getUserManager().getUserMap().entrySet().stream().filter(uuidUserEntry ->
                        uuidUserEntry.getValue().getPlayer().isOp() && uuidUserEntry.getValue().isAlerts())
                .forEach(uuidUserEntry -> uuidUserEntry.getValue().getPlayer().spigot().sendMessage(textComponent));


        if (Anticheat.getInstance().getConfigValues().isConsoleAlerts()) {
            Anticheat.getInstance().getServer().getConsoleSender().sendMessage(alert);
        }

        if (this.violations >= this.punishmentVL && !getUser().isBanned()
                && this.punishable
                && Anticheat.getInstance().getConfigValues().isPunish()) {
            punishPlayer(getUser());
        }

        if (this.punishable) {
            this.violations += 1.0;
        }
    }

    public static void punishPlayer(User user) {
        if (user.isBanned()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Anticheat.getInstance().getConfigValues()
                        .getPunishCommand()
                        .replace("%PLAYER%", user.getPlayer().getName())
                        .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix())
                        .replaceFirst("/", ""));

                if (Anticheat.getInstance().getConfigValues().isAnnounce()) {
                    Bukkit.broadcastMessage(Anticheat.getInstance().getConfigValues().getAnnounceMessage()
                            .replace("%PLAYER%", user.getPlayer().getName())
                            .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix()));
                }

                user.setBanned(true);
            }
        }.runTask(Anticheat.getInstance());
    }
}
