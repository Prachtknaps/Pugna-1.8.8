package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.MessageConfig;
import org.bukkit.Bukkit;

import java.io.File;

public final class FileSystemUtils {
    private FileSystemUtils() {}

    private static Pugna plugin = null;
    private static MessageConfig messageConfig = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static MessageConfig getMessageConfig() {
        if (messageConfig == null) {
            messageConfig = getPlugin().getMessageConfig();
        }

        return messageConfig;
    }

    /* === Operations === */

    public static void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            try {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
            } catch (Exception exception) {
                Bukkit.getLogger().warning(getMessageConfig().getRawPrefix() + "FileSystemUtils.deleteDirectory: Could not delete file: " + file.getAbsolutePath() + " (" + exception.getMessage() + ")");
            }
        }

        try {
            if (!directory.delete()) {
                directory.deleteOnExit();
            }
        } catch (Exception exception) {
            Bukkit.getLogger().warning(getMessageConfig().getRawPrefix() + "FileSystemUtils.deleteDirectory: Could not delete directory: " + directory.getAbsolutePath() + " (" + exception.getMessage() + ")");
        }
    }
}
