package at.minecraft.pugna.countdowns;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartCountdown extends BukkitRunnable {
    private int seconds;

    public RestartCountdown() {
        this.seconds = GameConfig.getRestartCountdownSeconds();
    }

    /* === Operations === */

    public void start() {
        runTaskTimer(Pugna.getInstance(), 0L, 20L);
    }

    @Override
    public void run() {
        if (seconds == 0) {
            String message = ChatConfig.getMessage(Message.KICK_SERVER_RESTARTING);
            PlayerUtils.kickAllPlayers(message);

            Bukkit.getScheduler().runTaskLater(Pugna.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
            }, 10L);

            cancel();
            return;
        }

        if (CountdownUtils.shouldAnnounce(seconds)) {
            String time = CountdownUtils.getTime(seconds);
            String unit = CountdownUtils.getUnit(seconds);
            String message = ChatConfig.getChatMessage(Message.SERVER_RESTART_COUNTDOWN).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
        }

        seconds--;
    }
}
