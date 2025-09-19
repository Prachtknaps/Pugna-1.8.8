package at.minecraft.pugna.world;

import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.seeds.PugnaSeed;
import at.minecraft.pugna.seeds.SeedManager;
import at.minecraft.pugna.spawn.SpawnManager;
import at.minecraft.pugna.utils.FileSystemUtils;
import org.bukkit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldManager {
    private final int maxPlayersCount;
    private final int spawnRadius;
    private final PugnaSeed pugnaSeed;

    private World lobbyWorld = null;
    private World pugnaWorld = null;
    private World pugnaNetherWorld = null;

    private Location lobbySpawnLocation = null;
    private Location pugnaSpawnLocation = null;
    private Location pugnaNetherSpawnLocation = null;

    private final List<List<Location>> teamSpawns;

    public WorldManager() {
        SeedManager seedManager = new SeedManager();

        this.maxPlayersCount = GameConfig.getMaxPlayersCount();
        this.spawnRadius = SpawnManager.calculateRadius(maxPlayersCount);
        this.pugnaSeed = seedManager.getRandomSeed(spawnRadius);

        this.teamSpawns = new ArrayList<>();
    }

    /* === Queries === */

    public World getLobbyWorld() {
        if (lobbyWorld == null) {
            World world = Bukkit.getWorld(GameConfig.getLobbyWorldName());
            if (world != null) {
                lobbyWorld = world;
            } else {
                lobbyWorld = setupLobbyWorld();
            }
        }

        return lobbyWorld;
    }

    public World getPugnaWorld() {
        if (pugnaWorld == null) {
            World world = Bukkit.getWorld(GameConfig.getPugnaWorldName());
            if (world != null) {
                pugnaWorld = world;
            } else {
                pugnaWorld = setupPugnaWorld();
            }
        }

        return pugnaWorld;
    }

    public World getPugnaNetherWorld() {
        if (pugnaNetherWorld == null) {
            World world = Bukkit.getWorld(GameConfig.getPugnaNetherWorldName());
            if (world != null) {
                pugnaNetherWorld = world;
            } else {
                pugnaNetherWorld = setupPugnaNetherWorld();
            }
        }

        return pugnaNetherWorld;
    }

    public Location getLobbySpawnLocation() {
        if (lobbySpawnLocation == null) {
            World world = getLobbyWorld();
            if (world == null) {
                world = setupLobbyWorld();
            }

            Location spawnLocation = world.getSpawnLocation();
            lobbySpawnLocation = new Location(
                world,
                spawnLocation.getBlockX() + 0.5,
                spawnLocation.getBlockY(),
                spawnLocation.getBlockZ() + 0.5,
                GameConfig.getLobbySpawnYaw(),
                GameConfig.getLobbySpawnPitch()
            );
        }

        return lobbySpawnLocation;
    }

    public Location getPugnaSpawnLocation() {
        if (pugnaSpawnLocation == null) {
            World world = getPugnaWorld();
            if (world == null) {
                world = setupPugnaWorld();
            }

            Location spawnLocation = world.getSpawnLocation();
            pugnaSpawnLocation = new Location(
                world,
                spawnLocation.getBlockX() + 0.5,
                spawnLocation.getBlockY(),
                spawnLocation.getBlockZ() + 0.5,
                GameConfig.getPugnaSpawnYaw(),
                GameConfig.getPugnaSpawnPitch()
            );
        }

        return pugnaSpawnLocation;
    }

    public Location getPugnaNetherSpawnLocation() {
        if (pugnaNetherSpawnLocation == null) {
            World world = getPugnaNetherWorld();
            if (world == null) {
                world = setupPugnaNetherWorld();
            }

            Location spawnLocation = world.getSpawnLocation();
            pugnaNetherSpawnLocation = new Location(
                world,
                spawnLocation.getBlockX() + 0.5,
                spawnLocation.getBlockY(),
                spawnLocation.getBlockZ() + 0.5,
                GameConfig.getPugnaSpawnYaw(),
                GameConfig.getPugnaSpawnPitch()
            );
        }

        return pugnaNetherSpawnLocation;
    }

    public List<List<Location>> getTeamSpawns() {
        return teamSpawns;
    }

    /* === Operations === */

    public void setTeamSpawns(List<List<Location>> locations) {
        teamSpawns.clear();
        teamSpawns.addAll(locations);
    }

    public void setup() {
        if (GameConfig.isDevelopment()) {
            pugnaWorld = setupPugnaWorld();
            pugnaNetherWorld = setupPugnaNetherWorld();

            SpawnManager.buildDevSpawnHoles(pugnaWorld, 4);
            SpawnManager.buildDevSpawnHoles(pugnaWorld, 8);
            SpawnManager.buildDevSpawnHoles(pugnaWorld, 16);
            SpawnManager.buildDevSpawnHoles(pugnaWorld, 32);
            SpawnManager.buildDevSpawnHoles(pugnaWorld, 64);
            SpawnManager.buildNetherPortal(pugnaWorld);
            SpawnManager.buildSpawnChests(pugnaWorld, 16);

            return;
        }

        lobbyWorld = getLobbyWorld();
        pugnaWorld = getPugnaWorld();
        pugnaNetherWorld = getPugnaNetherWorld();

        SpawnManager.buildSpawnHoles(pugnaWorld, GameConfig.getMaxTeamsCount(), GameConfig.getMaxTeamCapacity(), spawnRadius);
        SpawnManager.buildNetherPortal(pugnaWorld);
        SpawnManager.buildSpawnChests(pugnaWorld, maxPlayersCount);
    }

    private World setupLobbyWorld() {
        World world = Bukkit.getWorld(GameConfig.getLobbyWorldName());
        if (world == null) {
            Bukkit.getLogger().info("Setting up lobby world");

            WorldCreator worldCreator = new WorldCreator(GameConfig.getLobbyWorldName());
            worldCreator.environment(World.Environment.NORMAL).seed(pugnaSeed.getSeed()).type(WorldType.FLAT).generateStructures(false);
            world = Bukkit.createWorld(worldCreator);

            int x = (int) GameConfig.getLobbySpawnX();
            int y = (int) GameConfig.getLobbySpawnY();
            int z = (int) GameConfig.getLobbySpawnZ();
            world.setSpawnLocation(x, y, z);

            world.setDifficulty(Difficulty.PEACEFUL);
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(0L);

            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("randomTickSpeed", "0");

            WorldBorder worldBorder = world.getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setCenter(world.getSpawnLocation());
                worldBorder.setSize(GameConfig.getBorderEndSize());
            }
        }

        return world;
    }

    private World setupPugnaWorld() {
        World world = Bukkit.getWorld(GameConfig.getPugnaWorldName());
        if (world == null) {
            Bukkit.getLogger().info("Setting up pugna world");

            WorldCreator worldCreator = new WorldCreator(GameConfig.getPugnaWorldName());
            worldCreator.environment(World.Environment.NORMAL).seed(pugnaSeed.getSeed()).type(pugnaSeed.getWorldType()).generateStructures(true);
            world = Bukkit.createWorld(worldCreator);

            int x = (int) GameConfig.getPugnaSpawnX();
            int z = (int) GameConfig.getPugnaSpawnZ();
            int y = world.getHighestBlockYAt(x, z);
            world.setSpawnLocation(x, y, z);

            world.setDifficulty(Difficulty.PEACEFUL);
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(0L);

            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("randomTickSpeed", "100");

            WorldBorder worldBorder = world.getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setCenter(world.getSpawnLocation());
                worldBorder.setSize(GameConfig.getBorderBaseSize());
            }
        }

        return world;
    }

    private World setupPugnaNetherWorld() {
        World world = Bukkit.getWorld(GameConfig.getPugnaNetherWorldName());
        if (world == null) {
            Bukkit.getLogger().info("Setting up pugna nether world");

            WorldCreator worldCreator = new WorldCreator(GameConfig.getPugnaNetherWorldName());
            worldCreator.environment(World.Environment.NETHER).seed(pugnaSeed.getSeed()).type(pugnaSeed.getWorldType()).generateStructures(true);
            world = Bukkit.createWorld(worldCreator);

            world.setDifficulty(Difficulty.PEACEFUL);
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(0L);

            WorldBorder worldBorder = world.getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setCenter(world.getSpawnLocation());
                worldBorder.setSize(GameConfig.getBorderBaseSize() / 2);
            }
        }

        return world;
    }

    public void updateWorlds(GameState state) {
        World pugnaWorld = getPugnaWorld();
        World pugnaNetherWorld = getPugnaNetherWorld();

        if (pugnaWorld == null || pugnaNetherWorld == null) {
            return;
        }

        List<World> worlds = Arrays.asList(pugnaWorld, pugnaNetherWorld);

        if (state == GameState.LOBBY_WAITING || state == GameState.LOBBY_COUNTDOWN || state == GameState.GAME_COUNTDOWN) {
            for (World world : worlds) {
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("doFireTick", "false");
                world.setGameRuleValue("mobGriefing", "false");
                world.setGameRuleValue("randomTickSpeed", "100");
                world.setDifficulty(Difficulty.PEACEFUL);
            }
        } else if (state == GameState.GAME_RUNNING) {
            for (World world : worlds) {
                world.setGameRuleValue("doDaylightCycle", "true");
                world.setGameRuleValue("doFireTick", "true");
                world.setGameRuleValue("mobGriefing", "true");
                world.setGameRuleValue("randomTickSpeed", "3");
                world.setDifficulty(Difficulty.HARD);
            }
        } else if (state == GameState.GAME_PAUSED) {
            for (World world : worlds) {
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("doFireTick", "false");
                world.setGameRuleValue("mobGriefing", "false");
                world.setDifficulty(Difficulty.HARD);
            }
        } else if (state == GameState.RESTARTING) {
            for (World world : worlds) {
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("doFireTick", "false");
            }
        }
    }

    public void deleteWorlds() {
        if (pugnaWorld != null) {
            Bukkit.unloadWorld(pugnaWorld, false);
        }

        if (pugnaNetherWorld != null) {
            Bukkit.unloadWorld(pugnaNetherWorld, false);
        }

        File container = Bukkit.getWorldContainer();
        File pugnaWorldDirectory = new File(container, GameConfig.getPugnaWorldName());
        File pugnaNetherWorldDirectory = new File(container, GameConfig.getPugnaNetherWorldName());
        File playerDataDirectory = new File(container, GameConfig.getLobbyWorldName() + File.separator + "playerdata");

        FileSystemUtils.deleteDirectory(pugnaWorldDirectory);
        FileSystemUtils.deleteDirectory(pugnaNetherWorldDirectory);
        FileSystemUtils.deleteDirectory(playerDataDirectory);

        Bukkit.getLogger().info(MessageConfig.getRawPrefix() + "WorldManager.deleteWorlds: The server has been cleaned successfully.");
    }
}
