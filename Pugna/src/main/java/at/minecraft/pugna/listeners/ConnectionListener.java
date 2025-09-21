package at.minecraft.pugna.listeners;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.NetherUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;
    private final WorldManager worldManager;
    private final GameManager gameManager;

    public ConnectionListener(PugnaConfig pugnaConfig, MessageConfig messageConfig, WorldManager worldManager, GameManager gameManager) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;
        this.worldManager = worldManager;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        Player player = event.getPlayer();
        String joinMessage = messageConfig.getChatMessage(Message.PLAYER_JOIN).player(player.getName()).toString();

        if (pugnaConfig.isDevelopment()) {
            if (!player.isOp()) {
                player.kickPlayer("");
                return;
            }

            player.setGameMode(GameMode.CREATIVE);
            player.teleport(worldManager.getPugnaSpawnLocation());
            ChatUtils.broadcast(joinMessage);
            return;
        }

        GameState state = gameManager.getState();
        if (state == GameState.LOBBY_WAITING) {
            PlayerUtils.setupPlayer(player);
            player.teleport(worldManager.getLobbySpawnLocation());
            ChatUtils.broadcast(joinMessage);
            if (Bukkit.getOnlinePlayers().size() >= pugnaConfig.getMinPlayersCount()) {
                gameManager.setState(GameState.LOBBY_COUNTDOWN);
            }
            player.sendMessage(messageConfig.getMessage(Message.TEAM_JOIN_HINT));
            PlayerUtils.showAllPlayers();
        } else if (state == GameState.LOBBY_COUNTDOWN) {
            if (Bukkit.getOnlinePlayers().size() >= pugnaConfig.getMaxPlayersCount()) {
                player.kickPlayer(messageConfig.getMessage(Message.KICK_SERVER_FULL));
                PlayerUtils.showAllPlayers();
                return;
            }
            PlayerUtils.setupPlayer(player);
            player.teleport(worldManager.getLobbySpawnLocation());
            ChatUtils.broadcast(joinMessage);
            player.sendMessage(messageConfig.getMessage(Message.TEAM_JOIN_HINT));
            PlayerUtils.showAllPlayers();
        } else if (state == GameState.GAME_COUNTDOWN) {
            if (PlayerUtils.isSpectator(player)) {
                PlayerUtils.setupSpectator(player);
                player.teleport(worldManager.getPugnaSpawnLocation());
                PlayerUtils.handleVisibility();
                return;
            }
            PlayerUtils.setupPlayer(player);
            ChatUtils.broadcast(joinMessage);
            PlayerUtils.handleVisibility();
        } else if (state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            if (PlayerUtils.isSpectator(player)) {
                PlayerUtils.setupSpectator(player);
                player.teleport(worldManager.getPugnaSpawnLocation());
                PlayerUtils.handleVisibility();
                return;
            }
            if (player.getWorld().getName().equals(pugnaConfig.getPugnaNetherWorldName()) && !NetherUtils.allowNether()) {
                player.teleport(worldManager.getPugnaSpawnLocation());
                player.sendMessage(messageConfig.getMessage(Message.NETHER_TELEPORT_NOT_ALLOWED));
            }
            ChatUtils.broadcast(joinMessage);
            PlayerUtils.handleVisibility();
            if (state == GameState.GAME_PAUSED) {
                Bukkit.getScheduler().runTaskLater(Pugna.getInstance(), () -> {
                    if (PlayerUtils.areEnoughPlayersOnline()) {
                        gameManager.setState(GameState.GAME_RUNNING);
                        ChatUtils.broadcast(messageConfig.getMessage(Message.GAME_RESUMED));
                    } else {
                        player.sendMessage(messageConfig.getMessage(Message.GAME_PAUSED));
                    }
                }, 5L);
            }
        } else if (state == GameState.RESTARTING) {
            player.kickPlayer(messageConfig.getMessage(Message.KICK_SERVER_RESTARTING));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        Player player = event.getPlayer();
        String quitMessage = messageConfig.getChatMessage(Message.PLAYER_QUIT).player(player.getName()).toString();

        if (pugnaConfig.isDevelopment()) {
            ChatUtils.broadcast(quitMessage);
            return;
        }

        GameState state = gameManager.getState();
        if (state == GameState.LOBBY_WAITING) {
            Team team = TeamUtils.getTeam(player);
            if (team != null) {
                team.leave(player);
            }
            ChatUtils.broadcast(quitMessage);
            PlayerUtils.showAllPlayers();
        } else if (state == GameState.LOBBY_COUNTDOWN) {
            Team team = TeamUtils.getTeam(player);
            if (team != null) {
                team.leave(player);
            }
            ChatUtils.broadcast(quitMessage);
            PlayerUtils.showAllPlayers();
            Bukkit.getScheduler().runTaskLater(Pugna.getInstance(), () -> {
                if (Bukkit.getOnlinePlayers().size() < pugnaConfig.getMinPlayersCount()) {
                    gameManager.setState(GameState.LOBBY_WAITING);
                    ChatUtils.broadcast(messageConfig.getMessage(Message.TELEPORT_COUNTDOWN_ABORTED));
                }
            }, 5L);
        } else if (state == GameState.GAME_COUNTDOWN) {
            ChatUtils.broadcast(quitMessage);
            PlayerUtils.handleVisibility();
        } else if (state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            if (!PlayerUtils.isSpectator(player)) {
                ChatUtils.broadcast(quitMessage);
            }
            PlayerUtils.handleVisibility();
            if (state == GameState.GAME_RUNNING) {
                Bukkit.getScheduler().runTaskLater(Pugna.getInstance(), () -> {
                    if (!PlayerUtils.areEnoughPlayersOnline()) {
                        gameManager.setState(GameState.GAME_PAUSED);
                        ChatUtils.broadcast(messageConfig.getMessage(Message.GAME_PAUSED));
                    }
                }, 5L);
            }
        } else if (state == GameState.RESTARTING) {
            ChatUtils.broadcast(quitMessage);
            PlayerUtils.showAllPlayers();
        }
    }
}
