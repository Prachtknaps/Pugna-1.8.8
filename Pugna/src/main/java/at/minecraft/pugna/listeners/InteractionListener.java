package at.minecraft.pugna.listeners;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.gui.NavigationGUI;
import at.minecraft.pugna.gui.TeamSelectionGUI;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class InteractionListener implements Listener {
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;
    private final GameConfig gameConfig;
    private final GameManager gameManager;

    public InteractionListener(PugnaConfig pugnaConfig, MessageConfig messageConfig, GameConfig gameConfig, GameManager gameManager) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;
        this.gameConfig = gameConfig;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameState state = gameManager.getState();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        /* === Buttons and Pressure Plates in Lobby world during LOBBY_WAITING and LOBBY_COUNTDOWN === */
        if (state == GameState.LOBBY_WAITING || state == GameState.LOBBY_COUNTDOWN) {
            if (event.getClickedBlock() != null && player.getWorld().getName().equals(pugnaConfig.getLobbyWorldName())) {
                Material clickedType = event.getClickedBlock().getType();
                Action action = event.getAction();

                boolean isButton = clickedType == Material.STONE_BUTTON || clickedType == Material.WOOD_BUTTON;
                boolean isPlate  = clickedType == Material.WOOD_PLATE || clickedType == Material.STONE_PLATE || clickedType == Material.GOLD_PLATE || clickedType == Material.IRON_PLATE;
                boolean allowButtonUse = isButton && action == Action.RIGHT_CLICK_BLOCK;
                boolean allowPlateUse  = isPlate  && action == Action.PHYSICAL;

                if (allowButtonUse || allowPlateUse) {
                    return;
                }
            }
        }

        /* === Team Selection === */
        if (state == GameState.LOBBY_WAITING || state == GameState.LOBBY_COUNTDOWN) {
            ItemStack itemInHand = event.getItem();
            if (itemInHand != null && itemInHand.getType() == Material.BED && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() && itemInHand.getItemMeta().getDisplayName().equals(pugnaConfig.getTeamSelectionItemName())) {
                event.setCancelled(true);
                TeamSelectionGUI.openFor(player);
                return;
            }
        }

        /* === Info Book === */
        if (state == GameState.LOBBY_WAITING || state == GameState.LOBBY_COUNTDOWN) {
            ItemStack itemInHand = event.getItem();
            if (itemInHand != null && itemInHand.getType() == Material.WRITTEN_BOOK && itemInHand.hasItemMeta() && (BookMeta) itemInHand.getItemMeta() != null && ((BookMeta) itemInHand.getItemMeta()).hasTitle() && ((BookMeta) itemInHand.getItemMeta()).getTitle().equals(pugnaConfig.getInfoBookItemName())) {
                return;
            }
        }

        /* === Navigation === */
        if ((state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) && PlayerUtils.isSpectator(player)) {
            ItemStack itemInHand = event.getItem();
            if (itemInHand != null && itemInHand.getType() == Material.COMPASS && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() && itemInHand.getItemMeta().getDisplayName().equals(pugnaConfig.getNavigationItemName())) {
                event.setCancelled(true);
                NavigationGUI.openFor(player);
                return;
            }
        }

        /* === Leave === */
        ItemStack itemInHand = event.getItem();
        if (itemInHand != null && itemInHand.getType() == Material.MAGMA_CREAM && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() && itemInHand.getItemMeta().getDisplayName().equals(pugnaConfig.getLeaveItemName())) {
            event.setCancelled(true);
            player.performCommand("hub");
            return;
        }

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        GameState state = gameManager.getState();
        Player player = event.getPlayer();
        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (PlayerUtils.isSpectator(player)) {
            return;
        }

        if (state == GameState.GAME_PAUSED && toLocation != null) {
            if (fromLocation.getX() != toLocation.getX() || fromLocation.getY() != toLocation.getY() || fromLocation.getZ() != toLocation.getZ() || fromLocation.getYaw() != toLocation.getYaw() || fromLocation.getPitch() != toLocation.getPitch()) {
                event.setTo(fromLocation);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        GameState state = gameManager.getState();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
        }

        /* === Handle Diamond Discovery === */
        if (state == GameState.GAME_RUNNING && pugnaConfig.useFoundDiamonds() && !gameConfig.foundDiamonds() && event.getItem() != null && event.getItem().getItemStack() != null && event.getItem().getItemStack().getType() == Material.DIAMOND) {
            gameConfig.saveFoundDiamonds(true);
            String message = messageConfig.getMessage(Message.FOUND_DIAMONDS);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.LEVEL_UP, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        GameState state = gameManager.getState();
        Entity target = event.getTarget();

        if (target instanceof Player) {
            Player player = (Player) target;

            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
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

    @EventHandler
    public void onExperienceOrbPickup(EntityTargetLivingEntityEvent event) {
        GameState state = gameManager.getState();
        Entity entity = event.getEntity();
        LivingEntity target = event.getTarget();

        if (entity.getType() == EntityType.EXPERIENCE_ORB) {
            if (target instanceof Player) {
                Player player = (Player) target;

                if (player.getGameMode() == GameMode.CREATIVE) {
                    return;
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
    }
}
