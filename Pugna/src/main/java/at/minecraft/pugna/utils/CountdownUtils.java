package at.minecraft.pugna.utils;

public final class CountdownUtils {
    private CountdownUtils() {}

    /* === Operations === */

    public static String getTime(int totalSeconds) {
        if (totalSeconds < 60) {
            return Integer.toString(totalSeconds);
        } else if (totalSeconds < 3600) {
            int minutes = totalSeconds / 60;
            return Integer.toString(minutes);
        } else {
            int hours = totalSeconds / 3600;
            return Integer.toString(hours);
        }
    }

    public static String getUnit(int totalSeconds) {
        if (totalSeconds < 60) {
            return (totalSeconds == 1) ? "Sekunde" : "Sekunden";
        } else if (totalSeconds < 3600) {
            int minutes = totalSeconds / 60;
            return (minutes == 1) ? "Minute" : "Minuten";
        } else {
            int hours = totalSeconds / 3600;
            return (hours == 1) ? "Stunde" : "Stunden";
        }
    }

    public static boolean shouldAnnounce(int seconds) {
        if (seconds == 0) {
            return false;
        } else if (seconds <= 5) {
            return true;
        } else if (seconds == 10 || seconds == 20 || seconds == 30 || seconds == 45) {
            return true;
        } else if (seconds % 60 == 0) {
            int minutes = seconds / 60;
            return minutes == 1 || minutes == 2 || minutes == 5 || minutes == 10 || minutes == 15 || minutes == 30 || minutes == 45 || minutes == 60 || minutes == 120;
        }

        return false;
    }
}
