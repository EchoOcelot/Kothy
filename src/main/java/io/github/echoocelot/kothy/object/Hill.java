package io.github.echoocelot.kothy.object;

import org.bukkit.Location;
import org.bukkit.World;

public class Hill {

    private Location pos1;
    private Location pos2;
    private World world;

    public Hill(Location pos1, Location pos2, World w) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        world = w;
    }

    public void setPos1X(int x) {
        pos1.setX(x);
    }

    public void setPos1Y(int y) {
        pos1.setY(y);
    }

    public void setPos1Z(int z) {
        pos1.setZ(z);
    }

    public void setPos2X(int x) {
        pos2.setX(x);
    }

    public void setPos2Y(int y) {
        pos2.setY(y);
    }

    public void setPos2Z(int z) {
        pos2.setZ(z);
    }

    public int getPos1X() {
        return (int) pos1.getX();
    }

    public int getPos1Y() {
        return (int) pos1.getY();
    }

    public int getPos1Z() {
        return (int) pos1.getZ();
    }

    public int getPos2X() {
        return (int) pos2.getX();
    }

    public int getPos2Y() {
        return (int) pos2.getY();
    }

    public int getPos2Z() {
        return (int) pos2.getZ();
    }

    public Location getPos1() { return pos1; }

    public Location getPos2() { return pos2; }

    public void setWorld(World w) {
        world = w;
    }

    public World getWorld() {
        return world;
    }

    public int getLargestDimensionValue() {
        int minX = Math.min((int) pos1.x(), (int) pos2.x());
        int maxX = Math.max((int) pos1.x(), (int) pos2.x());

        int minY = Math.min((int) pos1.y(), (int) pos2.y());
        int maxY = Math.max((int) pos1.y(), (int) pos2.y());

        int minZ = Math.min((int) pos1.z(), (int) pos2.z());
        int maxZ = Math.max((int) pos1.z(), (int) pos2.z());

        int largest = 0;
        if ( (maxX - minX) > largest) largest = (maxX - minX);
        else if ( (maxY - minY) > largest) largest = (maxY - minY);
        else if ( (maxZ - minZ) > largest) largest = (maxZ - minZ);
        return largest;
    }

    public Location getHillCenter() {
        int minX = Math.min((int) pos1.x(), (int) pos2.x());
        int maxX = Math.max((int) pos1.x(), (int) pos2.x());
        int centerX = (maxX + minX)/2;

        int minY = Math.min((int) pos1.y(), (int) pos2.y());
        int maxY = Math.max((int) pos1.y(), (int) pos2.y());
        int centerY = (maxY + minY)/2;

        int minZ = Math.min((int) pos1.z(), (int) pos2.z());
        int maxZ = Math.max((int) pos1.z(), (int) pos2.z());
        int centerZ = (maxZ + minZ)/2;

        return new Location(world, centerX, centerY, centerZ);
    }

}
