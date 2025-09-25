package at.minecraft.pugna.listeners;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.ItemUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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

        /* === Handle forbidden items === */
        if (event.getItemDrop() != null && event.getItemDrop().getItemStack() != null && ItemUtils.shouldBlock(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            purgeForbiddenFrom(player);
            return;
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

        /* === Handle forbidden items === */
        boolean blockedFound = false;

        ItemStack cursorItem = event.getCursor();
        if (cursorItem != null && cursorItem.getType() != Material.AIR && ItemUtils.shouldBlock(cursorItem)) {
            event.setCancelled(true);
            event.setCursor(null);
            blockedFound = true;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType() != Material.AIR && ItemUtils.shouldBlock(currentItem)) {
            event.setCancelled(true);
            event.setCurrentItem(null);
            blockedFound = true;
        }

        if (blockedFound) {
            purgeForbiddenFrom(player);
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
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        /* === Handle forbidden items === */
        ItemStack oldCursor = event.getOldCursor();
        if (oldCursor != null && oldCursor.getType() != Material.AIR && ItemUtils.shouldBlock(oldCursor)) {
            event.setCancelled(true);
            player.setItemOnCursor(null);
            purgeForbiddenFrom(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GameState state = gameManager.getState();

        /* === Handle forbidden items === */
        if (event.getPlayer() instanceof Player) {
            purgeForbiddenFrom((Player) event.getPlayer());
        }

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

    /* === Helpers === */

    private int purgeForbiddenFrom(Player player) {
        if (player == null || !player.isOnline()) {
            return 0;
        }

        int removedAmount = 0;
        boolean contentsModified = false;
        boolean armorModified = false;

        /* === Main Inventory === */
        ItemStack[] contents = player.getInventory().getContents();
        for (int slotIndex = 0; slotIndex < contents.length; slotIndex++) {
            ItemStack itemStack = contents[slotIndex];
            if (itemStack != null && itemStack.getType() != Material.AIR && ItemUtils.shouldBlock(itemStack)) {
                removedAmount += itemStack.getAmount();
                contents[slotIndex] = null;
                contentsModified = true;
            }
        }

        if (contentsModified) {
            player.getInventory().setContents(contents);
        }

        /* === Armor === */
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int slotIndex = 0; slotIndex < armor.length; slotIndex++) {
            ItemStack itemStack = armor[slotIndex];
            if (itemStack != null && itemStack.getType() != Material.AIR && ItemUtils.shouldBlock(itemStack)) {
                removedAmount += itemStack.getAmount();
                armor[slotIndex] = null;
                armorModified = true;
            }
        }
        if (armorModified) {
            player.getInventory().setArmorContents(armor);
        }

        /* === Cursor === */
        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem != null && cursorItem.getType() != Material.AIR && ItemUtils.shouldBlock(cursorItem)) {
            removedAmount += cursorItem.getAmount();
            player.setItemOnCursor(null);
        }

        if (removedAmount > 0) {
            String infoMessage = messageConfig.getChatMessage(Message.FORBIDDEN_ITEMS_REMOVED).count(removedAmount).toString();
            player.sendMessage(infoMessage);
            player.updateInventory();
        }

        return removedAmount;
    }

}
