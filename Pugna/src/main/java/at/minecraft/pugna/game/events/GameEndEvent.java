package at.minecraft.pugna.game.events;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.SoundUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameEndEvent implements GameEvent {
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;
    private final WorldManager worldManager;
    private final GameManager gameManager;

    private final int countdownStartSeconds;
    private final int eventSeconds;
    private boolean triggered;

    public GameEndEvent(PugnaConfig pugnaConfig, MessageConfig messageConfig, WorldManager worldManager, GameManager gameManager, int countdownStartSeconds, int eventSeconds) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;
        this.worldManager = worldManager;
        this.gameManager = gameManager;

        this.countdownStartSeconds = countdownStartSeconds;
        this.eventSeconds = eventSeconds;
        this.triggered = false;
    }

    @Override
    public String getEventName() {
        return "Spielende";
    }

    @Override
    public int getEventSeconds() {
        return eventSeconds;
    }

    @Override
    public boolean isExpired(int seconds) {
        return triggered && seconds > eventSeconds;
    }

    @Override
    public void handle(int seconds) {
        if (seconds >= countdownStartSeconds && seconds < eventSeconds) {
            int remaining = eventSeconds - seconds;
            if (CountdownUtils.shouldAnnounce(remaining)) {
                String time = CountdownUtils.getTime(remaining);
                String unit = CountdownUtils.getUnit(remaining);
                String message = messageConfig.getChatMessage(Message.GAME_END_COUNTDOWN).time(time).unit(unit).toString();
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
                if (seconds == countdownStartSeconds) {
                    String explanation = messageConfig.getMessage(Message.GAME_END_EXPLANATION);
                    ChatUtils.broadcast(explanation);
                }
            }
        } else if (!triggered && seconds >= eventSeconds) {
            Location center = worldManager.getPugnaSpawnLocation();
            if (center == null) {
                gameManager.setState(GameState.RESTARTING);
                return;
            }

            Team winner = null;
            double smallestTeamDistance = Double.MAX_VALUE;

            for (Team team : gameManager.getTeams()) {
                double smallestMemberDistance = Double.MAX_VALUE;
                for (UUID uuid : team.getMembers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline() || PlayerUtils.isSpectator(player) || !player.getWorld().getName().equals(pugnaConfig.getPugnaWorldName())) {
                        continue;
                    }

                    double distance = center.distanceSquared(player.getLocation());
                    if (distance < smallestMemberDistance) {
                        smallestMemberDistance = distance;
                    }
                }

                if (smallestMemberDistance < smallestTeamDistance) {
                    smallestTeamDistance = smallestMemberDistance;
                    winner = team;
                } else if (smallestMemberDistance == smallestTeamDistance && winner != null && team.getId() < winner.getId()) {
                    winner = team;
                }
            }

            if (winner != null) {
                Team finalWinner = winner;
                gameManager.getTeams().removeIf(team -> team != finalWinner);
                gameManager.handleElimination();
            } else {
                String message = messageConfig.getMessage(Message.GAME_END_NO_WINNER);
                ChatUtils.broadcast(message);
            }

            triggered = true;
        }
    }
}
