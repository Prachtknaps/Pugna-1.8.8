package at.minecraft.pugna;

import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Pugna extends JavaPlugin {
    private static Pugna instance;

    private PugnaConfig pugnaConfig;
    private GameConfig gameConfig;
    private MessageConfig messageConfig;

    /* === Getters === */

    public static Pugna getInstance() {
        return instance;
    }

    public PugnaConfig getPugnaConfig() {
        return pugnaConfig;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    /* === Setup === */

    @Override
    public void onEnable() {
        instance = this;

        /* === Configuration === */
        pugnaConfig = new PugnaConfig(this);
        pugnaConfig.setup();

        gameConfig = new GameConfig(this);
        gameConfig.saveDefaultConfig();
        gameConfig.reload();
        gameConfig.setup();

        messageConfig = new MessageConfig(this);
        messageConfig.saveDefaultConfig();
        messageConfig.reload();
        messageConfig.setup();

        /* === Logging === */
        Bukkit.getLogger().info(messageConfig.getRawPrefix() + " The plugin has been enabled.");
    }

    /* === Shutdown === */

    @Override
    public void onDisable() {
        /* === Logging === */
        Bukkit.getLogger().info(messageConfig.getRawPrefix() + "The plugin has been disabled.");
    }
}
