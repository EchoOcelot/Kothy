package io.github.echoocelot.kothy;

import io.github.echoocelot.kothy.api.ConfigManager;
import io.github.echoocelot.kothy.handler.FutureGameDBHandler;
import io.github.echoocelot.kothy.listener.HillDeathListener;
import io.github.echoocelot.kothy.listener.HillSelectionListener;
import io.github.echoocelot.kothy.listener.ScoreboardZoneEntryListener;
import io.github.echoocelot.kothy.object.ScheduledGame;
import io.github.echoocelot.kothy.scheduler.FutureGameScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.echoocelot.kothy.command.KothyCommand;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class Kothy extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners(
                new HillDeathListener(),
                new HillSelectionListener(),
                new ScoreboardZoneEntryListener()
        );

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();  // Create the folder if it doesn't exist
        }

        File configFile = new File(dataFolder, "config.yml");

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                ConfigManager.loadDefaults();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            ConfigManager.loadNonPresentConfigOptions();
        }

        List<ScheduledGame> gamesArr = FutureGameDBHandler.getAllGames();
        for(ScheduledGame game : gamesArr) {
            FutureGameScheduler.scheduleGame(game);
        }
    }

    private void registerCommands() {
        getCommand("kothy").setExecutor(new KothyCommand());
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pm = getServer().getPluginManager();

        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!"));
    }
}
