package at.minecraft.pugna.config;

import at.minecraft.pugna.Pugna;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GameConfig
{
    private final Pugna plugin;
    private final String fileName = "game.yml";

    private final File configFile;
    private FileConfiguration configuration;

    public GameConfig(final Pugna plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), this.fileName);
    }

    /* === Getters === */

    public FileConfiguration getConfig() {
        if (this.configuration == null) {
            reload();
        }

        return this.configuration;
    }

    /* === Operations === */

    public void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder.");
        }

        if (!configFile.exists()) {
            try {
                plugin.saveResource(this.fileName, false);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().severe("Default resource '" + this.fileName + "' not found in JAR. Did you add it to src/main/resources/?");
            }
        }
    }

    public void save() {
        if (this.configuration == null) {
            return;
        }

        try {
            this.configuration.save(this.configFile);
        } catch (Exception exception) {
            plugin.getLogger().severe("Could not save '" + this.fileName + "': " + exception.getMessage());
        }
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);

        try (InputStream defaultStream = plugin.getResource(this.fileName)) {
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );

                this.configuration.setDefaults(defaultConfig);
                this.configuration.options().copyDefaults(true);
                this.configuration.options().copyHeader(true);
                save();
            } else {
                plugin.getLogger().fine("No default resource found for '" + this.fileName + "'.");
            }
        } catch (Exception exception) {
            plugin.getLogger().severe("Could not reload '" + this.fileName + "': " + exception.getMessage());
        }
    }

    /* === Setup === */

    public void setup() {

    }
}
