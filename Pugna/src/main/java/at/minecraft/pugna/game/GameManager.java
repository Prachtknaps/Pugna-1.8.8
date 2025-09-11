package at.minecraft.pugna.game;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.countdowns.LobbyCountdown;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import at.minecraft.pugna.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    // TODO: Implement class
    private final WorldManager worldManager;

    private GameState state;
    private boolean allowNether;

    private final List<Team> teams;

    private LobbyCountdown lobbyCountdown;
    // TODO: private GameCountdown gameCountdown;
    // TODO: private RestartCountdown restartCountdown;

    // TODO: private GameTimer gameTimer;

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

    public boolean allowNether() {
        return allowNether;
    }

    public List<Team> getTeams() {
        return teams;
    }

    /* === Operations === */

    public void setAllowNether(boolean allowNether) {
        this.allowNether = allowNether;
    }

    public void setState(GameState state) {
        this.state = state;

        // TODO: Implement method
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
            // TODO: gameCountdown = new GameCountdown(this);
            // TODO: gameCountdown.start();
        } else if (state == GameState.GAME_RUNNING) {
            // TODO: if (gameTimer == null) {
            // TODO: gameTimer = new GameTimer(worldManager, this);
            // TODO: gameTimer.start(); }
        } else if (state == GameState.GAME_PAUSED) {
            ChatUtils.broadcast(ChatConfig.getMessage(Message.GAME_PAUSED));
        } else if (state == GameState.GAME_RESTARTING) {
            // TODO: if (gameTimer != null) {
            // TODO: gameTimer.cancel(); }
            PlayerUtils.clearSpectators();
            // TODO: restartCountdown = new RestartCountdown();
            // TODO: restartCountdown.start();
        }

        worldManager.updateWorlds(state);
    }

    public void handleElimination() {
        // TODO: Implement method
    }

    public void checkForWinner() {
        // TODO: Implement method
    }
}
