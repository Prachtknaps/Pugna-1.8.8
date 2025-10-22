package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class PlaytimeUtils {
    private PlaytimeUtils() {}

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static Pugna plugin = null;
    private static PugnaConfig pugnaConfig = null;
    private static MessageConfig messageConfig = null;
    private static String playtimeKickMessage = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static PugnaConfig getPugnaConfig() {
        if (pugnaConfig == null) {
            pugnaConfig = getPlugin().getPugnaConfig();
        }

        return pugnaConfig;
    }

    private static MessageConfig getMessageConfig() {
        if (messageConfig == null) {
            messageConfig = getPlugin().getMessageConfig();
        }

        return messageConfig;
    }

    /* === Queries === */

    public static boolean isWithinPlaytimeWindow() {
        final ZonedDateTime now = ZonedDateTime.now(getPugnaConfig().getPlaytimeZoneId());
        final LocalTime currentTime = now.toLocalTime();

        final LocalTime startTime = getPugnaConfig().getPlaytimeStart();
        final LocalTime endTime = getPugnaConfig().getPlaytimeEnd();

        if (startTime.equals(endTime)) {
            return true;
        }

        if (startTime.isBefore(endTime)) {
            return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime);
        } else {
            return !currentTime.isBefore(startTime) || currentTime.isBefore(endTime);
        }
    }

    public static String getPlaytimeKickMessage() {
        if (playtimeKickMessage == null) {
            final String formattedStart = getPugnaConfig().getPlaytimeStart().format(DISPLAY_FORMATTER);
            final String formattedEnd = getPugnaConfig().getPlaytimeEnd().format(DISPLAY_FORMATTER);

            playtimeKickMessage = getMessageConfig().getMessage(Message.KICK_PLAYTIME_WINDOW).replace("{start}", formattedStart).replace("{end}", formattedEnd);
        }

        return playtimeKickMessage;
    }

   /* === Helpers === */

   public static void invalidateCache() {
       playtimeKickMessage = null;
   }
}
