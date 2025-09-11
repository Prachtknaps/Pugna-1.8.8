package at.minecraft.pugna.utils;

import org.bukkit.entity.Player;

public final class ChatUtils {
    private ChatUtils() {}

    /* === Operations === */

    public static void broadcast(String message) {
        // TODO: Implement method
    }

    public static void sendTeamMessage(Player sender, String message) {
        // TODO: Implement method
    }

    public static void sendSpectatorMessage(String message) {
        // TODO: Implement method
    }

    public static String formatMessage(Player sender, String message) {
        return String.format("§e%s§7: §f%s", sender.getName(), message);
    }

    public static String formatGlobalMessage(Player sender, String message) {
        return String.format("§7[@all] §e%s§7: §f%s", sender.getName(), message);
    }

    public static String formatSpectatorMessage(Player sender, String message) {
        return String.format("§7%s§7: §f%s", sender.getName(), message);
    }
}
