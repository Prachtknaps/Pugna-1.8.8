package at.minecraft.pugna.commands;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand implements CommandExecutor {
    private final MessageConfig messageConfig;

    public HubCommand(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
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
        String message = messageConfig.getMessage(Message.KICK_HUB_COMMAND);
        player.kickPlayer(message);

        return true;
    }
}
