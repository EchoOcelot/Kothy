package io.github.echoocelot.kothy.handler;

import io.github.echoocelot.kothy.Kothy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlacklistDBHandler {

    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    static File dataFolder = plugin.getDataFolder();

    static File file = new File(dataFolder, "blacklist.yml");
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static boolean blacklistPlayer(String name) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Player p = Bukkit.getPlayer(name);
        if(p == null) return false;
        String uuid = String.valueOf(p.getUniqueId());
        if (config.contains(name)) return false;
        config.set(name, uuid);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean unblacklistPlayer(String name) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (config.contains(name)) {
            config.set(name, null);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else return false;
    }

    public static boolean isPlayerBlacklisted(String name) {
        return config.contains(name);
    }

    public static List<String> getBlacklist() {
        return new ArrayList<>(config.getKeys(false));
    }

}
