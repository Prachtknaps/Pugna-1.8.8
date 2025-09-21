package at.minecraft.pugna.listeners;

import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final PugnaConfig pugnaConfig;
    private final GameManager gameManager;

    public ChatListener(PugnaConfig pugnaConfig, GameManager gameManager) {
        this.pugnaConfig = pugnaConfig;
        this.gameManager = gameManager;
    }

    /* === Events === */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        GameState state = gameManager.getState();
        Player sender = event.getPlayer();
        String rawMessage = event.getMessage();

        if (PlayerUtils.isSpectator(sender)) {
            String message = ChatUtils.formatSpectatorMessage(sender, rawMessage);
            ChatUtils.sendSpectatorMessage(message);
            return;
        }

        if (state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            if (pugnaConfig.getMaxTeamCapacity() >= 2) {
                boolean isGlobal = rawMessage.matches("(?i)^@(a|all)\\s+.*");
                if (isGlobal) {
                    String trimmed = rawMessage.replaceFirst("(?i)^@(a|all)\\s+", "").trim();
                    String message = ChatUtils.formatGlobalMessage(sender, trimmed);
                    ChatUtils.sendGlobalMessage(message);
                } else {
                    String message = ChatUtils.formatMessage(sender, rawMessage);
                    ChatUtils.sendTeamMessage(sender, message);
                }
            } else {
                String message = ChatUtils.formatMessage(sender, rawMessage);
                ChatUtils.sendGlobalMessage(message);
            }
            return;
        }

        String message = ChatUtils.formatMessage(sender, rawMessage);
        ChatUtils.sendGlobalMessage(message);
    }
}
