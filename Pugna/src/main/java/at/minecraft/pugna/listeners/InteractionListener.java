package at.minecraft.pugna.listeners;

import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class InteractionListener implements Listener {
    private final GameManager gameManager;

    public InteractionListener(GameManager gameManager) {
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

        // TODO: Team Selection, Leave and Navigation

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
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

        if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
            event.setCancelled(true);
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

            if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
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

                if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
