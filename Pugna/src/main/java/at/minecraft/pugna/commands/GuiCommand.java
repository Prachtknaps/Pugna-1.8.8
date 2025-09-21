package at.minecraft.pugna.commands;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.timers.GameTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand implements CommandExecutor {
    private final MessageConfig messageConfig;
    private final GameManager gameManager;

    public GuiCommand(MessageConfig messageConfig, GameManager gameManager) {
        this.messageConfig = messageConfig;
        this.gameManager = gameManager;
    }

    /* === Operations === */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player)) {
            String message = messageConfig.getMessage(Message.PLAYERS_ONLY_COMMAND);
            sender.sendMessage(ChatColor.stripColor(message));
            return true;
        }

        Player player = (Player) sender;

        if (arguments.length != 1) {
            String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
            String usageMessage = messageConfig.getMessage(Message.GUI_USAGE);
            player.sendMessage(invalidUsageMessage);
            player.sendMessage(usageMessage);
            return true;
        }

        String subCommand = arguments[0].toLowerCase();
        GameTimer gameTimer = gameManager.getGameTimer();
        switch (subCommand) {
            case "enable":
                if (gameTimer != null) {
                    gameTimer.enableGUIFor(player);
                    String message = messageConfig.getMessage(Message.GUI_ENABLED);
                    player.sendMessage(message);
                }
                return true;
            case "disable":
                if (gameTimer != null) {
                    gameTimer.disableGUIFor(player);
                    String message = messageConfig.getMessage(Message.GUI_DISABLED);
                    player.sendMessage(message);
                }
                return true;
            default:
                String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
                String usageMessage = messageConfig.getMessage(Message.GUI_USAGE);
                player.sendMessage(invalidUsageMessage);
                player.sendMessage(usageMessage);
                return true;
        }
    }
}
