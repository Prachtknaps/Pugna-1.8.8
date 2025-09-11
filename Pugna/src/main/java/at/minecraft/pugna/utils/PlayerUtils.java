package at.minecraft.pugna.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class PlayerUtils {
    private PlayerUtils() {}

    /* === Queries === */

    public static List<Player> getAllOnlinePlayers() {
        // TODO: Implement method
        List<Player> players = new ArrayList<>();
        return players;
    }

    public static List<Player> getOnlinePlayers() {
        // TODO: Implement method
        return null;
    }

    public static int getAlivePlayersCount() {
        // TODO: Implement method
        // if state == LOBBY_WAITING, LOBBY_COUNTDOWN, GAME_COUNTDOWN or GAME_RESTARTING --> true
        return 0;
    }

    public static boolean areEnoughPlayersOnline() {
        // TODO: Implement method
        return true;
    }

    public static boolean isSpectator(Player player) {
        // TODO: Implement method
        return false;
    }

    /* === Operations === */

    public static void setupPlayer(Player player) {
        // TODO: Implement method
    }

    public static void setupSpectator(Player spectator) {
        // TODO: Implement method
    }

    public static void handleVisibility() {
        // TODO: Implement method
    }

    public static void showAllPlayers() {
        // TODO: Implement method
    }

    public static void kickAllPlayers(String message) {
        // TODO: Implement method
    }

    public static void clearSpectators() {
        // TODO: Implement method
    }

    public static void simulatePlayerDeath(Player player) {
        // TODO: Implement method
    }

    public static Player getClosestEnemy(Player player) {
        // TODO: Implement method
        return null;
    }
}
