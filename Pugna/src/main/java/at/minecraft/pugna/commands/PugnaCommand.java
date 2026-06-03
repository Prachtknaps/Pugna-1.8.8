package at.minecraft.pugna.commands;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PugnaCommand implements CommandExecutor {
    private final MessageConfig messageConfig;

    public PugnaCommand(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    /* === Operations === */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.isOp()) {
                String message = messageConfig.getMessage(Message.NO_PERMISSION);
                player.sendMessage(message);
                return true;
            }
        }

        sendCommandOverview(sender);
        return true;
    }

    private void sendCommandOverview(CommandSender sender) {
        String prefix = messageConfig.getPrefix();

        sendMessage(sender, prefix + " §6Verfügbare Pugna-Befehle:§r");
        sendMessage(sender, "§7- §e/pugna §8- §fZeigt diese Übersicht an.§r");
        sendMessage(sender, "§7- §e/countdown set <seconds> §8- §fSetzt den aktiven Countdown.§r");
        sendMessage(sender, "§7- §e/gui <enable | disable> §8- §fAktiviert oder deaktiviert die GUI.§r");
        sendMessage(sender, "§7- §e/hub §8- §fVerlässt den Server.§r");
        sendMessage(sender, "§7- §e/rules <items | enchantments | potions> §8- §fZeigt die Regeln an.§r");
        sendMessage(sender, "§7- §e/team <list | join <name> | rename <name> | leave> §8- §fVerwaltet Teams.§r");
    }

    private void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(message);
            return;
        }

        sender.sendMessage(ChatColor.stripColor(message));
    }
}