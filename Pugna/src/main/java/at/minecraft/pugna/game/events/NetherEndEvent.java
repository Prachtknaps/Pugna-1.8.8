package at.minecraft.pugna.game.events;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.NetherUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.Sound;

public class NetherEndEvent implements GameEvent {
    private final MessageConfig messageConfig;

    private final int countdownStartSeconds;
    private final int eventSeconds;
    private boolean triggered;

    public NetherEndEvent(MessageConfig messageConfig, int countdownStartSeconds, int eventSeconds) {
        this.messageConfig = messageConfig;

        this.countdownStartSeconds = countdownStartSeconds;
        this.eventSeconds = eventSeconds;
        this.triggered = false;
    }

    @Override
    public String getEventName() {
        return "Nether-Ende";
    }

    @Override
    public int getEventSeconds() {
        return eventSeconds;
    }

    @Override
    public boolean isExpired(int seconds) {
        return triggered && seconds > eventSeconds;
    }

    @Override
    public void handle(int seconds) {
        if (seconds >= countdownStartSeconds && seconds < eventSeconds) {
            int remaining = eventSeconds - seconds;
            if (CountdownUtils.shouldAnnounce(remaining)) {
                String time = CountdownUtils.getTime(remaining);
                String unit = CountdownUtils.getUnit(remaining);
                String message = messageConfig.getChatMessage(Message.NETHER_END_COUNTDOWN).time(time).unit(unit).toString();
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
            }
        } else if (!triggered && seconds >= eventSeconds ) {
            NetherUtils.closeNether();

            String message = messageConfig.getMessage(Message.NETHER_END);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.WITHER_DEATH, 1.0f, 1.0f);

            triggered = true;
        }
    }
}
