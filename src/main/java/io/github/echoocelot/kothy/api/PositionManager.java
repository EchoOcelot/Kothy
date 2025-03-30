package io.github.echoocelot.kothy.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PositionManager {
    static Map<String, PositionManager> map = new HashMap<>();
    private static Location pos1;
    private static Location pos2;
    private static World world;
    private static boolean pos1Entered = false;
    private static boolean pos2Entered = false;

    public static PositionManager getPlayerInstance(Player p) {
        if(map.containsKey(p.getName())) {
            return map.get(p.getName());
        }
        else {
            PositionManager newPos = new PositionManager();
            map.put(p.getName(), newPos);
            return newPos;
        }
    }

    public void setPos1Entered(boolean state) {
        pos1Entered = state;
    }

    public void setPos2Entered(boolean state) {
        pos2Entered = state;
    }

    public boolean getPos1Entered() {
        return pos1Entered;
    }

    public boolean getPos2Entered() {
        return pos2Entered;
    }


    public Location getPos1() {
        return(pos1);
    }

    public void setPos1(Location position1) {
        pos1 = position1;
    }
    public Location getPos2() { return(pos2); }
    public void setPos2(Location position2) {
        pos2 = position2;
    }

    public World getWorld() { return(world); }
    public void setWorld(World w) {
        world = w;
    }
    public void clearPositions() {
        pos1 = null;
        pos2 = null;
        pos1Entered = false;
        pos2Entered = false;
    }
}
