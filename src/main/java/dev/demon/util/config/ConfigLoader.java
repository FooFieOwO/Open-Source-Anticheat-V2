package dev.demon.util.config;

import dev.demon.Anticheat;

public class ConfigLoader {

    public void load() {
        ConfigFile.getInstance().setup(Anticheat.getInstance());
        ConfigFile.getInstance().writeDefaults();

        Anticheat.getInstance().getConfigValues().setCommandName(ConfigFile.getInstance().getData()
                .getString("Command.Name"));

        Anticheat.getInstance().getConfigValues().setConsoleAlerts(ConfigFile.getInstance().getData()
                .getBoolean("Alert.Console-Alerts"));
        Anticheat.getInstance().getConfigValues().setPrefix(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Alert.Prefix")));
        Anticheat.getInstance().getConfigValues().setAlertsMessage(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Alert.Alert-Message")));
        Anticheat.getInstance().getConfigValues().setPunish(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.Command.Enabled"));
        Anticheat.getInstance().getConfigValues().setPunishCommand(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Punishment.Command.Execute")));
        Anticheat.getInstance().getConfigValues().setAnnounce(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.Announce.Enabled"));
        Anticheat.getInstance().getConfigValues().setAnnounceMessage(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Punishment.Announce.Message")));


        Anticheat.getInstance().getConfigValues().setAllowOp(ConfigFile.getInstance().getData()
                .getBoolean("Bypass.Op-Bypass"));


    }

    String convertColor(String in) {
        return in.replace("&", "§");
    }


}
