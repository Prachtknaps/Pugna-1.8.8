package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.entity.Player;

public final class NetherUtils {
    private NetherUtils() {}

    private static Pugna plugin = null;
    private static WorldManager worldManager = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static WorldManager getWorldManager() {
        if (worldManager == null) {
            worldManager = getPlugin().getWorldManager();
        }

        return worldManager;
    }

    /* === Operations === */

    public static void teleportPlayersToOverworld() {
        for (Player player : PlayerUtils.getAllOnlinePlayers()) {
            if (player.getWorld().getName().equals(GameConfig.getPugnaNetherWorldName())) {
                player.teleport(getWorldManager().getPugnaWorldSpawn());
            }
        }
    }
}
