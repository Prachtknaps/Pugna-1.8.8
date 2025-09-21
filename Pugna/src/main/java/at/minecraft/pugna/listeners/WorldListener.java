package at.minecraft.pugna.listeners;

import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {
    private final PugnaConfig pugnaConfig;
    private final GameManager gameManager;

    public WorldListener(PugnaConfig pugnaConfig, GameManager gameManager) {
        this.pugnaConfig = pugnaConfig;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        GameState state = gameManager.getState();
        World world = event.getWorld();

        if (world.getName().equals(pugnaConfig.getLobbyWorldName())) {
            event.setCancelled(true);
            return;
        }

        if (world.getName().equals(pugnaConfig.getPugnaWorldName())) {
            if (state != GameState.GAME_RUNNING && state != GameState.GAME_PAUSED && state != GameState.RESTARTING) {
                event.setCancelled(true);
            }
        }
    }
}
