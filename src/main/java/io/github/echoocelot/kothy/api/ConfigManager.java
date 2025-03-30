package io.github.echoocelot.kothy.api;

import io.github.echoocelot.kothy.Kothy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    @NotNull
    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    static File dataFolder = plugin.getDataFolder();

    static File file = new File(dataFolder, "config.yml");
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    static List<String> configKeys = Arrays.asList("gameNameLength", "maxHillSizeToShowParticles", "scoreboardDisplayRange",
            "scoreboardName", "wandItem");

    public static int getGameNameLength() {
        return config.getInt("gameNameLength", 30);
    }

    public static int getMaxHillSizeToShowParticles() {
        return config.getInt("maxHillSizeToShowParticles", 100);
    }

    public static int getScoreboardDisplayRange() {
        return config.getInt("scoreboardDisplayRange", 100);
    }

    public static String getScoreboardName() {
        return config.getString("scoreboardName", "King of the Hill");
    }

    public static String getWandItem() {
        return config.getString("wandItem", "ECHO_SHARD");
    }

    public static void loadDefaults() {
        config.set("gameNameLength", 30);
        config.set("maxHillSizeToShowParticles", 100);
        config.set("scoreboardDisplayRange", 100);
        config.set("scoreboardName", "King of the Hill");
        config.set("wandItem", "ECHO_SHARD");

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadNonPresentConfigOptions() {
        for(String configKey : configKeys) {
            if(!config.contains(configKey)) {
                switch(configKey) {
                    case "gameNameLength" -> config.set("gameNameLength", 30);
                    case "maxHillSizeToShowParticles" -> config.set("maxHillSizeToShowParticles", 100);
                    case "scoreboardDisplayRange" -> config.set("scoreboardDisplayRange", 100);
                    case "scoreboardName" -> config.set("scoreboardName", "King of the Hill");
                    case "wandItem" -> config.set("wandItem", "ECHO_SHARD");
                }
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
