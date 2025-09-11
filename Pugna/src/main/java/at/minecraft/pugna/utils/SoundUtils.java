package at.minecraft.pugna.utils;

import org.bukkit.Sound;

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
        // TODO: Implement method
    }
}
