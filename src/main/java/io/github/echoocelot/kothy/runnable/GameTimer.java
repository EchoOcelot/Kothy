package io.github.echoocelot.kothy.runnable;

import io.github.echoocelot.kothy.Kothy;
import io.github.echoocelot.kothy.api.ConfigManager;
import io.github.echoocelot.kothy.api.GameManager;
import io.github.echoocelot.kothy.api.KothyMessaging;
import io.github.echoocelot.kothy.api.WithinZonesManager;
import io.github.echoocelot.kothy.handler.BlacklistDBHandler;
import io.github.echoocelot.kothy.handler.FutureGameDBHandler;
import io.github.echoocelot.kothy.handler.ResultsDBHandler;
import io.github.echoocelot.kothy.object.Hill;
import io.github.echoocelot.kothy.object.KothyScoreboard;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.*;

public class GameTimer implements Runnable {

    @NotNull Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    int timeRan = 0;
    int gameLengthSeconds;
    static BukkitTask t;
    static BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    static long currentTimestamp;
    static String gameName;

    @Override
    public void run() {
        Map<String, Boolean> areParticlesShowingMap = new HashMap<>();
        currentTimestamp = System.currentTimeMillis() / 1000;
        gameName = GameManager.getGameName();
        timeRan = 0;
        Hill hill = GameManager.getGameHill();
        Location pos1 = hill.getPos1();
        Location pos2 = hill.getPos2();

        String gameLength = GameManager.getGameLength();
        gameLengthSeconds = FutureGameDBHandler.getGameLengthInSeconds(gameLength);
        t = scheduler.runTaskTimer(plugin, () -> {
            KothyScoreboard.updateScoreboard(GameManager.getGameHill(), gameLengthSeconds - timeRan);
            for (Player p : Bukkit.getOnlinePlayers()) {

                if(WithinZonesManager.iswithinScoreboardZone(hill, p.getLocation())) {
                    if (hill.getLargestDimensionValue() < ConfigManager.getMaxHillSizeToShowParticles()
                            && !areParticlesShowingMap.containsKey(p.getName())) {
                        ParticleTimer.startParticleTaskForGame(pos1, pos2, p);
                        areParticlesShowingMap.put(p.getName(), true);
                    }
                }
                else if(areParticlesShowingMap.containsKey(p.getName())) ParticleTimer.stopParticleTaskForGame(p);

                boolean withinHill = WithinZonesManager.isWithinHill(hill, p);
                boolean blacklisted = BlacklistDBHandler.isPlayerBlacklisted(p.getName());
                GameMode gamemode = p.getGameMode();
                if(withinHill) {
                    if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) p.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
                if (withinHill && !p.isDead() && !blacklisted && gamemode != GameMode.SPECTATOR && gamemode != GameMode.CREATIVE)
                    KothyScoreboard.increaseScore(p, 1);
                else if (blacklisted && KothyScoreboard.getPlayerScore(p).getScore() > 0) KothyScoreboard.clearScore(p);
            }
            incrementAndCheckTime();
        }, 1L, 20L);
    }

    public void incrementAndCheckTime() {
        if (timeRan == gameLengthSeconds) {
            endGame();
        }
        timeRan++;
    }

    public static void endGame() {
        scheduler.cancelTask(t.getTaskId());

        List<String> highScores = KothyScoreboard.getWinner();
        if (highScores.size() == 1) {
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                KothyMessaging.sendMessage(p, highScores.get(0) + " won King of the Hill (" + gameName + ") with " +
                        KothyScoreboard.getHighScore() + " points!");
            }
            ResultsDBHandler.addResult(gameName, currentTimestamp, highScores.get(0), KothyScoreboard.getHighScore());
        } else if (highScores.size() > 1) {
            String winnersText = String.join(", ", highScores.subList(0, highScores.size() - 1))
                    + " and " + highScores.get(highScores.size() - 1);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                KothyMessaging.sendMessage(p, winnersText + " won King of the Hill (" + gameName + ") with " +
                        KothyScoreboard.getHighScore() + " points!");
            }
            ResultsDBHandler.addResult(gameName, currentTimestamp, winnersText, KothyScoreboard.getHighScore());
        }

        for(Player p : getOnlinePlayers())  {
            KothyScoreboard.clearScore(p);
            KothyScoreboard.hideScoreboard(p);
            ParticleTimer.stopParticleTaskForGame(p);
        }
        GameManager.setGameOccurring(false);
    }
}
