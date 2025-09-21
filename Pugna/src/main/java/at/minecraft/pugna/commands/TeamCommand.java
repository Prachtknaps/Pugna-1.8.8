package at.minecraft.pugna.commands;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {
    private final MessageConfig messageConfig;
    private final GameManager gameManager;

    public TeamCommand(MessageConfig messageConfig, GameManager gameManager) {
        this.messageConfig = messageConfig;
        this.gameManager = gameManager;
    }

    /* === Operations === */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player)) {
            execute(sender, arguments);
        } else {
            Player player = (Player) sender;
            execute(player, arguments);
        }

        return true;
    }

    private void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
            String usageMessage = messageConfig.getMessage(Message.TEAM_USAGE);
            sender.sendMessage(ChatColor.stripColor(invalidUsageMessage));
            sender.sendMessage(ChatColor.stripColor(usageMessage));
            return;
        }

        String subCommand = arguments[0].toLowerCase();
        switch (subCommand) {
            case "list":
                for (String message : getListMessages()) {
                    sender.sendMessage(ChatColor.stripColor(message));
                }
                break;
            case "join":
            case "rename":
            case "leave":
                String message = messageConfig.getMessage(Message.PLAYERS_ONLY_COMMAND);
                sender.sendMessage(ChatColor.stripColor(message));
                return;
            default:
                String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
                String usageMessage = messageConfig.getMessage(Message.TEAM_USAGE);
                sender.sendMessage(ChatColor.stripColor(invalidUsageMessage));
                sender.sendMessage(ChatColor.stripColor(usageMessage));
        }
    }

    private void execute(Player player, String[] arguments) {
        if (arguments.length == 0) {
            String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
            String usageMessage = messageConfig.getMessage(Message.TEAM_USAGE);
            player.sendMessage(invalidUsageMessage);
            player.sendMessage(usageMessage);
            return;
        }

        String subCommand = arguments[0].toLowerCase();
        switch (subCommand) {
            case "list":
                for (String message : getListMessages()) {
                    player.sendMessage(message);
                }
                return;
            case "join":
                if (arguments.length < 2) {
                    String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
                    String usageMessage = messageConfig.getMessage(Message.TEAM_USAGE);
                    player.sendMessage(invalidUsageMessage);
                    player.sendMessage(usageMessage);
                }
                handleTeamJoin(player, arguments[1]);
                return;
            case "rename":
                if (arguments.length < 2) {
                    String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
                    String usageMessage = messageConfig.getMessage(Message.TEAM_USAGE);
                    player.sendMessage(invalidUsageMessage);
                    player.sendMessage(usageMessage);
                }
                handleRename(player, arguments[1]);
                return;
            case "leave":
                handleLeave(player);
                return;
            default:
                String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
                String usageMessage = messageConfig.getMessage(Message.TEAM_USAGE);
                player.sendMessage(invalidUsageMessage);
                player.sendMessage(usageMessage);
        }
    }

    private List<String> getListMessages() {
        List<String> messages = new ArrayList<>();

        if (gameManager.getTeams().isEmpty()) {
            String message = messageConfig.getMessage(Message.TEAM_LIST_EMPTY);
            messages.add(message);
            return messages;
        }

        String header = messageConfig.getMessage(Message.TEAM_LIST_HEADER);
        messages.add(header);

        for (Team team : gameManager.getTeams()) {
            List<Player> members = new ArrayList<>();
            for (UUID uuid : team.getMembers()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) {
                    members.add(member);
                }
            }

            String message = messageConfig.getChatMessage(Message.TEAM_LIST_ENTRY).team(team.getName()).players(members).toString();
            messages.add(message);
        }

        return messages;
    }

    private void handleTeamJoin(Player player, String name) {
        GameState state = gameManager.getState();
        if ((state != GameState.LOBBY_WAITING && state != GameState.LOBBY_COUNTDOWN) || PlayerUtils.isSpectator(player)) {
            String message = messageConfig.getMessage(Message.TEAM_JOIN_NOT_ALLOWED);
            player.sendMessage(message);
            return;
        }

        Team targetTeam = TeamUtils.getTeam(name);
        if (targetTeam == null) {
            String message = messageConfig.getChatMessage(Message.TEAM_JOIN_NOT_FOUND).team(name).toString();
            player.sendMessage(message);
            return;
        }

        Team currentTeam = TeamUtils.getTeam(player);
        if (currentTeam != null) {
            if (currentTeam.getId() == targetTeam.getId()) {
                String message = messageConfig.getMessage(Message.TEAM_JOIN_SAME_TEAM);
                player.sendMessage(message);
            } else {
                if (!targetTeam.isFull()) {
                    currentTeam.leave(player);
                }
                targetTeam.join(player);
            }
        } else {
            targetTeam.join(player);
        }
    }

    private void handleRename(Player player, String name) {
        Team team = TeamUtils.getTeam(player);
        if (team == null) {
            String message = messageConfig.getMessage(Message.NO_TEAM);
            player.sendMessage(message);
            return;
        }

        GameState state = gameManager.getState();
        if (state != GameState.LOBBY_WAITING && state != GameState.LOBBY_COUNTDOWN) {
            String message = messageConfig.getMessage(Message.TEAM_RENAME_NOT_ALLOWED);
            player.sendMessage(message);
            return;
        }

        Team existingTeam = TeamUtils.getTeam(name);
        if (existingTeam != null) {
            String message;
            if (existingTeam.getId() != team.getId()) {
                message = messageConfig.getMessage(Message.TEAM_RENAME_TAKEN);
            } else {
                message = messageConfig.getChatMessage(Message.TEAM_RENAME_SAME_NAME).team(team.getName()).toString();
            }
            player.sendMessage(message);
        } else {
            team.rename(player, name);
        }
    }

    private void handleLeave(Player player) {
        Team team = TeamUtils.getTeam(player);
        if (team == null) {
            String message = messageConfig.getMessage(Message.NO_TEAM);
            player.sendMessage(message);
            return;
        }

        GameState state = gameManager.getState();
        if (state != GameState.LOBBY_WAITING && state != GameState.LOBBY_COUNTDOWN) {
            String message = messageConfig.getMessage(Message.TEAM_LEAVE_NOT_ALLOWED);
            player.sendMessage(message);
            return;
        }

        team.leave(player);
    }
}
