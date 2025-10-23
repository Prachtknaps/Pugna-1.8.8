package at.minecraft.pugna.commands;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.game.countdowns.GameCountdown;
import at.minecraft.pugna.game.countdowns.LobbyCountdown;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CountdownCommand implements CommandExecutor {
    private final MessageConfig messageConfig;
    private final GameManager gameManager;

    public CountdownCommand(MessageConfig messageConfig, GameManager gameManager) {
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
        if (arguments.length != 2 || !arguments[0].equalsIgnoreCase("set")) {
            String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
            String usageMessage = messageConfig.getMessage(Message.COUNTDOWN_USAGE);
            sender.sendMessage(ChatColor.stripColor(invalidUsageMessage));
            sender.sendMessage(ChatColor.stripColor(usageMessage));
            return;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(arguments[1]);
            if (seconds < 0) {
                seconds = 0;
            }
        } catch (NumberFormatException exception) {
            String message = messageConfig.getMessage(Message.INVALID_NUMBER);
            sender.sendMessage(ChatColor.stripColor(message));
            return;
        }

        GameState state = gameManager.getState();
        if (state == GameState.LOBBY_COUNTDOWN) {
            LobbyCountdown lobbyCountdown = gameManager.getLobbyCountdown();
            if (lobbyCountdown == null) {
                String message = messageConfig.getMessage(Message.COUNTDOWN_NOT_ACTIVE);
                sender.sendMessage(ChatColor.stripColor(message));
                return;
            }

            lobbyCountdown.setSeconds(seconds);
            String time = CountdownUtils.getTime(seconds);
            String unit = CountdownUtils.getUnit(seconds);
            String message = messageConfig.getChatMessage(Message.COUNTDOWN_UPDATE_SUCCESS).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            return;
        }

        if (state == GameState.GAME_COUNTDOWN) {
            GameCountdown gameCountdown = gameManager.getGameCountdown();
            if (gameCountdown == null) {
                String message = messageConfig.getMessage(Message.COUNTDOWN_NOT_ACTIVE);
                sender.sendMessage(ChatColor.stripColor(message));
                return;
            }

            gameCountdown.setSeconds(seconds);
            String time = CountdownUtils.getTime(seconds);
            String unit = CountdownUtils.getUnit(seconds);
            String message = messageConfig.getChatMessage(Message.COUNTDOWN_UPDATE_SUCCESS).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            return;
        }

        String message = messageConfig.getMessage(Message.COUNTDOWN_NOT_ACTIVE);
        sender.sendMessage(ChatColor.stripColor(message));
    }

    private void execute(Player player, String[] arguments) {
        if (!player.isOp()) {
            String message = messageConfig.getMessage(Message.NO_PERMISSION);
            player.sendMessage(message);
            return;
        }

        if (arguments.length != 2 || !arguments[0].equalsIgnoreCase("set")) {
            String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
            String usageMessage = messageConfig.getMessage(Message.COUNTDOWN_USAGE);
            player.sendMessage(invalidUsageMessage);
            player.sendMessage(usageMessage);
            return;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(arguments[1]);
            if (seconds < 0) {
                seconds = 0;
            }
        } catch (NumberFormatException exception) {
            String message = messageConfig.getMessage(Message.INVALID_NUMBER);
            player.sendMessage(message);
            return;
        }

        GameState state = gameManager.getState();
        if (state == GameState.LOBBY_COUNTDOWN) {
            LobbyCountdown lobbyCountdown = gameManager.getLobbyCountdown();
            if (lobbyCountdown == null) {
                String message = messageConfig.getMessage(Message.COUNTDOWN_NOT_ACTIVE);
                player.sendMessage(message);
                return;
            }

            lobbyCountdown.setSeconds(seconds);
            String time = CountdownUtils.getTime(seconds);
            String unit = CountdownUtils.getUnit(seconds);
            String message = messageConfig.getChatMessage(Message.COUNTDOWN_UPDATE_SUCCESS).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            return;
        }

        if (state == GameState.GAME_COUNTDOWN) {
            GameCountdown gameCountdown = gameManager.getGameCountdown();
            if (gameCountdown == null) {
                String message = messageConfig.getMessage(Message.COUNTDOWN_NOT_ACTIVE);
                player.sendMessage(message);
                return;
            }

            gameCountdown.setSeconds(seconds);
            String time = CountdownUtils.getTime(seconds);
            String unit = CountdownUtils.getUnit(seconds);
            String message = messageConfig.getChatMessage(Message.COUNTDOWN_UPDATE_SUCCESS).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            return;
        }

        String message = messageConfig.getMessage(Message.COUNTDOWN_NOT_ACTIVE);
        player.sendMessage(message);
    }
}
