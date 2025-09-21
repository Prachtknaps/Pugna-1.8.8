package at.minecraft.pugna.game;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.countdowns.GameCountdown;
import at.minecraft.pugna.game.countdowns.LobbyCountdown;
import at.minecraft.pugna.game.countdowns.RestartCountdown;
import at.minecraft.pugna.game.timers.GameTimer;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import at.minecraft.pugna.world.WorldManager;
import at.minecraft.pugna.world.border.BorderManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;
    private final GameConfig gameConfig;
    private final WorldManager worldManager;

    private GameState state;
    private final List<Team> teams;

    private LobbyCountdown lobbyCountdown = null;
    private GameCountdown gameCountdown = null;
    @SuppressWarnings("FieldCanBeLocal")
    private RestartCountdown restartCountdown = null;
    private GameTimer gameTimer = null;

    public GameManager(PugnaConfig pugnaConfig, MessageConfig messageConfig, GameConfig gameConfig, WorldManager worldManager) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;
        this.gameConfig = gameConfig;
        this.worldManager = worldManager;

        this.teams = new ArrayList<>();
    }

    /* === Getters === */

    public GameState getState() {
        return state;
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
        setState(GameState.LOBBY_WAITING);

        int maxTeamCapacity = pugnaConfig.getMaxTeamCapacity();
        for (int teamIndex = 0; teamIndex < pugnaConfig.getMaxTeamsCount(); teamIndex++) {
            String defaultName;
            try {
                defaultName = pugnaConfig.getDefaultTeamNames().get(teamIndex);
            } catch (Exception exception) {
                defaultName = "Team" + teamIndex;
            }

            Team team = new Team(teamIndex, maxTeamCapacity);
            team.setName(defaultName);
            team.setSpawns(worldManager.getTeamSpawnLocations().get(teamIndex));
            teams.add(team);
        }
    }

    public void resumeGame() {
        setState(GameState.GAME_PAUSED);

        this.teams.clear();
        this.teams.addAll(gameConfig.getTeams());

        if (gameTimer == null) {
            gameTimer = new GameTimer(pugnaConfig, messageConfig, gameConfig, worldManager, this);
            gameTimer.setSeconds(gameConfig.getSeconds());
        }

        World pugnaWorld = worldManager.getPugnaWorld();
        if (pugnaWorld != null) {
            pugnaWorld.setTime(gameConfig.getTime());
        }

        BorderManager.updateBorder();
        gameTimer.start();
    }

    /* === Operations === */

    public void setState(GameState state) {
        this.state = state;

        if (state == GameState.LOBBY_WAITING) {
            if (lobbyCountdown != null) {
                lobbyCountdown.cancel();
                lobbyCountdown = null;
            }
        } else if (state == GameState.LOBBY_COUNTDOWN) {
            lobbyCountdown = new LobbyCountdown(pugnaConfig, messageConfig, this);
            lobbyCountdown.start();
        } else if (state == GameState.GAME_COUNTDOWN) {
            TeamUtils.assignPlayers();
            TeamUtils.removeEmptyTeams();
            TeamUtils.teleportTeams();

            if (gameCountdown == null) {
                gameCountdown = new GameCountdown(pugnaConfig, messageConfig, gameConfig, this);
            }
            gameCountdown.start();
        } else if (state == GameState.GAME_RUNNING) {
            if (gameTimer == null) {
                gameTimer = new GameTimer(pugnaConfig, messageConfig, gameConfig, worldManager, this);
                gameTimer.setSeconds(gameConfig.getSeconds());
            }
            gameTimer.start();

            World pugnaWorld = worldManager.getPugnaWorld();
            if (pugnaWorld != null) {
                pugnaWorld.setDifficulty(Difficulty.HARD);
                pugnaWorld.setGameRuleValue("doDaylightCycle", "true");
                pugnaWorld.setGameRuleValue("doFireTick", "true");
                pugnaWorld.setGameRuleValue("mobGriefing", "true");
                pugnaWorld.setGameRuleValue("randomTickSpeed", "3");
            }
        } else if (state == GameState.GAME_PAUSED) {
            World pugnaWorld = worldManager.getPugnaWorld();
            if (pugnaWorld != null) {
                pugnaWorld.setDifficulty(Difficulty.HARD);
                pugnaWorld.setGameRuleValue("doDaylightCycle", "false");
                pugnaWorld.setGameRuleValue("doFireTick", "false");
                pugnaWorld.setGameRuleValue("mobGriefing", "false");
                pugnaWorld.setGameRuleValue("randomTickSpeed", "0");
            }
        } else if (state == GameState.RESTARTING) {
            if (gameTimer != null) {
                gameTimer.cancel();
                gameTimer = null;
            }

            PlayerUtils.clearSpectators();
            restartCountdown = new RestartCountdown(pugnaConfig, messageConfig);
            restartCountdown.start();
        }

        BorderManager.updateBorder();
    }

    public void handleElimination() {
        if (state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING) {
            for (Team team : teams) {
                if (team.isEmpty() && team.getCapacity() >= 2) {
                    String message = messageConfig.getChatMessage(Message.TEAM_ELIMINATED).team(team.getName()).toString();
                    ChatUtils.broadcast(message);
                }
            }

            TeamUtils.removeEmptyTeams();
            gameConfig.saveTeams(teams);
            checkForWinner();
        }
    }

    public void checkForWinner() {
        if (teams.size() != 1) {
            return;
        }

        Team winnerTeam = teams.get(0);
        String message;

        if (winnerTeam.getMembers().size() >= 2) {
            message = messageConfig.getChatMessage(Message.TEAM_WIN).team(winnerTeam.getName()).toString();
        } else {
            Player winner = Bukkit.getPlayer(winnerTeam.getMembers().get(0));
            if (winner != null && winner.isOnline()) {
                message = messageConfig.getChatMessage(Message.PLAYER_WIN).player(winner.getName()).toString();
            } else {
                // Fallback
                message = messageConfig.getChatMessage(Message.TEAM_WIN).team(winnerTeam.getName()).toString();
            }
        }

        if (state == GameState.GAME_COUNTDOWN) {
            gameCountdown.cancel();
        }

        ChatUtils.broadcast(message);
        setState(GameState.RESTARTING);
    }
}
