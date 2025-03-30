package io.github.echoocelot.kothy.object;

import io.github.echoocelot.kothy.api.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import io.github.echoocelot.kothy.api.WithinZonesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.format.TextColor.color;

public class KothyScoreboard {
    static ScoreboardManager manager = Bukkit.getScoreboardManager();
    static Scoreboard board = manager.getNewScoreboard();
    static Component component = Component.text(ConfigManager.getScoreboardName())
            .color(color(200, 0, 0));

    static Objective objective = board.registerNewObjective(ConfigManager.getScoreboardName(), Criteria.DUMMY, component);

    public static void updateScoreboard(Hill hill, int timeLeft) {
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String formattedTime = formatDisplayTime(timeLeft);
        Component updatedComponent = Component.text(ConfigManager.getScoreboardName() + " " + formattedTime)
                .color(color(200, 0, 0));
        objective.displayName(updatedComponent);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if(WithinZonesManager.iswithinScoreboardZone(hill, p.getLocation())) displayScoreboard(p);
        }
    }

    public static void displayScoreboard(Player p) {
        p.setScoreboard(board);
    }

    public static void hideScoreboard(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public static void increaseScore(Player p, int amount) {
        Score score = objective.getScore(p);
        int s = score.getScore();
        score.setScore(s + amount);
    }

    public static void decreaseScore(Player p, int amount) {
        Score score = objective.getScore(p);
        int s = score.getScore();
        score.setScore(s - amount);
    }

    public static void setScore(Player p, int amount) {
        Score score = objective.getScore(p);
        score.setScore(amount);
    }

    public static void clearScore(Player p) {
        board.resetScores(p.getName());
    }

    public static Score getPlayerScore(Player p) {
        return objective.getScore(p);
    }

    public static String formatDisplayTime(int time) {
        int hours, minutes, seconds;
        hours = time / 3600;
        minutes = (time % 3600) / 60;
        seconds = time % 60;

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    public static List<String> getWinner() {
        List<String> topNames = new ArrayList<String>();
        int top = Integer.MIN_VALUE;
        int pScore;
        for(String name : Objects.requireNonNull(objective.getScoreboard()).getEntries()) {
            pScore = objective.getScore(name).getScore();
            if(pScore > top) {
                topNames.clear();
                topNames.add(name);
                top = pScore;
            } else if(pScore == top) {
                topNames.add(name);
            }
        }
        return topNames;
    }

    public static int getHighScore() {
        int top = Integer.MIN_VALUE;
        int pScore;
        for(String name : Objects.requireNonNull(objective.getScoreboard()).getEntries()) {
            pScore = objective.getScore(name).getScore();
            if(pScore > top) {
                top = pScore;
            }
        }
        return top;
    }

}
