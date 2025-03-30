package io.github.echoocelot.kothy.listener;

import io.github.echoocelot.kothy.object.Hill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import io.github.echoocelot.kothy.api.GameManager;
import io.github.echoocelot.kothy.object.KothyScoreboard;
import io.github.echoocelot.kothy.api.WithinZonesManager;

/*
    Detects when a player enters an active hill
 */
public class ScoreboardZoneEntryListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();

        Location to = event.getTo();

        Hill hill = GameManager.getGameHill();

        if(GameManager.isGameOccurring()) {
            boolean withinZone = WithinZonesManager.iswithinScoreboardZone(hill, to);
            if (withinZone) KothyScoreboard.displayScoreboard(player);
            else KothyScoreboard.hideScoreboard(player);
        }
    }
}
