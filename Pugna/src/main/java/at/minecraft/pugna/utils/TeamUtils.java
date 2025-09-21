package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class TeamUtils {
    private TeamUtils() {}

    private static Pugna plugin = null;
    private static MessageConfig messageConfig = null;
    private static GameManager gameManager = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static MessageConfig getMessageConfig() {
        if (messageConfig == null) {
            messageConfig = getPlugin().getMessageConfig();
        }

        return messageConfig;
    }

    private static GameManager getGameManager() {
        if (gameManager == null) {
            gameManager = getPlugin().getGameManager();
        }

        return gameManager;
    }

    /* === Queries === */

    public static Team getTeam(Player player) {
        if (player == null) {
            return null;
        }

        for (Team team : getGameManager().getTeams()) {
            if (team.hasPlayer(player)) {
                return team;
            }
        }

        return null;
    }

    public static Team getTeam(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        for (Team team : getGameManager().getTeams()) {
            if (team.getName().equalsIgnoreCase(ChatColor.stripColor(name).trim())) {
                return team;
            }
        }

        return null;
    }

    /* === Operations === */

    public static void assignPlayers() {
        List<Player> playersToAssign = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getTeam(player) == null) {
                playersToAssign.add(player);
            }
        }

        List<Team> incompleteTeams = new ArrayList<>();
        List<Team> emptyTeams = new ArrayList<>();
        for (Team team : getGameManager().getTeams()) {
            if (team.isFull()) {
                continue;
            }

            if (team.isEmpty()) {
                emptyTeams.add(team);
            } else {
                incompleteTeams.add(team);
            }
        }

        for (Player player : playersToAssign) {
            if (!incompleteTeams.isEmpty()) {
                Team team = incompleteTeams.remove(0);
                team.add(player);
                if (!team.isFull()) {
                    incompleteTeams.add(team);
                }
                continue;
            }

            if (!emptyTeams.isEmpty()) {
                Team team = emptyTeams.remove(0);
                team.add(player);
                if (!team.isFull()) {
                    incompleteTeams.add(team);
                }
                continue;
            }

            player.kickPlayer(getMessageConfig().getMessage(Message.TEAM_NOT_ASSIGNED));
        }
    }

    public static void removeEmptyTeams() {
        getGameManager().getTeams().removeIf(Team::isEmpty);
    }

    public static void teleportTeams() {
        for (Team team : getGameManager().getTeams()) {
            team.teleport();
        }
    }
}
