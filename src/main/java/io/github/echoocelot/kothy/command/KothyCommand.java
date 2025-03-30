package io.github.echoocelot.kothy.command;

import io.github.echoocelot.kothy.Kothy;
import io.github.echoocelot.kothy.api.ConfigManager;
import io.github.echoocelot.kothy.api.KothyMessaging;
import io.github.echoocelot.kothy.handler.BlacklistDBHandler;
import io.github.echoocelot.kothy.handler.FutureGameDBHandler;
import io.github.echoocelot.kothy.api.GameManager;
import io.github.echoocelot.kothy.handler.Paginator;
import io.github.echoocelot.kothy.handler.ResultsDBHandler;
import io.github.echoocelot.kothy.object.Hill;
import io.github.echoocelot.kothy.object.ScheduledGame;
import io.github.echoocelot.kothy.runnable.ParticleTimer;
import io.github.echoocelot.kothy.scheduler.FutureGameScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import io.github.echoocelot.kothy.runnable.GameTimer;
import io.github.echoocelot.kothy.api.PositionManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.stream.Collectors;

public class KothyCommand implements TabExecutor {

    @NotNull
    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            KothyMessaging.sendErrorMessageToSender(sender, "Only players can use this command");
            return true;
        }

        if (args.length == 0) {
            KothyMessaging.sendInfoMessage(player);
            return true;
        }

        switch (args[0]) {
            case "select" -> {
                if (!player.hasPermission("kothy.command.kothy.select")) {
                    KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                    return true;
                }
                PositionManager pm = PositionManager.getPlayerInstance(player);
                if (args[1].equals("pos1")) {
                    Location pos1;
                    if (args.length > 4) {
                        World w = player.getWorld();
                        try {
                            if (args[2].contains(".") || args[3].contains(".") || args[4].contains(".")) {
                                throw new Exception();
                            }
                            pos1 = new Location(w, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                            pm.setPos1(pos1);
                        } catch (Exception e) {
                            KothyMessaging.sendErrorMessage(player, "Please only enter whole number coordinates");
                            pm.setPos1Entered(false);
                            return true;
                        }
                        pm.setPos1Entered(true);
                        pm.setWorld(w);
                        KothyMessaging.sendMessage(player, "Set first position to " + pos1.getBlockX() + ", " + pos1.getBlockY() + ", " + pos1.getBlockZ());
                    } else if (args.length > 2 && args[2].equals("here")) {
                        pos1 = player.getLocation();
                        pos1.setY(pos1.getBlockY() - 1);
                        pm.setPos1(pos1);
                        pm.setPos1Entered(true);
                        pm.setWorld(player.getWorld());
                        KothyMessaging.sendMessage(player, "Set first position to " + pos1.getBlockX() + ", " + pos1.getBlockY() + ", " + pos1.getBlockZ());
                    } else {
                        KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                    }
                } else if (args[1].equals("pos2")) {
                    Location pos2;
                    if (args.length > 4) {
                        World w = player.getWorld();
                        try {
                            if (args[2].contains(".") || args[3].contains(".") || args[4].contains(".")) {
                                throw new Exception();
                            }
                            pos2 = new Location(w, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                            pm.setPos2(pos2);
                        } catch (Exception e) {
                            KothyMessaging.sendErrorMessage(player, "Please only enter whole number coordinates");
                            pm.setPos2Entered(false);
                            return true;
                        }
                        pm.setPos2Entered(true);
                        pm.setWorld(w);
                        KothyMessaging.sendMessage(player, "Set second position to " + pos2.getBlockX() + ", " + pos2.getBlockY() + ", " + pos2.getBlockZ());
                    } else if (args.length > 2 && args[2].equals("here")) {
                        pos2 = player.getLocation();
                        pos2.setY(pos2.getBlockY() - 1);
                        pm.setPos2(pos2);
                        pm.setPos2Entered(true);
                        pm.setWorld(player.getWorld());
                        KothyMessaging.sendMessage(player, "Set second position to " + pos2.getBlockX() + ", " + pos2.getBlockY() + ", " + pos2.getBlockZ());
                    } else {
                        KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                    }
                } else {
                    KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                }

                return true;
            }

            case "start" -> {
                if (!player.hasPermission("kothy.command.kothy.start")) {
                    KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                    return true;
                }
                PositionManager pm = PositionManager.getPlayerInstance(player);
                if (GameManager.isGameOccurring())
                    KothyMessaging.sendErrorMessage(player, "Game already occurring");
                else if (args.length > 2) {
                    Pattern pattern = Pattern.compile("^[1-9]\\d{0,2}[mh]$", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(args[2]);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        GameManager.setGameName(args[1]);
                        GameManager.setGameLength(args[2]);
                        if (pm.getPos1Entered() && pm.getPos2Entered()) {
                            World w = player.getWorld();
                            Hill gameHill = new Hill(pm.getPos1(), pm.getPos2(), w);
                            GameManager.setGameHill(gameHill);
                            Location center = gameHill.getHillCenter();
                            if(!FutureGameDBHandler.doesGameCoincide(System.currentTimeMillis()/1000, "once", args[2])) {
                                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                                    KothyMessaging.sendMessage(p, "King of the Hill " + (args[1]) + " starting at " + center.x()
                                            + ", " + center.y() + ", " + center.z() + " in " + gameHill.getWorld().getName() + "!");
                                }
                                GameManager.setGameOccurring(true);
                                pm.clearPositions();
                                ParticleTimer.stopParticleTaskForSelection(player);

                                GameTimer timer = new GameTimer();
                                Thread t = new Thread(timer);
                                t.start();
                            }
                        } else {
                            KothyMessaging.sendMessage(player, "Please select a hill area");
                        }
                    } else {
                        KothyMessaging.sendErrorMessage(player, "Invalid game duration entered");
                        return true;
                    }
                } else {
                    KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                }

                return true;
            }

            case "schedule" -> {
                if (!player.hasPermission("kothy.command.kothy.schedule")) {
                    KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                    return true;
                }
                PositionManager pm = PositionManager.getPlayerInstance(player);
                if (args[1].equals("new")) {
                    if (!player.hasPermission("kothy.command.kothy.schedule.new")) {
                        KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                        return true;
                    }
                    if (args.length > 5) {
                        Pattern pattern = Pattern.compile("^[1-9]\\d{0,2}[mh]$", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(args[4]);
                        boolean matchFound = matcher.find();
                        if (matchFound) {
                            String name = args[2];
                            long timestamp;
                            String gameLength = args[4];
                            String frequency = args[5];

                            switch (frequency) {
                                case "daily", "weekly", "biweekly", "monthly", "yearly", "once" -> {
                                }
                                default -> {
                                    KothyMessaging.sendErrorMessage(player, "Invalid frequency entered");
                                    return true;
                                }
                            }

                            try {
                                timestamp = Long.parseLong(args[3]);
                            } catch (NumberFormatException e) {
                                KothyMessaging.sendErrorMessage(player, "Invalid timestamp entered");
                                return true;
                            }

                            int timestampLength = String.valueOf(timestamp).length();
                            long currentTimeMillis = System.currentTimeMillis();
                            if (timestampLength == 13) {
                                timestamp /= 1000;
                            } else if (timestampLength != 10) {
                                KothyMessaging.sendErrorMessage(player, "Invalid timestamp entered");
                                return true;
                            }

                            long finalTimestamp = timestamp;
                            if (finalTimestamp > currentTimeMillis / 1000) {
                                if (FutureGameDBHandler.isLengthGreaterThanFrequency(gameLength, frequency)) {
                                    KothyMessaging.sendErrorMessage(player, "Game length is greater than frequency!");
                                } else {
                                    // Run doesGameCoincide() asynchronously to prevent server freezing
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                        boolean coincides = FutureGameDBHandler.doesGameCoincide(finalTimestamp, frequency, gameLength);

                                        Bukkit.getScheduler().runTask(plugin, () -> { // Switch back to main thread
                                            if (coincides) {
                                                KothyMessaging.sendErrorMessage(player, "Game coincides with another scheduled game!");
                                            } else {
                                                if (pm.getPos1Entered() && pm.getPos2Entered()) {
                                                    World world = player.getWorld();
                                                    Location pos1 = pm.getPos1();
                                                    Location pos2 = pm.getPos2();
                                                    Hill hill = new Hill(pos1, pos2, world);
                                                    ScheduledGame game = new ScheduledGame(name, finalTimestamp, finalTimestamp, frequency, gameLength, hill);
                                                    boolean successful = FutureGameDBHandler.addGame(game);
                                                    if (!successful) {
                                                        KothyMessaging.sendErrorMessage(player, "Game name already exists!");
                                                    } else {
                                                        Date date = new Date(finalTimestamp * 1000);
                                                        KothyMessaging.sendMessage(player, "Game scheduled for " + date);
                                                        pm.clearPositions();
                                                        ParticleTimer.stopParticleTaskForSelection(player);
                                                    }
                                                } else {
                                                    KothyMessaging.sendMessage(player, "Please select a hill area and try again");
                                                }
                                            }
                                        });
                                    });
                                }
                            } else {
                                KothyMessaging.sendErrorMessage(player, "The entered timestamp must be in the future!");
                                return true;
                            }
                        } else {
                            KothyMessaging.sendErrorMessage(player, "Invalid game duration entered");
                            return true;
                        }
                    } else {
                        KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                    }
                } else if (args[1].equals("remove")) {
                    if (!player.hasPermission("kothy.command.kothy.schedule.remove")) {
                        KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                        return true;
                    }
                    if (args.length > 2) {
                        String name = args[2];
                        if (FutureGameScheduler.removeGame(name)) {
                            KothyMessaging.sendMessage(player, name + " successfully removed!");
                        } else {
                            KothyMessaging.sendErrorMessage(player, "Game with name " + name + " not found.");
                        }
                    } else {
                        KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                    }
                } else if (args[1].equals("list")) {
                    if (!player.hasPermission("kothy.command.kothy.schedule.list")) {
                        KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                        return true;
                    }
                    List<ScheduledGame> games = FutureGameDBHandler.getAllGames();
                    List<String> gameDescriptions = new ArrayList<>();
                    if (args.length == 2) {
                        for (ScheduledGame game : games) {
                            String desc = game.getGameDescription();
                            gameDescriptions.add(desc);
                        }
                        Paginator.paginateList("Scheduled Games", "Name - Next Game - Length - Frequency - Location", gameDescriptions,
                                10, 1, player, "/kothy schedule list", ConfigManager.getGameNameLength());
                    } else if (args.length == 3) {
                        try {
                            int page = Integer.parseInt(args[1]);
                            for (ScheduledGame game : games) {
                                String desc = game.getGameDescription();
                                gameDescriptions.add(desc);
                            }
                            Paginator.paginateList("Scheduled Games", "Name - Next Game - Length - Frequency - Location", gameDescriptions,
                                    10, page, player, "/kothy schedule list", ConfigManager.getGameNameLength());
                        } catch (NumberFormatException nfe) {
                            for (ScheduledGame game : games) {
                                if (args[2].equals(game.getName())) {
                                    String desc = game.getGameDescription();
                                    gameDescriptions.add(desc);
                                }
                            }
                            Paginator.paginateList("Scheduled Games", "Name - Next Game - Length - Frequency - Location", gameDescriptions,
                                    10, 1, player, "/kothy schedule list " + args[2], ConfigManager.getGameNameLength());
                        }
                    } else if (args.length == 4) {
                        try {
                            int page = Integer.parseInt(args[2]);
                            for (ScheduledGame game : games) {
                                if (args[2].equals(game.getName())) {
                                    String desc = game.getGameDescription();
                                    gameDescriptions.add(desc);
                                }
                            }
                            Paginator.paginateList("Scheduled Games", "Name - Next Game - Length - Frequency - Location", gameDescriptions,
                                    10, page, player, "/kothy schedule list " + args[2], ConfigManager.getGameNameLength());
                        } catch (NumberFormatException nfe) {
                            KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                        }
                    }
                } else {
                    KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                }
                return true;
            }

            case "blacklist" -> {
                if (!player.hasPermission("kothy.command.kothy.blacklist")) {
                    KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                    return true;
                }
                if (args.length > 2) {
                    switch (args[1]) {
                        case "add" -> {
                            if (!player.hasPermission("kothy.command.kothy.blacklist.add")) {
                                KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                                return true;
                            }
                            String username = args[2];
                            if (!BlacklistDBHandler.isPlayerBlacklisted(username)) {
                                if (BlacklistDBHandler.blacklistPlayer(username)) {
                                    KothyMessaging.sendMessage(player, username + " successfully blacklisted!");
                                } else KothyMessaging.sendErrorMessage(player, "Player not found");
                            } else KothyMessaging.sendErrorMessage(player, "Player already blacklisted");
                        }
                        case "remove" -> {
                            if (!player.hasPermission("kothy.command.kothy.blacklist.remove")) {
                                KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                                return true;
                            }
                            String username = args[2];
                            if (BlacklistDBHandler.isPlayerBlacklisted(username)) {
                                if (BlacklistDBHandler.unblacklistPlayer(username)) {
                                    KothyMessaging.sendMessage(player, username + " successfully unblacklisted!");
                                } else KothyMessaging.sendErrorMessage(player, "Player not found");
                            } else KothyMessaging.sendErrorMessage(player, "Player not blacklisted");
                        }
                        case "contains" -> {
                            if (!player.hasPermission("kothy.command.kothy.blacklist.contains")) {
                                KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                                return true;
                            }
                            String username = args[2];
                            if (BlacklistDBHandler.isPlayerBlacklisted(username)) {
                                KothyMessaging.sendMessage(player, username + " is blacklisted");
                            } else
                                KothyMessaging.sendMessage(player, username + " is not blacklisted");
                        }
                        case "list" -> {
                            if (!player.hasPermission("kothy.command.kothy.blacklist.list")) {
                                KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                                return true;
                            }
                            try {
                                int page = Integer.parseInt(args[2]);
                                List<String> blacklist = BlacklistDBHandler.getBlacklist();
                                Paginator.paginateList("Blacklisted Players", blacklist, 10, page,
                                        player, "/kothy blacklist list", ConfigManager.getGameNameLength());
                            } catch (NumberFormatException nfe) {
                                KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                            }
                        }
                        default ->
                                KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                    }
                } else if (args.length > 1) {
                    if (args[1].equals("list")) {
                        if (!player.hasPermission("kothy.command.kothy.blacklist.list")) {
                            KothyMessaging.sendErrorMessage(player, "Insufficient permissions");
                            return true;
                        }
                        List<String> blacklist = BlacklistDBHandler.getBlacklist();
                        Paginator.paginateList("Blacklisted Players", blacklist, 10, 1,
                                player, "/kothy blacklist list", ConfigManager.getGameNameLength());
                    } else
                        KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                } else {
                    KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                }

                return true;
            }

            case "results" -> {
                List<String> results = ResultsDBHandler.getResults();
                if (args.length == 1) {
                    Paginator.paginateList("Past Game Results", "Name - Date - Winner - Score", results,
                            10, 1, player, "/kothy results", ConfigManager.getGameNameLength());
                } else if (args.length == 2) {
                    try {
                        int page = Integer.parseInt(args[1]);
                        Paginator.paginateList("Past Game Results", "Name - Date - Winner - Score", results,
                                10, page, player, "/kothy results", ConfigManager.getGameNameLength());
                    } catch (NumberFormatException nfe) {
                        List<String> shortenedResults = new ArrayList<>();
                        String searchQuery = args[1];
                        for (String result : results) {
                            if (result.contains(searchQuery)) shortenedResults.add(result);
                        }
                        Paginator.paginateList("Past Game Results", "Name - Date - Winner - Score", shortenedResults,
                                10, 1, player, "/kothy results " + searchQuery, ConfigManager.getGameNameLength());
                    }
                } else if (args.length == 3) {
                    try {
                        int page = Integer.parseInt(args[2]);
                        List<String> shortenedResults = new ArrayList<>();
                        String searchQuery = args[1];
                        for (String result : results) {
                            if (result.contains(searchQuery)) shortenedResults.add(result);
                        }
                        Paginator.paginateList("Past Game Results", "Name - Date - Winner - Score", shortenedResults, 10, page,
                                player, "/kothy results " + searchQuery, ConfigManager.getGameNameLength());
                    } catch (NumberFormatException nfe) {
                        KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                    }
                } else {
                    KothyMessaging.sendErrorMessage(player, "Invalid command syntax. Please try again");
                }
                return true;
            }

            case "forceend" -> {
                if (GameManager.isGameOccurring()) {
                    GameTimer.endGame();
                } else KothyMessaging.sendErrorMessage(player, "Game not currently occurring");
                return true;
            }

            default -> {
                KothyMessaging.sendErrorMessage(player, "Unknown arguments. Please try again");
                return true;
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> empty = new ArrayList<>();

        // Helper function to filter suggestions
        Function<List<String>, List<String>> filterSuggestions = suggestions ->
                suggestions.stream()
                        .filter(s -> s.startsWith(args[args.length - 1].toLowerCase()))
                        .collect(Collectors.toList());

        if (args.length == 1) {
            List<String> firstArguments = List.of("select", "start", "schedule", "results", "blacklist", "forceend");
            if (args[0].isEmpty()) return firstArguments;

            return filterSuggestions.apply(firstArguments);
        } else {
            switch (args[0]) {
                case "blacklist" -> {
                    List<String> blacklistFirstArguments = List.of("add", "remove", "contains", "list");
                    if (args.length == 2) {
                        if (args[1].isEmpty()) return blacklistFirstArguments;

                        return filterSuggestions.apply(blacklistFirstArguments);
                    }

                    if (Objects.equals(args[1], "add") || Objects.equals(args[1], "remove") || Objects.equals(args[1], "contains")) {
                        return Collections.singletonList("{player}");
                    } else return empty;
                }
                case "select" -> {
                    List<String> selectFirstArguments = List.of("pos1", "pos2");
                    if (args.length == 2) {
                        if (args[1].isEmpty()) return selectFirstArguments;

                        return filterSuggestions.apply(selectFirstArguments);
                    }

                    if (Objects.equals(args[1], "pos1") || Objects.equals(args[1], "pos2")) {
                        if (args.length == 3) return List.of("here", "{x}");
                        else if (args.length == 4 && args[2].equals("{x}")) return Collections.singletonList("{y}");
                        else if (args.length == 5 && args[3].equals("{y}")) return Collections.singletonList("{z}");
                    } else return empty;
                }
                case "start" -> {
                    if (args.length == 2) return Collections.singletonList("{name}");
                    else if (args.length == 3) {
                        List<String> durationList = List.of("15m", "30m", "1h", "{duration}");
                        if (args[2].isEmpty()) return durationList;

                        return filterSuggestions.apply(durationList);
                    }
                    else return empty;
                }
                case "schedule" -> {
                    List<String> scheduleFirstArguments = List.of("new", "remove", "list");

                    if (args.length == 2) {
                        if (args[1].isEmpty()) return scheduleFirstArguments;

                        return filterSuggestions.apply(scheduleFirstArguments);
                    }

                    if(args[1].equals("new")) {
                        if (args.length == 3) return Collections.singletonList("{name}");
                        else if (args.length == 4) return Collections.singletonList("{timestamp}");
                        else if (args.length == 5) {
                            List<String> durationList = List.of("15m", "30m", "1h", "{duration}");
                            if (args[4].isEmpty()) return durationList;

                            return filterSuggestions.apply(durationList);
                        }
                        else if (args.length == 6) {
                            List<String> frequencyList = List.of("daily", "weekly", "biweekly", "monthly", "yearly", "once");
                            if (args[5].isEmpty()) return frequencyList;

                            return filterSuggestions.apply(frequencyList);
                        }
                    }
                    else if(args[1].equals("remove")) {
                        if (args.length == 3) {
                            List<ScheduledGame> games = FutureGameDBHandler.getAllGames();
                            List<String> scheduledGameNames = new ArrayList<>();
                            for (ScheduledGame game : games) {
                                scheduledGameNames.add(game.getName());
                            }
                            return scheduledGameNames;
                        }
                        else return empty;
                    }
                    else return empty;
                }
                default -> {
                    return empty;
                }
            }
        }
        return empty;
    }
}