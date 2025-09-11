package at.minecraft.pugna.listeners;

import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryListener implements Listener {
    private final GameManager gameManager;

    public InventoryListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        GameState state = gameManager.getState();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
        }

        if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        GameState state = gameManager.getState();

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // TODO: Implement Teal Select Inventory and Navigation Inventory logic

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
            event.setCancelled(true);
        }
    }
}
