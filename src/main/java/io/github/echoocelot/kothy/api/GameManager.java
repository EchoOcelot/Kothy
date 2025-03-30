package io.github.echoocelot.kothy.api;

import io.github.echoocelot.kothy.object.Hill;

public class GameManager {

    private static Hill gameHill;
    private static String gameLength;
    private static String gameName;
    private static boolean gameOccurring = false;

    // Getter and Setter for game
    public static Hill getGameHill() {
        return gameHill;
    }

    public static void setGameHill(Hill hill) {
        GameManager.gameHill = hill;
    }

    // Getter and Setter for gameLength
    public static String getGameLength() {
        return gameLength;
    }

    public static void setGameLength(String gameLength) {
        GameManager.gameLength = gameLength;
    }

    // Getter and Setter for gameName
    public static String getGameName() {
        return gameName;
    }

    public static void setGameName(String gameName) {
        GameManager.gameName = gameName;
    }

    // Getter and Setter for gameOccurring
    public static boolean isGameOccurring() {
        return gameOccurring;
    }

    public static void setGameOccurring(boolean gameOccurring) {
        GameManager.gameOccurring = gameOccurring;
    }
}
