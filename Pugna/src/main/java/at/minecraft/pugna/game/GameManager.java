package at.minecraft.pugna.game;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.countdowns.GameCountdown;
import at.minecraft.pugna.countdowns.LobbyCountdown;
import at.minecraft.pugna.countdowns.RestartCountdown;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.BorderUtils;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
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
        this.teams = new ArrayList<>();
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

    /* === Setup === */

    public void prepareNewGame() {
        this.state = GameState.LOBBY_WAITING;
        this.allowNether = false;

        int maxTeamCapacity = GameConfig.getMaxTeamCapacity();
        for (int teamIndex = 0; teamIndex < GameConfig.getMaxTeamsCount(); teamIndex++) {
            String defaultName;
            try {
                defaultName = GameConfig.getDefaultTeamNames().get(teamIndex);
            } catch (Exception exception) {
                defaultName = "Team" + teamIndex;
            }
            Team team = new Team(teamIndex, maxTeamCapacity);
            team.setName(defaultName);
            team.setSpawns(worldManager.getTeamSpawns().get(teamIndex));
            teams.add(team);
        }
    }

    // GameManager.java
    public void resumeGame() {
        setState(GameState.GAME_PAUSED);

        int seconds = GameConfig.getSeconds();
        this.allowNether = seconds >= GameConfig.getNetherStartSeconds() && seconds < GameConfig.getNetherEndSeconds();

        this.teams.clear();
        this.teams.addAll(GameConfig.getTeams());

        if (gameTimer == null) {
            gameTimer = new GameTimer(worldManager, this);
            gameTimer.setSeconds(seconds);
        }

        World pugnaWorld = worldManager.getPugnaWorld();
        if (pugnaWorld != null) {
            pugnaWorld.setTime(GameConfig.getGameTime());

            double borderSize;
            if (seconds >= GameConfig.getBorderShrinkEndSeconds()) {
                borderSize = GameConfig.getBorderEndSize();
            } else if (seconds < GameConfig.getBorderShrinkStartSeconds()) {
                borderSize = BorderUtils.calculateBorderSize();
            } else {
                borderSize = GameConfig.getBorderSize();
            }

            WorldBorder worldBorder = pugnaWorld.getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setSize(borderSize);
            }
            GameConfig.saveBorderSize(borderSize);
        }

        gameTimer.start();
    }

    /* === Operations === */

    // GameManager.java
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

            double borderSize = BorderUtils.calculateBorderSize();
            WorldBorder worldBorder = worldManager.getPugnaWorld().getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setSize(borderSize);
            }
            GameConfig.saveBorderSize(borderSize);

            if (gameCountdown == null) {
                gameCountdown = new GameCountdown(this);
            }
            gameCountdown.start();

        } else if (state == GameState.GAME_RUNNING) {
            if (gameTimer == null) {
                gameTimer = new GameTimer(worldManager, this);
                gameTimer.setSeconds(GameConfig.getSeconds());
            }
            gameTimer.start();

            int seconds = GameConfig.getSeconds();
            double borderSize;
            if (seconds >= GameConfig.getBorderShrinkEndSeconds()) {
                borderSize = GameConfig.getBorderEndSize();
            } else if (seconds < GameConfig.getBorderShrinkStartSeconds()) {
                borderSize = BorderUtils.calculateBorderSize();
            } else {
                borderSize = GameConfig.getBorderSize();
            }

            WorldBorder worldBorder = worldManager.getPugnaWorld().getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setSize(borderSize);
            }
            GameConfig.saveBorderSize(borderSize);

        } else if (state == GameState.GAME_PAUSED) {
            int seconds = GameConfig.getSeconds();
            double borderSize;
            if (seconds >= GameConfig.getBorderShrinkEndSeconds()) {
                borderSize = GameConfig.getBorderEndSize();
            } else if (seconds < GameConfig.getBorderShrinkStartSeconds()) {
                borderSize = BorderUtils.calculateBorderSize();
            } else {
                borderSize = GameConfig.getBorderSize();
            }

            WorldBorder worldBorder = worldManager.getPugnaWorld().getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setSize(borderSize);
            }
            GameConfig.saveBorderSize(borderSize);

        } else if (state == GameState.RESTARTING) {
            if (gameTimer != null) {
                gameTimer.cancel();
                gameTimer = null;
            }

            PlayerUtils.clearSpectators();
            restartCountdown = new RestartCountdown();
            restartCountdown.start();
        }

        worldManager.updateWorlds(state);
    }

    public void setAllowNether(boolean allowNether) {
        this.allowNether = allowNether;
    }

    public void handleElimination() {
        if (state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING) {
            for (Team team : teams) {
                if (team.isEmpty() && team.getCapacity() >= 2) {
                    String message = MessageConfig.getChatMessage(Message.TEAM_ELIMINATED).team(team.getName()).toString();
                    ChatUtils.broadcast(message);
                }
            }

            TeamUtils.removeEmptyTeams();
            GameConfig.saveTeams(teams);
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
            message = MessageConfig.getChatMessage(Message.TEAM_WIN).team(winnerTeam.getName()).toString();
        } else {
            Player winner = Bukkit.getPlayer(winnerTeam.getPlayers().get(0));
            if (winner != null && winner.isOnline()) {
                message = MessageConfig.getChatMessage(Message.PLAYER_WIN).player(winner.getName()).toString();
            } else {
                // Fallback
                message = MessageConfig.getChatMessage(Message.TEAM_WIN).team(winnerTeam.getName()).toString();
            }
        }

        if (state == GameState.GAME_COUNTDOWN) {
            gameCountdown.cancel();
        }

        ChatUtils.broadcast(message);
        setState(GameState.RESTARTING);
    }
}
