package at.minecraft.pugna.listeners;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    private final WorldManager worldManager;
    private final GameManager gameManager;

    public ConnectionListener(WorldManager worldManager, GameManager gameManager) {
        this.worldManager = worldManager;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        GameState state = gameManager.getState();
        Player player = event.getPlayer();
        String joinMessage = ChatConfig.getChatMessage(Message.PLAYER_JOIN).player(player.getName()).toString();

        if (state == GameState.LOBBY_WAITING) {
            PlayerUtils.setupPlayer(player);
            player.teleport(worldManager.getLobbyWorldSpawn());
            //player.teleport(worldManager.getTeamSpawns().get(0).get(0)); // TESTING
            ChatUtils.broadcast(joinMessage);
            if (PlayerUtils.getAllOnlinePlayers().size() >= GameConfig.getMinPlayersCount()) {
                gameManager.setState(GameState.LOBBY_COUNTDOWN);
            }
            player.sendMessage(ChatConfig.getMessage(Message.TEAM_JOIN_HINT));
            PlayerUtils.showAllPlayers();
        } else if (state == GameState.LOBBY_COUNTDOWN) {
            if (PlayerUtils.getAllOnlinePlayers().size() >= GameConfig.getMaxPlayersCount()) {
                player.kickPlayer(ChatConfig.getMessage(Message.KICK_SERVER_FULL));
            } else {
                PlayerUtils.setupPlayer(player);
                player.teleport(worldManager.getLobbyWorldSpawn());
                ChatUtils.broadcast(joinMessage);
                player.sendMessage(ChatConfig.getMessage(Message.TEAM_JOIN_HINT));
            }
            PlayerUtils.showAllPlayers();
        } else if (state == GameState.GAME_COUNTDOWN) {
            if (GameConfig.allowReconnect()) {
                if (PlayerUtils.isSpectator(player)) {
                    PlayerUtils.setupSpectator(player);
                    player.teleport(worldManager.getPugnaWorldSpawn());
                } else {
                    PlayerUtils.setupPlayer(player);
                    ChatUtils.broadcast(joinMessage);
                }
            } else {
                PlayerUtils.setupSpectator(player);
                player.teleport(worldManager.getPugnaWorldSpawn());
            }
            PlayerUtils.handleVisibility();
        } else if (state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            if (GameConfig.allowReconnect()) {
                if (PlayerUtils.isSpectator(player)) {
                    PlayerUtils.setupSpectator(player);
                    player.teleport(worldManager.getPugnaWorldSpawn());
                } else {
                    if (player.getWorld().getName().equals(GameConfig.getPugnaNetherWorldName()) && !gameManager.allowNether()) {
                        player.teleport(worldManager.getPugnaWorldSpawn());
                        player.sendMessage(ChatConfig.getMessage(Message.NETHER_TELEPORT_NOT_ALLOWED));
                    }
                    ChatUtils.broadcast(joinMessage);
                    if (state == GameState.GAME_PAUSED) {
                        Bukkit.getScheduler().runTaskLater(Pugna.getInstance(), () -> {
                            if (PlayerUtils.areEnoughPlayersOnline()) {
                                gameManager.setState(GameState.GAME_RUNNING);
                            } else {
                                player.sendMessage(ChatConfig.getMessage(Message.GAME_PAUSED));
                            }
                        }, 5L);
                    }
                }
            } else {
                PlayerUtils.setupSpectator(player);
                player.teleport(worldManager.getPugnaWorldSpawn());
            }
            PlayerUtils.handleVisibility();

        } else if (state == GameState.GAME_RESTARTING) {
            player.kickPlayer(ChatConfig.getMessage(Message.KICK_SERVER_RESTARTING));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        GameState state = gameManager.getState();
        Player player = event.getPlayer();
        String quitMessage = ChatConfig.getChatMessage(Message.PLAYER_QUIT).player(player.getName()).toString();
        String deathMessage = ChatConfig.getChatMessage(Message.PLAYER_DEATH_OTHERS).player(player.getName()).toString();

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
                if (PlayerUtils.getAllOnlinePlayers().size() < GameConfig.getMinPlayersCount()) {
                    gameManager.setState(GameState.LOBBY_WAITING);
                    ChatUtils.broadcast(ChatConfig.getMessage(Message.TELEPORT_COUNTDOWN_ABORTED));
                }
            }, 5L);
        } else if (state == GameState.GAME_COUNTDOWN) {
            if (GameConfig.allowReconnect()) {
                ChatUtils.broadcast(quitMessage);
            } else {
                Team team = TeamUtils.getTeam(player);
                if (team != null) {
                    team.leave(player);
                    gameManager.handleElimination();
                }
                ChatUtils.broadcast(deathMessage);
            }
            PlayerUtils.handleVisibility();
        } else if (state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            if (GameConfig.allowReconnect()) {
                if (!PlayerUtils.isSpectator(player)) {
                    ChatUtils.broadcast(quitMessage);
                }
                if (state == GameState.GAME_RUNNING) {
                    Bukkit.getScheduler().runTaskLater(Pugna.getInstance(), () -> {
                        if (!PlayerUtils.areEnoughPlayersOnline()) {
                            gameManager.setState(GameState.GAME_PAUSED);
                        }
                    }, 5L);
                }
            } else {
                if (!PlayerUtils.isSpectator(player)) {
                    Team team = TeamUtils.getTeam(player);
                    if (team != null) {
                        team.leave(player);
                        gameManager.handleElimination();
                    }
                    ChatUtils.broadcast(deathMessage);
                    PlayerUtils.simulatePlayerDeath(player);
                }
            }
            PlayerUtils.handleVisibility();
        } else if (state == GameState.GAME_RESTARTING) {
            ChatUtils.broadcast(quitMessage);
            PlayerUtils.showAllPlayers();
        }
    }
}
