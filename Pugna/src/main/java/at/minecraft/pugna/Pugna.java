package at.minecraft.pugna;

import at.minecraft.pugna.commands.CountdownCommand;
import at.minecraft.pugna.commands.HubCommand;
import at.minecraft.pugna.commands.TeamCommand;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.listeners.*;
import at.minecraft.pugna.utils.ChatUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Pugna extends JavaPlugin {
    private static Pugna instance;

    private WorldManager worldManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;

        /* === Configuration === */
        // TODO: saveDefaultConfig();
        FileConfiguration configuration = getConfig();
        GameConfig.setup(configuration);
        ChatConfig.setup(configuration);

        /* === Managers === */
        worldManager = new WorldManager();
        gameManager = new GameManager(worldManager);

        /* === Listeners === */
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new BlockListener(gameManager), this);
        pluginManager.registerEvents(new ChatListener(gameManager), this);
        pluginManager.registerEvents(new ConnectionListener(worldManager, gameManager), this);
        pluginManager.registerEvents(new DamageListener(worldManager, gameManager), this);
        pluginManager.registerEvents(new InteractionListener(gameManager), this);
        pluginManager.registerEvents(new InventoryListener(gameManager), this);
        pluginManager.registerEvents(new PortalListener(), this);
        pluginManager.registerEvents(new WorldListener(gameManager), this);

        /* === Commands === */
        getCommand("countdown").setExecutor(new CountdownCommand());
        getCommand("hub").setExecutor(new HubCommand());
        getCommand("team").setExecutor(new TeamCommand());

        /* === Logging === */
        Bukkit.getLogger().info(ChatConfig.getRawPrefix() + "The plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        /* === Clean up === */
        worldManager.cleanUp();

        /* === Logging === */
        Bukkit.getLogger().info(ChatConfig.getRawPrefix() + "[The plugin has been disabled.");
    }

    /* === Getters === */

    public static Pugna getInstance() {
        return instance;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
