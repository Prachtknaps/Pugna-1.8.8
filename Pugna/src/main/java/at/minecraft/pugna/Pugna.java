package at.minecraft.pugna;

import at.minecraft.pugna.commands.*;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.listeners.*;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Pugna extends JavaPlugin {
    private static Pugna instance;

    private PugnaConfig pugnaConfig;
    private GameConfig gameConfig;
    private MessageConfig messageConfig;

    private WorldManager worldManager;
    private GameManager gameManager;

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
        pugnaConfig = new PugnaConfig(this);
        pugnaConfig.setup();

        gameConfig = new GameConfig(this);
        gameConfig.saveDefaultConfig();
        gameConfig.reload();

        messageConfig = new MessageConfig(this);
        messageConfig.saveDefaultConfig();
        messageConfig.reload();
        messageConfig.setup();

        /* === Managers === */
        worldManager = new WorldManager(pugnaConfig, messageConfig);
        if (pugnaConfig.isDevelopment() || (!pugnaConfig.isDevelopment() && !gameConfig.isRunning())) {
            worldManager.setup();
        }

        gameManager = new GameManager(pugnaConfig, messageConfig, gameConfig, worldManager);
        if (!pugnaConfig.isDevelopment()) {
            if (!gameConfig.isRunning()) {
                gameManager.prepareNewGame();
            } else {
                gameManager.resumeGame();
            }
        }

        /* === Listeners === */
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new BlockListener(messageConfig, gameManager), this);
        pluginManager.registerEvents(new ChatListener(pugnaConfig, gameManager), this);
        pluginManager.registerEvents(new ConnectionListener(pugnaConfig, messageConfig, worldManager, gameManager), this);
        pluginManager.registerEvents(new DamageListener(pugnaConfig, messageConfig, gameConfig, worldManager, gameManager), this);
        pluginManager.registerEvents(new InteractionListener(pugnaConfig, messageConfig, gameConfig, gameManager), this);
        pluginManager.registerEvents(new InventoryListener(pugnaConfig, messageConfig, gameConfig, gameManager), this);
        pluginManager.registerEvents(new PortalListener(pugnaConfig, messageConfig, gameConfig, worldManager), this);
        pluginManager.registerEvents(new WorldListener(pugnaConfig, gameManager), this);

        /* === Commands === */
        getCommand("countdown").setExecutor(new CountdownCommand(messageConfig, gameManager));
        getCommand("gui").setExecutor(new GuiCommand(messageConfig, gameManager));
        getCommand("hub").setExecutor(new HubCommand(messageConfig));
        getCommand("rules").setExecutor(new RulesCommand(messageConfig));
        getCommand("team").setExecutor(new TeamCommand(messageConfig, gameManager));

        /* === Logging === */
        Bukkit.getLogger().info(messageConfig.getRawPrefix() + " The plugin has been enabled.");
    }

    /* === Shutdown === */

    @Override
    public void onDisable() {
        /* === Clean Up === */
        if (pugnaConfig.isDevelopment() || gameManager.getState() == GameState.LOBBY_WAITING || gameManager.getState() == GameState.LOBBY_COUNTDOWN || gameManager.getState() == GameState.RESTARTING) {
            gameConfig.reset();
            worldManager.deleteWorldData();
        }

        /* === Logging === */
        Bukkit.getLogger().info(messageConfig.getRawPrefix() + "The plugin has been disabled.");
    }
}
