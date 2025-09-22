package at.minecraft.pugna.commands;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {
    private final MessageConfig messageConfig;

    public RulesCommand(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    /* === Operations === */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player)) {
            execute(sender, arguments);
        } else {
            execute((Player) sender, arguments);
        }

        return true;
    }

    private void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
            String usageMessage = messageConfig.getMessage(Message.RULES_USAGE);
            sender.sendMessage(ChatColor.stripColor(invalidUsageMessage));
            sender.sendMessage(ChatColor.stripColor(usageMessage));
            return;
        }

        String subCommand = arguments[0].toLowerCase();
        switch (subCommand) {
            case "items":
                for (String message : messageConfig.getRuleMessages(Message.RULES_ITEMS)) {
                    sender.sendMessage(ChatColor.stripColor(message));
                }
                return;
            case "enchantments":
                for (String message : messageConfig.getRuleMessages(Message.RULES_ENCHANTMENTS)) {
                    sender.sendMessage(ChatColor.stripColor(message));
                }
                return;
            case "potions":
                for (String message : messageConfig.getRuleMessages(Message.RULES_POTIONS)) {
                    sender.sendMessage(ChatColor.stripColor(message));
                }
                return;
            default:
                String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
                String usageMessage = messageConfig.getMessage(Message.RULES_USAGE);
                sender.sendMessage(ChatColor.stripColor(invalidUsageMessage));
                sender.sendMessage(ChatColor.stripColor(usageMessage));
        }
    }

    private void execute(Player player, String[] arguments) {
        if (arguments.length == 0) {
            String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
            String usageMessage = messageConfig.getMessage(Message.RULES_USAGE);
            player.sendMessage(invalidUsageMessage);
            player.sendMessage(usageMessage);
            return;
        }

        String subCommand = arguments[0].toLowerCase();
        switch (subCommand) {
            case "items":
                for (String message : messageConfig.getRuleMessages(Message.RULES_ITEMS)) {
                    player.sendMessage(message);
                }
                return;
            case "enchantments":
                for (String message : messageConfig.getRuleMessages(Message.RULES_ENCHANTMENTS)) {
                    player.sendMessage(message);
                }
                return;
            case "potions":
                for (String message : messageConfig.getRuleMessages(Message.RULES_POTIONS)) {
                    player.sendMessage(message);
                }
                return;
            default:
                String invalidUsageMessage = messageConfig.getMessage(Message.INVALID_USAGE);
                String usageMessage = messageConfig.getMessage(Message.RULES_USAGE);
                player.sendMessage(invalidUsageMessage);
                player.sendMessage(usageMessage);
        }
    }
}
