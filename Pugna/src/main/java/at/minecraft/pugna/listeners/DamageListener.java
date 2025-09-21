package at.minecraft.pugna.listeners;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DamageListener implements Listener {
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;
    private final WorldManager worldManager;
    private final GameManager gameManager;

    public DamageListener(PugnaConfig pugnaConfig, MessageConfig messageConfig, WorldManager worldManager, GameManager gameManager) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;
        this.worldManager = worldManager;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        GameState state = gameManager.getState();
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (PlayerUtils.isSpectator(player)) {
                event.setCancelled(true);
                return;
            }
        }

        if (state == GameState.RESTARTING) {
            if (entity instanceof Player) {
                event.setCancelled(true);
                return;
            }
        }

        if (state != GameState.GAME_RUNNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        GameState state = gameManager.getState();
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        Player damagerPlayer = null;
        if (damager instanceof Player) {
            damagerPlayer = (Player) damager;
        } else if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Player) {
                damagerPlayer = (Player) shooter;
            }
        }

        if (damagerPlayer != null) {
            if (damagerPlayer.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            if (PlayerUtils.isSpectator(damagerPlayer)) {
                event.setCancelled(true);
                return;
            }

            if (state == GameState.RESTARTING) {
                if (!(victim instanceof Player)) {
                    return;
                }
            }

            if (state != GameState.GAME_RUNNING) {
                event.setCancelled(true);
                return;
            }
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
            return;
        }

        if (state == GameState.GAME_RUNNING && damagerPlayer != null && victim instanceof Player && !pugnaConfig.isFriendlyFire()) {
            Player victimPlayer = (Player) victim;
            Team damagerTeam = TeamUtils.getTeam(damagerPlayer);
            Team victimTeam = TeamUtils.getTeam(victimPlayer);

            if (damagerTeam != null && victimTeam != null && damagerTeam.getId() == victimTeam.getId()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");

        Player player = event.getEntity();
        Player killer = player.getKiller();

        Team team = TeamUtils.getTeam(player);
        if (team == null) {
            return;
        }

        String globalMessage;
        String playerMessage;
        if (killer == null) {
            globalMessage = messageConfig.getChatMessage(Message.PLAYER_DEATH_OTHERS).player(player.getName()).toString();
            playerMessage = messageConfig.getMessage(Message.PLAYER_DEATH_SELF);
        } else {
            globalMessage = messageConfig.getChatMessage(Message.PLAYER_KILLED_OTHERS).player(player.getName()).killer(killer.getName()).toString();
            playerMessage = messageConfig.getChatMessage(Message.PLAYER_KILLED_SELF).killer(killer.getName()).health(killer.getHealth()).toString();
        }

        ChatUtils.broadcast(globalMessage);
        player.sendMessage(playerMessage);

        team.remove(player);
        gameManager.handleElimination();

        Bukkit.getScheduler().runTaskLater(Pugna.getInstance(), () -> {
            player.spigot().respawn();
        }, 5 * 20L);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        GameState state = gameManager.getState();
        Player player = event.getPlayer();
        Location spawnLocation = worldManager.getPugnaSpawnLocation();

        if (spawnLocation != null) {
            event.setRespawnLocation(spawnLocation);
        }

        if (state == GameState.GAME_RUNNING) {
            PlayerUtils.setupSpectator(player);
        }

        PlayerUtils.handleVisibility();
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        GameState state = gameManager.getState();
        HumanEntity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (PlayerUtils.isSpectator(player)) {
                event.setCancelled(true);
                return;
            }
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
        }
    }
}
