package io.github.echoocelot.kothy.api;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ParticleManager {
    static Map<String, ParticleManager> map = new HashMap<>();

    public static ParticleManager getPlayerInstance(Player p) {
        if(map.containsKey(p.getName())) {
            return map.get(p.getName());
        }
        else {
            ParticleManager newPos = new ParticleManager();
            map.put(p.getName(), newPos);
            return newPos;
        }
    }
    public void drawParticleCubeForPlayer(Location pos1, Location pos2, Player p, Particle.DustOptions dustOptions)
    {
        int minX = Math.min((int) pos1.x(), (int) pos2.x());
        int maxX = Math.max((int) pos1.x(), (int) pos2.x()) + 1;

        int minY = Math.min((int) pos1.y(), (int) pos2.y());
        int maxY = Math.max((int) pos1.y(), (int) pos2.y()) + 1;

        int minZ = Math.min((int) pos1.z(), (int) pos2.z());
        int maxZ = Math.max((int) pos1.z(), (int) pos2.z()) + 1;

        for(int x = minX; x <= maxX; x++) {
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), x, minY, minZ), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), x, minY, maxZ), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), x, maxY, minZ), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), x, maxY, maxZ), 1, 0, 0, 0, 0, dustOptions);
        }

        for(int y = minY; y <= maxY; y++) {
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), minX, y, minZ), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), minX, y, maxZ), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), maxX, y, minZ), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), maxX, y, maxZ), 1, 0, 0, 0, 0, dustOptions);
        }

        for(int z = minZ; z <= maxZ; z++) {
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), minX, minY, z), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), minX, maxY, z), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), maxX, minY, z), 1, 0, 0, 0, 0, dustOptions);
            p.spawnParticle(Particle.DUST, new Location(p.getWorld(), maxX, maxY, z), 1, 0, 0, 0, 0, dustOptions);
        }
    }
}
