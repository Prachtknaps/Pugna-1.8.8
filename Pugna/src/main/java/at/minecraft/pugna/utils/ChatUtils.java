package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ChatUtils {
    private ChatUtils() {}

    private static Pugna plugin = null;
    private static MessageConfig messageConfig = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static MessageConfig getMessageConfig() {
        if (messageConfig == null) {
            messageConfig = getPlugin().getMessageConfig();
        }

        return messageConfig;
    }

    /* === Operations === */

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        Bukkit.getLogger().info(getMessageConfig().getRawPrefix() + ChatColor.stripColor(message));
    }

    public static void sendGlobalMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        Bukkit.getLogger().info("[Global] " + ChatColor.stripColor(message));
    }

    public static void sendTeamMessage(Player sender, String message) {
        if (sender == null || !sender.isOnline()) {
            return;
        }

        Team team = TeamUtils.getTeam(sender);
        if (team != null) {
            team.broadcast(message);
        }

        Bukkit.getLogger().info("[Team] " + ChatColor.stripColor(message));
    }

    public static void sendSpectatorMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerUtils.isSpectator(player)) {
                player.sendMessage(message);
            }
        }

        Bukkit.getLogger().info("[Spectator] " + ChatColor.stripColor(message));
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
