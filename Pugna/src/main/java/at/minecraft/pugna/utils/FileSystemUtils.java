package at.minecraft.pugna.utils;

import at.minecraft.pugna.config.ChatConfig;
import org.bukkit.Bukkit;

import java.io.File;

public final class FileSystemUtils {
    private FileSystemUtils() {}

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
                Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "FileSystemUtils.deleteDirectory: Could not delete file: " + file.getAbsolutePath() + " (" + exception.getMessage() + ")");
            }
        }

        try {
            if (!directory.delete()) {
                directory.deleteOnExit();
            }
        } catch (Exception exception) {
            Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "FileSystemUtils.deleteDirectory: Could not delete directory: " + directory.getAbsolutePath() + " (" + exception.getMessage() + ")");
        }
    }
}
