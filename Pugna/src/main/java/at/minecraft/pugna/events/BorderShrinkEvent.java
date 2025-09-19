package at.minecraft.pugna.events;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.SoundUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class BorderShrinkEvent extends GameEvent {
    private final WorldManager worldManager;

    private final int countdownStartSeconds;
    private final int eventStartSeconds;
    private final int eventEndSeconds;

    private boolean triggered;

    public BorderShrinkEvent(WorldManager worldManager, int countdownStartSeconds, int eventStartSeconds, int eventEndSeconds) {
        this.worldManager = worldManager;

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
            int remainingSeconds = eventStartSeconds - seconds;
            if (CountdownUtils.shouldAnnounce(remainingSeconds)) {
                String time = CountdownUtils.getTime(remainingSeconds);
                String unit = CountdownUtils.getUnit(remainingSeconds);
                String message = MessageConfig.getChatMessage(Message.BORDER_SHRINK_COUNTDOWN)
                        .time(time).unit(unit).toString();
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
            }
            return;
        }

        if (seconds >= eventStartSeconds && seconds < eventEndSeconds && !triggered) {
            World pugnaWorld = worldManager.getPugnaWorld();
            if (pugnaWorld == null) return;

            WorldBorder worldBorder = pugnaWorld.getWorldBorder();
            if (worldBorder == null) return;

            long remainingDuration = Math.max(1, eventEndSeconds - seconds);
            worldBorder.setSize(GameConfig.getBorderEndSize(), remainingDuration);

            String message = MessageConfig.getMessage(Message.BORDER_SHRINK_START);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.WITHER_SPAWN, 1.0f, 1.0f);

            triggered = true;
        }

        if (seconds > eventStartSeconds && seconds < eventEndSeconds && triggered) {
            World pugnaWorld = worldManager.getPugnaWorld();
            if (pugnaWorld == null) return;

            WorldBorder worldBorder = pugnaWorld.getWorldBorder();
            if (worldBorder == null) return;

            GameConfig.saveBorderSize(worldBorder.getSize());
        }

        if (seconds >= eventEndSeconds) {
            World pugnaWorld = worldManager.getPugnaWorld();
            if (pugnaWorld != null) {
                WorldBorder worldBorder = pugnaWorld.getWorldBorder();
                if (worldBorder != null) {
                    worldBorder.setSize(GameConfig.getBorderEndSize());
                }
            }
            GameConfig.saveBorderSize(GameConfig.getBorderEndSize());

            String message = MessageConfig.getMessage(Message.BORDER_SHRINK_END);
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_PLING, 1.0f, 1.0f);
        }
    }
}
