package at.minecraft.pugna.game.events;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.Sound;

public class NetherStartEvent implements GameEvent {
    private final MessageConfig messageConfig;

    private final int countdownStartSeconds;
    private final int eventSeconds;

    public NetherStartEvent(MessageConfig messageConfig, int countdownStartSeconds, int eventSeconds) {
        this.messageConfig = messageConfig;

        this.countdownStartSeconds = countdownStartSeconds;
        this.eventSeconds = eventSeconds;
    }

    @Override
    public String getEventName() {
        return "Nether-Start";
    }

    @Override
    public int getEventSeconds() {
        return eventSeconds;
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
                String message = messageConfig.getChatMessage(Message.NETHER_START_COUNTDOWN).time(time).unit(unit).toString();
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
            }
        } else if (seconds == eventSeconds) {
            String message = messageConfig.getMessage(Message.NETHER_START);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_PLING, 1.0f, 1.0f);
        }
    }
}
