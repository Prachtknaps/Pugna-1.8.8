package at.minecraft.pugna.commands;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player)) {
            String message = ChatConfig.getMessage(Message.PLAYER_ONLY_COMMAND);
            sender.sendMessage(ChatColor.stripColor(message));
            return true;
        }

        Player player = (Player) sender;
        player.kickPlayer("");
        return true;
    }
}
