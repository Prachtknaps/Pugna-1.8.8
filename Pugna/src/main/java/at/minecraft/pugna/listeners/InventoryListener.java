package at.minecraft.pugna.listeners;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;
    private final GameConfig gameConfig;
    private final GameManager gameManager;

    public InventoryListener(PugnaConfig pugnaConfig, MessageConfig messageConfig, GameConfig gameConfig, GameManager gameManager) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;
        this.gameConfig = gameConfig;
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

        /* === Handle Diamond Discovery === */
        if (state == GameState.GAME_RUNNING && pugnaConfig.useFoundDiamonds() && !gameConfig.foundDiamonds() && !PlayerUtils.isSpectator(player)) {
            Bukkit.getScheduler().runTask(Pugna.getInstance(), () -> {
                if (gameManager.getState() != GameState.GAME_RUNNING) {
                    return;
                }

                if (gameConfig.foundDiamonds()) {
                    return;
                }

                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack != null && itemStack.getType() == Material.DIAMOND && itemStack.getAmount() > 0) {
                        gameConfig.saveFoundDiamonds(true);
                        String message = messageConfig.getMessage(Message.FOUND_DIAMONDS);
                        ChatUtils.broadcast(message);
                        SoundUtils.broadcast(Sound.LEVEL_UP, 1.0f, 1.0f);
                        return;
                    }
                }
            });
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GameState state = gameManager.getState();

        /* === Handle Diamond Discovery === */
        if (state == GameState.GAME_RUNNING && event.getPlayer() instanceof Player && pugnaConfig.useFoundDiamonds() && !gameConfig.foundDiamonds()) {
            Player player = (Player) event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() == Material.DIAMOND && itemStack.getAmount() > 0) {
                    gameConfig.saveFoundDiamonds(true);
                    String message = messageConfig.getMessage(Message.FOUND_DIAMONDS);
                    ChatUtils.broadcast(message);
                    SoundUtils.broadcast(Sound.LEVEL_UP, 1.0f, 1.0f);
                    return;
                }
            }
        }
    }
}
