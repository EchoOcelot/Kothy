package io.github.echoocelot.kothy.object;

import org.bukkit.World;

import java.util.Date;

public class ScheduledGame {

    private String name;
    private long originalTimestamp;
    private long currentTimestamp;
    private String frequency;
    private String length;
    private Hill hill;

    public ScheduledGame(String name, long originalTimestamp, long currentTimestamp, String frequency, String length, Hill hill) {
        this.name = name;
        this.originalTimestamp = originalTimestamp;
        this.currentTimestamp = currentTimestamp;
        this.frequency = frequency;
        this.length = length;
        this.hill = hill;
    }

    // Getters
    public String getName() {
        return name;
    }

    public long getOriginalTimestamp() {
        return originalTimestamp;
    }

    public long getCurrentTimestamp() {
        return currentTimestamp;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getLength() {
        return length;
    }

    public Hill getHill() {
        return hill;
    }

    public String getGameDescription() {
        Date date = new Date(currentTimestamp * 1000);
        int centerX = (hill.getPos1X() + hill.getPos2X())/2;
        int centerY = (hill.getPos1Y() + hill.getPos2Y())/2;
        int centerZ = (hill.getPos1Z() + hill.getPos2Z())/2;
        World w = hill.getWorld();
        return String.format("%s - %s - %s - %s - (%d, %d, %d, %s)", name, date, length, frequency, centerX, centerY, centerZ, w.getName());
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setOriginalTimestamp(long originalTimestamp) {
        this.originalTimestamp = originalTimestamp;
    }

    public void setCurrentTimestamp(long currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setHill(Hill hill) {
        this.hill = hill;
    }
}
