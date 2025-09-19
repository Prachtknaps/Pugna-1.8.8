package at.minecraft.pugna;

import at.minecraft.pugna.commands.CountdownCommand;
import at.minecraft.pugna.commands.GuiCommand;
import at.minecraft.pugna.commands.HubCommand;
import at.minecraft.pugna.commands.TeamCommand;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.listeners.*;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Pugna extends JavaPlugin {
    private static Pugna instance;

    private WorldManager worldManager;
    private GameManager gameManager;

    /* === Queries === */

    public static Pugna getInstance() {
        return instance;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    /* === Setup === */

    @Override
    public void onEnable() {
        instance = this;

        /* === Configuration === */
        saveDefaultConfig();
        GameConfig.setup();
        MessageConfig.setup();

        /* === Managers === */
        worldManager = new WorldManager();
        if (GameConfig.isDevelopment()) {
            worldManager.setup();
        } else {
            if (!GameConfig.isRunning()) {
                worldManager.setup();
            }
        }
        gameManager = new GameManager(worldManager);
        if (!GameConfig.isDevelopment()) {
            if (!GameConfig.isRunning()) {
                gameManager.prepareNewGame();
            } else {
                gameManager.resumeGame();
            }
        }

        /* === Listeners === */
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new BlockListener(gameManager), this);
        pluginManager.registerEvents(new ChatListener(gameManager), this);
        pluginManager.registerEvents(new ConnectionListener(worldManager, gameManager), this);
        pluginManager.registerEvents(new DamageListener(worldManager, gameManager), this);
        pluginManager.registerEvents(new InteractionListener(gameManager), this);
        pluginManager.registerEvents(new InventoryListener(gameManager), this);
        pluginManager.registerEvents(new PortalListener(worldManager, gameManager), this);
        pluginManager.registerEvents(new WorldListener(gameManager), this);

        /* === Commands === */
        getCommand("countdown").setExecutor(new CountdownCommand(gameManager));
        getCommand("gui").setExecutor(new GuiCommand(gameManager));
        getCommand("hub").setExecutor(new HubCommand());
        getCommand("team").setExecutor(new TeamCommand(gameManager));

        /* === Logging === */
        getLogger().info(MessageConfig.getRawPrefix() + "The plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        /* === Clean Up === */
        if (GameConfig.isDevelopment() || gameManager.getState() == GameState.RESTARTING) {
            worldManager.deleteWorlds();
            GameConfig.reset();
        }

        /* === Logging === */
        getLogger().info(MessageConfig.getRawPrefix() + "The plugin has been disabled.");
    }
}
