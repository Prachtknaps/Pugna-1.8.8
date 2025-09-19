package at.minecraft.pugna.listeners;

import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {
    private final GameManager gameManager;

    public WorldListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        GameState state = gameManager.getState();
        World world = event.getWorld();

        if (world.getName().equals(GameConfig.getLobbyWorldName())) {
            event.setCancelled(true);
            return;
        }

        if (world.getName().equals(GameConfig.getPugnaWorldName())) {
            if (state != GameState.GAME_RUNNING && state != GameState.GAME_PAUSED && state != GameState.RESTARTING) {
                event.setCancelled(true);
            }
        }
    }
}
