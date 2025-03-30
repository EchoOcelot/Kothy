package io.github.echoocelot.kothy.handler;

import io.github.echoocelot.kothy.Kothy;
import io.github.echoocelot.kothy.object.Hill;
import io.github.echoocelot.kothy.object.ScheduledGame;
import io.github.echoocelot.kothy.scheduler.FutureGameScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FutureGameDBHandler {

    @NotNull
    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    static File dataFolder = plugin.getDataFolder();

    static File file = new File(dataFolder, "scheduled_games.yml");
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static boolean addGame(ScheduledGame game) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String name = game.getName();
        long timestamp = game.getCurrentTimestamp();
        String frequency = game.getFrequency();
        String gameLength = game.getLength();
        Hill hill = game.getHill();
        if (config.contains(name)) return false;
        config.set(name + ".originalTimestamp", timestamp);
        config.set(name + ".currentTimestamp", timestamp);
        config.set(name + ".frequency", frequency);
        config.set(name + ".length", gameLength);
        config.set(name + ".world", hill.getWorld().getName());
        config.set(name + ".pos1", hill.getPos1X() + "," + hill.getPos1Y() + "," + hill.getPos1Z());
        config.set(name + ".pos2", hill.getPos2X() + "," + hill.getPos2Y() + "," + hill.getPos2Z());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FutureGameScheduler.scheduleGame(game);
        return true;
    }

    public static boolean removeGame(String name) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (config.contains(name)) {
            config.set(name, null);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        } else return false;
    }

    public static boolean doesGameExist(String name) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return config.contains(name);
    }

    // Ensures that a newly scheduled game does not coincide with existing games
    public static boolean doesGameCoincide(long newTimestampStart, String newFrequency, String newGameLength) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        int newGameLengthSeconds = getGameLengthInSeconds(newGameLength);

        List<ScheduledGame> gamesArr = FutureGameDBHandler.getAllGames();

        // Convert new game's start and end times to LocalTime
        LocalDateTime newStart = Instant.ofEpochSecond(newTimestampStart).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime newEnd = newStart.plusSeconds(newGameLengthSeconds);

        for (ScheduledGame game : gamesArr) {
            long oldTimestampStart = game.getOriginalTimestamp();
            String oldFrequency = game.getFrequency();
            String oldGameLength = game.getLength();

            int oldGameLengthSeconds = getGameLengthInSeconds(oldGameLength);

            // Convert old game's start and end times to LocalTime
            LocalDateTime oldStart = Instant.ofEpochSecond(oldTimestampStart).atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime oldEnd = oldStart.plusSeconds(oldGameLengthSeconds);

            LocalTime oldStartTime = oldStart.toLocalTime();
            LocalTime oldEndTime = oldEnd.toLocalTime();
            LocalTime newStartTime = newStart.toLocalTime();
            LocalTime newEndTime = newEnd.toLocalTime();

            // Step 1: Check if original times of day overlap
            if (oldStartTime.isBefore(newEndTime) && newStartTime.isBefore(oldEndTime)) {
                // Step 2: If either game is daily, they will always conflict
                if (newFrequency.equals("daily") || oldFrequency.equals("daily")) {
                    return true;
                }

                String oldStartWeekday = String.valueOf(oldStart.getDayOfWeek());
                String oldEndWeekday = String.valueOf(oldEnd.getDayOfWeek());
                String newStartWeekday = String.valueOf(newStart.getDayOfWeek());
                String newEndWeekday = String.valueOf(newEnd.getDayOfWeek());

                boolean weekDaySame = (Objects.equals(oldStartWeekday, newStartWeekday) || Objects.equals(oldStartWeekday, newEndWeekday)) ||
                        (Objects.equals(oldEndWeekday, newStartWeekday) || Objects.equals(oldEndWeekday, newEndWeekday));

                // Step 3: If both are weekly or one is weekly & the other biweekly, they will conflict if day of week is the same
                if ((newFrequency.equals("weekly") && oldFrequency.equals("weekly")) ||
                        (newFrequency.equals("weekly") && oldFrequency.equals("biweekly")) ||
                        (newFrequency.equals("biweekly") && oldFrequency.equals("weekly"))) {
                    if(weekDaySame) {
                        return true;
                    }
                }

                // Step 4: If both are biweekly, check if they occur on separate weeks
                if (newFrequency.equals("biweekly") && oldFrequency.equals("biweekly")) {
                    double weeksBetween = Math.abs((newTimestampStart - oldTimestampStart) / (7.0 * 24 * 3600));
                    if (Math.round(weeksBetween) % 2 == 0) {
                        if(weekDaySame) {
                            return true;
                        }
                    }
                }

                // Step 5: If one is monthly and the other is weekly/biweekly/yearly, check for future instances
                if ((newFrequency.equals("monthly") || oldFrequency.equals("monthly")) ||
                        (newFrequency.equals("yearly") && (oldFrequency.equals("weekly") || oldFrequency.equals("biweekly")))) {

                    long nextNewGameStart = newTimestampStart;
                    long maxTimestamp = newTimestampStart + 157788000; // Check 5 years in the future
                    while (nextNewGameStart <= maxTimestamp) {
                        long newTimestampEnd = nextNewGameStart + newGameLengthSeconds;

                        long nextOldGameStart = oldTimestampStart;
                        while (nextOldGameStart <= newTimestampEnd) {
                            long oldTimestampEnd = nextOldGameStart + oldGameLengthSeconds;

                            if (nextOldGameStart < newTimestampEnd && nextNewGameStart < oldTimestampEnd) {
                                return true; // Conflict detected
                            }

                            // Get next occurrence of the old game
                            nextOldGameStart = FutureGameDBHandler.getNextGameTime(oldTimestampStart, nextOldGameStart, oldFrequency);
                        }

                        // Get next occurrence of the new game
                        nextNewGameStart = FutureGameDBHandler.getNextGameTime(newTimestampStart, nextNewGameStart, newFrequency);
                    }
                }
            }
        }

        return false; // No conflicts found
    }

    public static boolean isLengthGreaterThanFrequency(String gameLength, String frequency) {
        int gameLengthSeconds = getGameLengthInSeconds(gameLength);
        return switch (frequency) {
            case "daily" -> gameLengthSeconds > 86400;
            case "weekly" -> gameLengthSeconds > 604800;
            case "biweekly" -> gameLengthSeconds > 1209600;
            case "monthly" -> gameLengthSeconds > 2419200;
            case "yearly" -> false; // max length is 999 hours
            default -> false;
        };

    }

    public static long getNextGameTime(long originalTimestamp, long currentTimestamp, String frequency) {
        switch (frequency) {
            case "daily":
                return currentTimestamp + 86400;
            case "weekly":
                return currentTimestamp + 604800;
            case "biweekly":
                return currentTimestamp + 1209600;
            case "monthly":
                LocalDateTime originalMDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(originalTimestamp), ZoneId.systemDefault());
                LocalDateTime currentMDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(currentTimestamp), ZoneId.systemDefault());

                // Move forward one year
                LocalDateTime nextMonth = currentMDateTime.plusMonths(1);

                // Get the last valid day of the new month in the new year
                int lastDayOfNextMonth = nextMonth.getMonth().length(nextMonth.toLocalDate().isLeapYear());

                // Use the original day, or the last valid day if it doesn't exist
                nextMonth = nextMonth.withDayOfMonth(Math.min(originalMDateTime.getDayOfMonth(), lastDayOfNextMonth));

                return nextMonth.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
            case "yearly":
                LocalDateTime originalYDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(originalTimestamp), ZoneId.systemDefault());
                LocalDateTime currentYDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(currentTimestamp), ZoneId.systemDefault());

                // Move forward one year
                LocalDateTime nextYear = currentYDateTime.plusYears(1);

                // Get the last valid day of the new month in the new year
                int lastDayOfNextYear = nextYear.getMonth().length(nextYear.toLocalDate().isLeapYear());

                // Use the original day, or the last valid day if it doesn't exist
                nextYear = nextYear.withDayOfMonth(Math.min(originalYDateTime.getDayOfMonth(), lastDayOfNextYear));

                return nextYear.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
            default:
                return 0;
        }
    }

    public static int getGameLengthInSeconds(String gameLength) {
        int gameLengthSeconds;
        if (gameLength.contains("m")) {
            int gameLengthInt = Integer.parseInt(gameLength.replace("m", ""));
            gameLengthSeconds = gameLengthInt * 60;
        } else {
            int gameLengthInt = Integer.parseInt(gameLength.replace("h", ""));
            gameLengthSeconds = gameLengthInt * 3600;
        }
        return gameLengthSeconds;
    }

    public static List<ScheduledGame> getAllGames() {
        List<ScheduledGame> games = new ArrayList<>();
        List<String> gamesToRemove = new ArrayList<>();

        for (String name : config.getKeys(false)) {
            boolean remove = false;
            long originalTimestamp = config.getLong(name + ".originalTimestamp");
            long currentTimestamp = config.getLong(name + ".currentTimestamp");
            String frequency = Objects.requireNonNull(config.getString(name + ".frequency"));
            String length = config.getString(name + ".length");
            String w = config.getString(name + ".world");
            String pos1 = config.getString(name + ".pos1");
            String pos2 = config.getString(name + ".pos2");

            if (currentTimestamp * 1000 < System.currentTimeMillis()) {
                if (frequency.equalsIgnoreCase("once")) {
                    // Remove one-time games
                    gamesToRemove.add(name);
                    remove = true;
                } else {
                    // Keep advancing the timestamp until it's in the future
                    long newTimestamp = currentTimestamp;
                    do {
                        newTimestamp = FutureGameDBHandler.getNextGameTime(originalTimestamp, newTimestamp, frequency);
                    } while (newTimestamp * 1000 < System.currentTimeMillis()); // Ensure it's in the future

                    config.set(name + ".currentTimestamp", newTimestamp);
                    currentTimestamp = newTimestamp;
                }
            }

            if(!remove) {
                if (w == null || pos1 == null || pos2 == null) {
                    Bukkit.getLogger().severe("Invalid configuration for game: " + name);
                    continue;  // Skip this game if any value is missing
                }
                World world = Bukkit.getServer().getWorld(w);
                Hill hill = getHill(pos1, world, pos2);
                ScheduledGame game = new ScheduledGame(name, originalTimestamp, currentTimestamp, frequency, length, hill);
                games.add(game);
            }
        }

        // Remove past games
        for (String gameName : gamesToRemove) {
            config.set(gameName, null);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save scheduled games file: " + e.getMessage());
            e.printStackTrace();
        }

        return games;
    }

    public static int getNumGames() {
        List<ScheduledGame> games = getAllGames();
        return games.size();
    }

    @NotNull
    private static Hill getHill(String pos1, World world, String pos2) {
        String[] pos1StrArr = pos1.split(",");
        Location pos1Loc = new Location(world, Double.parseDouble(pos1StrArr[0]), Double.parseDouble(pos1StrArr[1]), Double.parseDouble(pos1StrArr[2]));

        String[] pos2StrArr = pos2.split(",");
        Location pos2Loc = new Location(world, Double.parseDouble(pos2StrArr[0]), Double.parseDouble(pos2StrArr[1]), Double.parseDouble(pos2StrArr[2]));

        return new Hill(pos1Loc, pos2Loc, world);
    }
}
