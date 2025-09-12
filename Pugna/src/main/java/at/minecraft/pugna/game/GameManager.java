package at.minecraft.pugna.game;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.countdowns.GameCountdown;
import at.minecraft.pugna.countdowns.LobbyCountdown;
import at.minecraft.pugna.countdowns.RestartCountdown;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final WorldManager worldManager;

    private GameState state;
    private boolean allowNether;

    private final List<Team> teams;

    private LobbyCountdown lobbyCountdown = null;
    private GameCountdown gameCountdown = null;
    @SuppressWarnings("FieldCanBeLocal")
    private RestartCountdown restartCountdown = null;

    private GameTimer gameTimer = null;

    public GameManager(WorldManager worldManager) {
        this.worldManager = worldManager;

        this.state = GameState.LOBBY_WAITING;
        this.allowNether = false;

        this.teams = new ArrayList<>();
        int capacity = GameConfig.getMaxTeamCapacity();
        for (int i = 0; i < GameConfig.getMaxTeamsCount(); i++) {
            String defaultName = GameConfig.getDefaultTeamNames().get(i);
            Team team = new Team(i, capacity);
            team.setName(defaultName);
            team.setSpawns(worldManager.getTeamSpawns().get(i));
            teams.add(team);
        }
    }

    /* === Getters === */

    public GameState getState() {
        return state;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean allowNether() {
        return allowNether;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public LobbyCountdown getLobbyCountdown() {
        return lobbyCountdown;
    }

    public GameCountdown getGameCountdown() {
        return gameCountdown;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    /* === Operations === */

    public void setAllowNether(boolean allowNether) {
        this.allowNether = allowNether;
    }

    public void setState(GameState state) {
        this.state = state;

        if (state == GameState.LOBBY_WAITING) {
            if (lobbyCountdown != null) {
                lobbyCountdown.cancel();
                lobbyCountdown = null;
            }
        } else if (state == GameState.LOBBY_COUNTDOWN) {
            lobbyCountdown = new LobbyCountdown(this);
            lobbyCountdown.start();
        } else if (state == GameState.GAME_COUNTDOWN) {
            TeamUtils.assignPlayers();
            TeamUtils.removeEmptyTeams();
            TeamUtils.teleportTeams();
            if (gameCountdown == null) {
                gameCountdown = new GameCountdown(this);
            }
            gameCountdown.start();
        } else if (state == GameState.GAME_RUNNING) {
            if (gameTimer == null) {
                gameTimer = new GameTimer(worldManager, this);
                gameTimer.start();
            }
        } else if (state == GameState.GAME_PAUSED) {
            ChatUtils.broadcast(ChatConfig.getMessage(Message.GAME_PAUSED));
        } else if (state == GameState.GAME_RESTARTING) {
            if (gameTimer != null) {
                gameTimer.cancel();
            }
            PlayerUtils.clearSpectators();
            restartCountdown = new RestartCountdown();
            restartCountdown.start();
        }

        worldManager.updateWorlds(state);
    }

    public void handleElimination() {
        if (state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING) {
            for (Team team : teams) {
                if (team.isEmpty() && team.getCapacity() >= 2) {
                    String message = ChatConfig.getChatMessage(Message.TEAM_ELIMINATED).team(team.getName()).toString();
                    ChatUtils.broadcast(message);
                }
            }

            TeamUtils.removeEmptyTeams();
            checkForWinner();
        }
    }

    public void checkForWinner() {
        if (teams.size() != 1) {
            return;
        }

        Team winnerTeam = teams.get(0);
        String message;

        if (winnerTeam.getPlayers().size() >= 2) {
            message = ChatConfig.getChatMessage(Message.TEAM_WIN).team(winnerTeam.getName()).toString();
        } else {
            Player winner = Bukkit.getPlayer(winnerTeam.getPlayers().get(0));
            if (winner != null && winner.isOnline()) {
                message = ChatConfig.getChatMessage(Message.PLAYER_WIN).player(winner.getName()).toString();
            } else {
                // Fallback
                message = ChatConfig.getChatMessage(Message.TEAM_WIN).team(winnerTeam.getName()).toString();
            }
        }

        if (state == GameState.GAME_COUNTDOWN) {
            gameCountdown.cancel();
        }

        ChatUtils.broadcast(message);
        setState(GameState.GAME_RESTARTING);
    }
}
