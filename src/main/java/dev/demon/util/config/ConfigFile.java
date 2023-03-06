package dev.demon.util.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private ConfigFile() {}

    static ConfigFile instance = new ConfigFile();

    public static ConfigFile getInstance() {
        return instance;
    }

    private FileConfiguration config;
    private FileConfiguration data;
    private File dfile;

    public void setup(Plugin p) {
        config = p.getConfig();
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }
        dfile = new File("plugins/Anticheat/config.yml");

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        data = YamlConfiguration.loadConfiguration(dfile);

    }

    public FileConfiguration getData() {
        return data;
    }


    public void writeDefaults() {
        data.options().header("%PLAYER% = the player cheating. %PREFIX% = the prefix of the anticheat you set below."
                + "\nCommand Name, set what you what the command for the anticheat to be, default: 'anticheat'"
                + "\nOp-Bypass: if set to true, a player that is op will automatically bypass" +
                " else if false they will need the permission \"anticheat.bypass\" to bypass"

            //    + "\nLagBacks are recommended to be OFF! to prevent bypasses"
          //      + "\nReplay Discord sends notifications if a player has been recorded to discord."
            //    + "\nReplayVL, is when the replay will begin to record when a check reaches that set VL"
           //     + "\nReplayTime is the set time of how long a recording is"
           //     + "\nAntiCancelSprint attempts to lag players back who try to cancel sprint packets in order to bypass (experimental)"
                + "\n");


        if (!data.contains("Command.Name")) data.set("Command.Name", "anticheat");

        if (!data.contains("Alert.Console-Alerts")) data.set("Alert.Console-Alerts", true);
        if (!data.contains("Alert.Prefix")) data.set("Alert.Prefix", "&c[ACv2]&r");
        if (!data.contains("Alert.Alert-Message")) data.set("Alert.Alert-Message",
                "%PREFIX% &f%PLAYER% &7flagged &f%CHECK% &8(&fType %CHECKTYPE%&8) &8(&c%VL%/%MAX-VL%&8)");
       /* if (!data.contains("Alert.Discord")) data.set("Alert.Discord", false);
        if (!data.contains("Alert.Discord-Bans")) data.set("Alert.Discord-Bans", true);
        if (!data.contains("Alert.Discord-Bans-Only")) data.set("Alert.Discord-Only", false);
        if (!data.contains("Alert.Discord-WebhookURL")) data.set("Alert.Discord-WebhookURL",
                "https://discord.com/api/webhooks/");

        if (!data.contains("Alert.Discord-Alert-Message")) data.set("Alert.Discord-Alert-Message",
                "[Anticheat] %PLAYER% flagged %CHECK% (%CHECKTYPE%) [%VL%/%MAX-VL%]");
        if (!data.contains("Alert.Discord-Ban-Message")) data.set("Alert.Discord-Ban-Message",
                "[Anticheat] has removed %PLAYER% for using Unfair Advantages. (%CHECK% %CHECKTYPE%)]");*/

        if (!data.contains("Punishment.Command.Enabled")) data.set("Punishment.Command.Enabled", true);
        if (!data.contains("Punishment.Command.Execute")) data.set("Punishment.Command.Execute",
                "/ban %PLAYER% %PREFIX% &cUnfair Advantage.");

     //   if (!data.contains("Punishment.BanWave.Enabled")) data.set("Punishment.BanWave.Enabled", false);
     //  if (!data.contains("Punishment.BanWave.Time")) data.set("Punishment.BanWave.Time", 5);
       // if (!data.contains("Punishment.BanWave.Timely")) data.set("Punishment.BanWave.Timely", false);
     //   if (!data.contains("Punishment.BanWave.CheckUpTime")) data.set("Punishment.BanWave.CheckUpTime", 120);

        if (!data.contains("Punishment.Announce.Enabled")) data.set("Punishment.Announce.Enabled", true);
        if (!data.contains("Punishment.Announce.Message")) data.set("Punishment.Announce.Message",
                "%PREFIX% &7has removed &f%PLAYER% &7for using &cUnfair Advantages.");

        if (!data.contains("Bypass.Op-Bypass")) data.set("Bypass.Op-Bypass", false);

      //  if (!data.contains("BungeeCord.Enabled")) data.set("BungeeCord.Enabled", false);

     /*   if (!data.contains("AdvancedReplay.Enabled")) data.set("AdvancedReplay.Enabled", false);
        if (!data.contains("AdvancedReplay.Discord")) data.set("AdvancedReplay.Discord", false);
        if (!data.contains("AdvancedReplay.ReplayVL")) data.set("AdvancedReplay.ReplayVL", 15);
        if (!data.contains("AdvancedReplay.ReplayTime")) data.set("AdvancedReplay.ReplayTime", 30);

        if (!data.contains("Process.AntiCancelSprint")) data.set("Process.AntiCancelSprint", true);

        if (!data.contains("Checks.ReachB")) data.set("Checks.ReachB", 3.03);*/

        saveData();
    }

    public void saveData() {
        try {
            data.save(dfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        this.data = YamlConfiguration.loadConfiguration(dfile);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
