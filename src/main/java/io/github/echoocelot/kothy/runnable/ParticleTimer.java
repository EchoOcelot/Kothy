package io.github.echoocelot.kothy.runnable;

import io.github.echoocelot.kothy.Kothy;
import io.github.echoocelot.kothy.api.ParticleManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ParticleTimer {

    @NotNull
    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    private static final Map<Player, BukkitRunnable> selectionTasks = new HashMap<>();
    private static final Map<Player, BukkitRunnable> gameTasks = new HashMap<>();

    // Method to start particles for a specific player's selection
    public static void startParticleTaskForSelection(Location pos1, Location pos2, Player player) {
        BukkitRunnable task = new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    selectionTasks.remove(player);
                    return;
                }

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.LIME, 1.0f);
                ParticleManager pm = ParticleManager.getPlayerInstance(player);
                pm.drawParticleCubeForPlayer(pos1, pos2, player, dustOptions);
            }
        };

        task.runTaskTimer(plugin, 0L, 5L);  // Start immediately, repeat every 5 ticks
        selectionTasks.put(player, task);
    }

    // Method to stop particles for a specific player's selection
    public static void stopParticleTaskForSelection(Player player) {
        if (selectionTasks.containsKey(player)) {
            selectionTasks.get(player).cancel();
            selectionTasks.remove(player);
        }
    }

    // Method to stop particles for all player selections
    public static void stopAllParticleTasksForSelections() {
        for (BukkitRunnable task : selectionTasks.values()) {
            task.cancel();
        }
        selectionTasks.clear();
    }



    // Method to start particles for a game
    public static void startParticleTaskForGame(Location pos1, Location pos2, Player player) {
        BukkitRunnable task = new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    gameTasks.remove(player);
                    return;
                }

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.FUCHSIA, 1.0f);
                ParticleManager pm = ParticleManager.getPlayerInstance(player);
                pm.drawParticleCubeForPlayer(pos1, pos2, player, dustOptions);
            }
        };

        task.runTaskTimer(plugin, 0L, 5L);  // Start immediately, repeat every 5 ticks
        gameTasks.put(player, task);
    }

    // Method to stop particles for a game
    public static void stopParticleTaskForGame(Player player) {
        if (gameTasks.containsKey(player)) {
            gameTasks.get(player).cancel();
            gameTasks.remove(player);
        }
    }

    // Method to stop particles for all players in a game
    public static void stopAllParticleTasksForPlayers() {
        for (BukkitRunnable task : gameTasks.values()) {
            task.cancel();
        }
        gameTasks.clear();
    }

}
