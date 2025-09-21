package at.minecraft.pugna.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtils {
    private SoundUtils() {}

    /* === Operations === */

    public static void broadcast(Sound sound) {
        broadcast(sound, 1.0f, 1.0f);
    }

    public static void broadcast(Sound sound, float volume) {
        broadcast(sound, volume, 1.0f);
    }

    public static void broadcast(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

        Bukkit.getLogger().info("[Sound] Broadcasted sound '" + sound.toString() + "' (volume: " + volume + ", pitch: " + pitch + ") for all players.");
    }
}
