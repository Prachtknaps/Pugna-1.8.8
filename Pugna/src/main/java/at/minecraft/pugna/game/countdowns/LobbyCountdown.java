package at.minecraft.pugna.game.countdowns;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyCountdown extends BukkitRunnable {
    private final MessageConfig messageConfig;
    private final GameManager gameManager;

    private int seconds;

    public LobbyCountdown(PugnaConfig pugnaConfig, MessageConfig messageConfig, GameManager gameManager) {
        this.messageConfig = messageConfig;
        this.gameManager = gameManager;

        this.seconds = pugnaConfig.getLobbyCountdownSeconds();
    }

    /* === Operations === */

    public void setSeconds(int seconds) {
        this.seconds = Math.max(0, seconds);
    }

    public void start() {
        runTaskTimer(Pugna.getInstance(), 0L, 20L);
    }

    @Override
    public void run() {
        if (seconds == 0) {
            gameManager.setState(GameState.GAME_COUNTDOWN);
            cancel();
            return;
        }

        if (CountdownUtils.shouldAnnounce(seconds)) {
            String time = CountdownUtils.getTime(seconds);
            String unit = CountdownUtils.getUnit(seconds);
            String message = messageConfig.getChatMessage(Message.TELEPORT_COUNTDOWN).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
        }

        seconds--;
    }
}
