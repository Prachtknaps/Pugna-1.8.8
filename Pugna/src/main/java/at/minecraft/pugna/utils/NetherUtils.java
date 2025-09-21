package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class NetherUtils {
    private NetherUtils() {}

    private static Pugna plugin = null;
    private static PugnaConfig pugnaConfig = null;
    private static GameConfig gameConfig = null;
    private static WorldManager worldManager = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static PugnaConfig getPugnaConfig() {
        if (pugnaConfig == null) {
            pugnaConfig = getPlugin().getPugnaConfig();
        }

        return pugnaConfig;
    }

    private static GameConfig getGameConfig() {
        if (gameConfig == null) {
            gameConfig = getPlugin().getGameConfig();
        }

        return gameConfig;
    }

    private static WorldManager getWorldManager() {
        if (worldManager == null) {
            worldManager = getPlugin().getWorldManager();
        }

        return worldManager;
    }

    /* === Queries === */

    public static boolean allowNether(int seconds) {
        return seconds >= getPugnaConfig().getNetherStartSeconds() && seconds < getPugnaConfig().getNetherEndSeconds();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean allowNether() {
        int seconds = getGameConfig().getSeconds();
        return allowNether(seconds);
    }

    /* === Operations === */

    public static void closeNether() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(getPugnaConfig().getPugnaNetherWorldName())) {
                player.teleport(getWorldManager().getPugnaSpawnLocation());
            }
        }
    }
}
