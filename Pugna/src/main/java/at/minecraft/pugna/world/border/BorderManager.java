package at.minecraft.pugna.world.border;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public final class BorderManager {
    private BorderManager() {}

    private static Pugna plugin = null;
    private static PugnaConfig pugnaConfig = null;
    private static GameConfig gameConfig = null;
    private static WorldManager worldManager = null;
    private static GameManager gameManager = null;

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

    private static GameManager getGameManager() {
        if (gameManager == null) {
            gameManager = getPlugin().getGameManager();
        }

        return gameManager;
    }

    /* === Operations === */

    public static void updateBorder() {
        GameState state = getGameManager().getState();

        World pugnaWorld = getWorldManager().getPugnaWorld();
        if (pugnaWorld == null) {
            return;
        }

        WorldBorder worldBorder = pugnaWorld.getWorldBorder();
        if (worldBorder == null) {
            return;
        }

        PugnaConfig pugnaConfig = getPugnaConfig();
        GameConfig gameConfig = getGameConfig();

        double spawnX = pugnaConfig.getPugnaSpawnX();
        double spawnZ = pugnaConfig.getPugnaSpawnZ();

        double maxBorderSize = pugnaConfig.getMaxBorderSize();
        double borderBaseSize = pugnaConfig.getBorderBaseSize();
        double borderEndSize = pugnaConfig.getBorderEndSize();

        if (state == GameState.LOBBY_WAITING || state == GameState.LOBBY_COUNTDOWN) {
            worldBorder.setCenter(spawnX, spawnZ);
            worldBorder.setSize(borderBaseSize);

            gameConfig.saveCurrentBorderSize(borderBaseSize);
        } else if (state == GameState.GAME_COUNTDOWN) {
            double initialBorderSize = calculateInitialBorderSize();
            gameConfig.saveInitialBorderSize(initialBorderSize);

            worldBorder.setCenter(spawnX, spawnZ);
            worldBorder.setSize(initialBorderSize);

            gameConfig.saveCurrentBorderSize(initialBorderSize);
        } else if (state == GameState.GAME_RUNNING) {
            int seconds = gameConfig.getSeconds();

            if (seconds < pugnaConfig.getBorderShrinkStartSeconds()) {
                double initialBorderSize = Math.min(gameConfig.getInitialBorderSize(), maxBorderSize);

                worldBorder.setCenter(spawnX, spawnZ);
                worldBorder.setSize(initialBorderSize);
            } else if (seconds >= pugnaConfig.getBorderShrinkStartSeconds() && seconds < pugnaConfig.getBorderShrinkEndSeconds()) {
                double currentBorderSize = Math.min(worldBorder.getSize(), maxBorderSize);
                long duration = Math.max(0L, pugnaConfig.getBorderShrinkEndSeconds() - seconds);

                worldBorder.setCenter(spawnX, spawnZ);
                worldBorder.setSize(currentBorderSize);
                worldBorder.setSize(borderEndSize, duration);
            } else {
                worldBorder.setCenter(spawnX, spawnZ);
                worldBorder.setSize(borderEndSize);

                gameConfig.saveCurrentBorderSize(borderEndSize);
            }
        } else if (state == GameState.GAME_PAUSED) {
            double currentBorderSize = Math.min(worldBorder.getSize(), maxBorderSize);

            worldBorder.setCenter(spawnX, spawnZ);
            worldBorder.setSize(currentBorderSize);

            gameConfig.saveCurrentBorderSize(currentBorderSize);
        } else if (state == GameState.RESTARTING) {
            worldBorder.setCenter(spawnX, spawnZ);
            worldBorder.setSize(borderEndSize);

            gameConfig.saveCurrentBorderSize(borderEndSize);
        }
    }

    /* === Helpers === */

    private static double calculateInitialBorderSize() {
        double borderBaseSize = getPugnaConfig().getBorderBaseSize();
        double borderSizePerPlayer = getPugnaConfig().getBorderSizePerPlayer();
        int playersCount = PlayerUtils.getAlivePlayersCount();

        return Math.min(getPugnaConfig().getMaxBorderSize(), borderBaseSize + (playersCount * borderSizePerPlayer));
    }
}
