package io.github.echoocelot.kothy.api;

import io.github.echoocelot.kothy.object.Hill;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WithinZonesManager {

    public static boolean isWithinHill(Hill hill, Player p) {
        Location loc = p.getLocation();
        boolean withinHill = false;

        int smallerX = Math.min(hill.getPos1X(), hill.getPos2X());
        int largerX = Math.max(hill.getPos1X(), hill.getPos2X());

        int smallerY = Math.min(hill.getPos1Y(), hill.getPos2Y());
        int largerY = Math.max(hill.getPos1Y(), hill.getPos2Y());

        int smallerZ = Math.min(hill.getPos1Z(), hill.getPos2Z());
        int largerZ = Math.max(hill.getPos1Z(), hill.getPos2Z());

        if (loc.getBlockX() <= largerX && loc.getBlockX() >= smallerX) {
            if (loc.getBlockY() <= largerY && loc.getBlockY() >= smallerY) {
                if (loc.getBlockZ() <= largerZ && loc.getBlockZ() >= smallerZ) {
                    withinHill = true;
                }
            }
        }
        return withinHill;
    }

    public static boolean iswithinScoreboardZone(Hill hill, Location to) {
        int smallerX = Math.min(hill.getPos1X(), hill.getPos2X());
        int largerX = Math.max(hill.getPos1X(), hill.getPos2X());

        int smallerY = Math.min(hill.getPos1Y(), hill.getPos2Y());
        int largerY = Math.max(hill.getPos1Y(), hill.getPos2Y());

        int smallerZ = Math.min(hill.getPos1Z(), hill.getPos2Z());
        int largerZ = Math.max(hill.getPos1Z(), hill.getPos2Z());

        // Amount of blocks outside the hill that show the scoreboard
        int scoreRange = ConfigManager.getScoreboardDisplayRange();


        if (to.getBlockX() < largerX + scoreRange && to.getBlockX() > smallerX - scoreRange) {
            if (to.getBlockY() < largerY + scoreRange && to.getBlockY() > smallerY - scoreRange) {
                return to.getBlockZ() < largerZ + scoreRange && to.getBlockZ() > smallerZ - scoreRange;
            }
        }
        return false;
    }

    public static boolean iswithinKillZone(Hill hill, Location to) {
        int smallerX = Math.min(hill.getPos1X(), hill.getPos2X());
        int largerX = Math.max(hill.getPos1X(), hill.getPos2X());

        int smallerY = Math.min(hill.getPos1Y(), hill.getPos2Y());
        int largerY = Math.max(hill.getPos1Y(), hill.getPos2Y());

        int smallerZ = Math.min(hill.getPos1Z(), hill.getPos2Z());
        int largerZ = Math.max(hill.getPos1Z(), hill.getPos2Z());

        int largestHillDimension = hill.getLargestDimensionValue();

        if(largestHillDimension <= 3) {
            if (to.getBlockX() < largerX + 2 && to.getBlockX() > smallerX - 2) {
                if (to.getBlockY() < largerY + 2 && to.getBlockY() > smallerY - 2) {
                    return to.getBlockZ() < largerZ + 2 && to.getBlockZ() > smallerZ - 2;
                }
            }
        }
        else if(largestHillDimension == 4) {
            if (to.getBlockX() < largerX + 3 && to.getBlockX() > smallerX - 3) {
                if (to.getBlockY() < largerY + 3 && to.getBlockY() > smallerY - 3) {
                    return to.getBlockZ() < largerZ + 3 && to.getBlockZ() > smallerZ - 3;
                }
            }
        }
        else {
            if (to.getBlockX() < largerX + 5 && to.getBlockX() > smallerX - 5) {
                if (to.getBlockY() < largerY + 5 && to.getBlockY() > smallerY - 5) {
                    return to.getBlockZ() < largerZ + 5 && to.getBlockZ() > smallerZ - 5;
                }
            }
        }
        return false;
    }
}
