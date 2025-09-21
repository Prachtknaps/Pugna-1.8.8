package at.minecraft.pugna.listeners;

import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryListener implements Listener {
    private final PugnaConfig pugnaConfig;
    private final GameManager gameManager;

    public InventoryListener(PugnaConfig pugnaConfig, GameManager gameManager) {
        this.pugnaConfig = pugnaConfig;
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

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        GameState state = gameManager.getState();

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        /* === Team Selection Inventory === */
        if (state == GameState.LOBBY_WAITING || state == GameState.LOBBY_COUNTDOWN) {
            String title = event.getView() != null ? event.getView().getTitle() : null;
            String teamSelectionTitle = ChatColor.stripColor(pugnaConfig.getTeamSelectionItemName());
            if (title != null && title.equalsIgnoreCase(teamSelectionTitle)) {
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                if (event.getCurrentItem().getType() != Material.WOOL) {
                    return;
                }

                if (!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
                    return;
                }

                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
                String teamName = ChatColor.stripColor(displayName).trim();

                player.closeInventory();
                player.performCommand("team join " + teamName);
            }
        }

        /* === Navigation Inventory === */
        if (state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            String title = event.getView() != null ? event.getView().getTitle() : null;
            String navigationTitle = ChatColor.stripColor(pugnaConfig.getNavigationItemName());
            if (title != null && title.equals(navigationTitle)) {
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                if (event.getCurrentItem().getType() != Material.SKULL_ITEM) {
                    return;
                }

                if (!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
                    return;
                }

                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
                String playerName = ChatColor.stripColor(displayName).trim();

                player.closeInventory();

                Player target = Bukkit.getPlayer(playerName);
                if (target != null && target.isOnline() && !PlayerUtils.isSpectator(target)) {
                    player.teleport(target);
                }
            }
        }

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
        }
    }
}
