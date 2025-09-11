package at.minecraft.pugna.countdowns;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdown extends BukkitRunnable {
    private final GameManager gameManager;
    private int seconds;

    public GameCountdown(GameManager gameManager) {
        this.gameManager = gameManager;
        this.seconds = GameConfig.getGameCountdownSeconds();
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
            gameManager.setState(GameState.GAME_RUNNING);
            String message = ChatConfig.getMessage(Message.GAME_START);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.WITHER_SPAWN, 1.0f, 1.0f);
            cancel();
            return;
        }

        if (CountdownUtils.shouldAnnounce(seconds)) {
            String time = CountdownUtils.getTime(seconds);
            String unit = CountdownUtils.getUnit(seconds);
            String message = ChatConfig.getChatMessage(Message.GAME_START_COUNTDOWN).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
        }

        seconds--;
    }
}
