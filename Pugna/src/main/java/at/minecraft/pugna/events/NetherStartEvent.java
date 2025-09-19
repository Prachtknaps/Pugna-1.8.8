package at.minecraft.pugna.events;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.Sound;

public class NetherStartEvent extends GameEvent {
    private final GameManager gameManager;

    private final int countdownStartSeconds;
    private final int eventSeconds;
    private final int eventThresholdSeconds;

    private boolean triggered;

    public NetherStartEvent(GameManager gameManager, int countdownStartSeconds, int eventSeconds) {
        this.gameManager = gameManager;

        this.countdownStartSeconds = countdownStartSeconds;
        this.eventSeconds = eventSeconds;
        this.eventThresholdSeconds = eventSeconds + 10;

        this.triggered = false;
    }

    @Override
    public boolean isExpired(int seconds) {
        return seconds > eventSeconds;
    }

    @Override
    public void handle(int seconds) {
        if (seconds >= countdownStartSeconds && seconds < eventSeconds) {
            int remaining = eventSeconds - seconds;
            if (CountdownUtils.shouldAnnounce(remaining)) {
                String time = CountdownUtils.getTime(remaining);
                String unit = CountdownUtils.getUnit(remaining);
                String message = MessageConfig.getChatMessage(Message.NETHER_START_COUNTDOWN).time(time).unit(unit).toString();
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
            }
        } else if (seconds >= eventSeconds && seconds <= eventThresholdSeconds && !triggered) {
            gameManager.setAllowNether(true);
            triggered = true;

            String message = MessageConfig.getMessage(Message.NETHER_START);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_PLING, 1.0f, 1.0f);
        }
    }
}
