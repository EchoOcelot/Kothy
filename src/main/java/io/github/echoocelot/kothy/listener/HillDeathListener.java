package io.github.echoocelot.kothy.listener;

import io.github.echoocelot.kothy.api.GameManager;
import io.github.echoocelot.kothy.object.Hill;
import io.github.echoocelot.kothy.object.KothyScoreboard;
import io.github.echoocelot.kothy.api.WithinZonesManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Score;

public class HillDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if(GameManager.isGameOccurring()) {
            if (player.getKiller() != null) {
                Hill gameHill = GameManager.getGameHill();
                boolean withinHill = WithinZonesManager.isWithinHill(gameHill, player);
                boolean withinKillZone = WithinZonesManager.iswithinKillZone(gameHill, player.getLocation());

                if (withinHill) {
                    Score killedPlayerScore = KothyScoreboard.getPlayerScore(player);
                    int s = killedPlayerScore.getScore();

                    int pointSteal;
                    if (s % 2 == 0) pointSteal = s / 2;
                    else pointSteal = (s / 2) + 1;

                    KothyScoreboard.decreaseScore(player, pointSteal);
                    KothyScoreboard.increaseScore(player.getKiller(), pointSteal);
                }
                else if(withinKillZone) {
                    Score killedPlayerScore = KothyScoreboard.getPlayerScore(player);
                    int s = killedPlayerScore.getScore();

                    int pointSteal;
                    if (s % 2 == 0) pointSteal = s / 2;
                    else pointSteal = (s / 2) + 1;

                    KothyScoreboard.decreaseScore(player, pointSteal/2);
                    KothyScoreboard.increaseScore(player.getKiller(), pointSteal);
                }
            }
        }
    }
}
