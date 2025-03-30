package io.github.echoocelot.kothy.handler;

import io.github.echoocelot.kothy.Kothy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ResultsDBHandler {

    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    static File dataFolder = plugin.getDataFolder();

    static File file = new File(dataFolder, "results.yml");
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static boolean addResult(String name, long currentTimestamp, String winner, int score) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Date date = new Date(currentTimestamp * 1000);
        config.set(currentTimestamp + ".name", name);
        config.set(currentTimestamp + ".date", date);
        config.set(currentTimestamp + ".winner", winner);
        config.set(currentTimestamp + ".score", score);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static List<String> getResults() {
        List<String> results = new ArrayList<>();

        // Get the keys at the first level (timestamps)
        for (String timestamp : Objects.requireNonNull(config.getConfigurationSection("")).getKeys(false)) {
            String name = config.getString(timestamp + ".name");
            String date = config.getString(timestamp + ".date");
            String winner = config.getString(timestamp + ".winner");
            int score = config.getInt(timestamp + ".score");

            // Format the result string
            String result = String.format("%s - %s - %s - (%d)", name, date, winner, score);
            results.add(result);
        }

        return results;
    }

    public static int getNumResults() {
        List<String> results = getResults();
        return results.size();
    }
}
