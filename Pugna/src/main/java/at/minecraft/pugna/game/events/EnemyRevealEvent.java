package at.minecraft.pugna.game.events;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.utils.CountdownUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EnemyRevealEvent implements GameEvent {
    private final MessageConfig messageConfig;

    private final int countdownStartSeconds;
    private final int eventSeconds;
    private boolean triggered;

    public EnemyRevealEvent(MessageConfig messageConfig, int countdownStartSeconds, int eventSeconds) {
        this.messageConfig = messageConfig;

        this.countdownStartSeconds = countdownStartSeconds;
        this.eventSeconds = eventSeconds;
        this.triggered = false;
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
                String message = messageConfig.getChatMessage(Message.ENEMY_REVEAL_COUNTDOWN).time(time).unit(unit).toString();
                ChatUtils.broadcast(message);
                SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
            }
        } else if (!triggered && seconds >= eventSeconds) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player == null || !player.isOnline() || PlayerUtils.isSpectator(player)) {
                    continue;
                }

                Player closestEnemy = PlayerUtils.getClosestEnemy(player);
                if (closestEnemy != null) {
                    Location location = closestEnemy.getLocation();
                    double distance = player.getLocation().distanceSquared(location);
                    int meters = (int) Math.round(Math.sqrt(distance));
                    String message = messageConfig.getChatMessage(Message.ENEMY_REVEAL_SUCCESS).player(closestEnemy.getName()).meters(meters).x(location.getBlockX()).y(location.getBlockY()).z(location.getBlockZ()).toString();
                    player.sendMessage(message);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 2.0f);
                } else {
                    String message = messageConfig.getMessage(Message.ENEMY_REVEAL_NO_ENEMY_FOUND);
                    player.sendMessage(message);
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0f, 1.0f);
                }
            }

            triggered = true;
        }
    }
}
