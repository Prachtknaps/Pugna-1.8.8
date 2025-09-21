package at.minecraft.pugna.game.events;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.SoundUtils;
import at.minecraft.pugna.world.border.BorderManager;
import org.bukkit.Sound;

public class BorderShrinkEvent implements GameEvent {
    private final MessageConfig messageConfig;

    private final int countdownStartSeconds;
    private final int eventStartSeconds;
    private final int eventEndSeconds;
    private boolean triggered;

    public BorderShrinkEvent(MessageConfig messageConfig, int countdownStartSeconds, int eventStartSeconds, int eventEndSeconds) {
        this.messageConfig = messageConfig;

        this.countdownStartSeconds = countdownStartSeconds;
        this.eventStartSeconds = eventStartSeconds;
        this.eventEndSeconds = eventEndSeconds;
        this.triggered = false;
    }

    @Override
    public boolean isExpired(int seconds) {
        return seconds > eventEndSeconds;
    }

    @Override
    public void handle(int seconds) {
        if (seconds >= countdownStartSeconds && seconds < eventStartSeconds) {
            int remaining = eventStartSeconds - seconds;
            if (CountdownUtils.shouldAnnounce(remaining)) {
                String time = CountdownUtils.getTime(remaining);
                String unit = CountdownUtils.getUnit(remaining);
                String message = messageConfig.getChatMessage(Message.BORDER_SHRINK_START_COUNTDOWN).time(time).unit(unit).toString();
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
            }
        } else if (seconds >= eventStartSeconds && seconds < eventEndSeconds && !triggered) {
            String message = messageConfig.getMessage(Message.BORDER_SHRINK_START);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.WITHER_SPAWN, 1.0f, 1.0f);

            BorderManager.updateBorder();

            triggered = true;
        } else if (seconds >= eventEndSeconds) {
            if (seconds == eventEndSeconds) {
                String message = messageConfig.getMessage(Message.BORDER_SHRINK_END);
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_PLING, 1.0f, 1.0f);
            }

            BorderManager.updateBorder();
        }
    }
}
