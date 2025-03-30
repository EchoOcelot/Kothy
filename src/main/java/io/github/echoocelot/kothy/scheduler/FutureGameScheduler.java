package io.github.echoocelot.kothy.scheduler;

import io.github.echoocelot.kothy.Kothy;
import io.github.echoocelot.kothy.api.GameManager;
import io.github.echoocelot.kothy.api.KothyMessaging;
import io.github.echoocelot.kothy.handler.FutureGameDBHandler;
import io.github.echoocelot.kothy.object.Hill;
import io.github.echoocelot.kothy.object.ScheduledGame;
import io.github.echoocelot.kothy.runnable.GameTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;



public class FutureGameScheduler {

    @NotNull
    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    static File dataFolder = plugin.getDataFolder();

    static File file = new File(dataFolder, "scheduled_games.yml");
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    private static final Map<String, Timer> scheduledGames = new HashMap<>();

    public static void scheduleGame(ScheduledGame game) {
        Timer timer = new Timer();
        scheduledGames.put(game.getName(), timer); // Store the timer

        scheduleTask(timer, game);
    }

    private static void scheduleTask(Timer timer, ScheduledGame game) {
        String name = game.getName();
        long originalTimestamp = game.getOriginalTimestamp();
        long currentTimestamp = game.getCurrentTimestamp();
        String frequency = game.getFrequency();
        String gameLength = game.getLength();
        Hill hill = game.getHill();
        Date executionTime = new Date(currentTimestamp * 1000);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                config = YamlConfiguration.loadConfiguration(file);
                // Check if the game has been removed
                if (!config.contains(name)) {
                    scheduledGames.remove(name); // Clean up stored timer
                    return;
                }

                // Set game details
                GameManager.setGameName(name);
                GameManager.setGameLength(gameLength);
                GameManager.setGameHill(hill);

                Location center = hill.getHillCenter();
                for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                    KothyMessaging.sendMessage(p, "King of the Hill " + (name) + " starting at " + center.x()
                            + ", " + center.y() + ", " + center.z() + " in " + hill.getWorld().getName() + "!");
                }
                // Start game timer
                GameTimer gameTimer = new GameTimer();
                Thread t = new Thread(gameTimer);
                t.start();

                // Compute next occurrence
                if(!Objects.equals(frequency, "once")) {
                    long newTimestamp = FutureGameDBHandler.getNextGameTime(originalTimestamp, currentTimestamp, frequency);
                    config.set(name + ".currentTimestamp", newTimestamp);

                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    game.setCurrentTimestamp(newTimestamp);

                    // Reschedule the task with the new timestamp
                    scheduleTask(timer, game);
                }
            }
        };

        timer.schedule(task, executionTime);
    }

    public static boolean removeGame(String name) {
        if (scheduledGames.containsKey(name)) {
            scheduledGames.get(name).cancel(); // Cancel the timer
            scheduledGames.remove(name); // Remove from the map
            return FutureGameDBHandler.removeGame(name);
        }
        return false;
    }
}
