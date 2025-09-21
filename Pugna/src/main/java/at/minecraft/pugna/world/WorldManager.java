package at.minecraft.pugna.world;

import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.utils.FileSystemUtils;
import at.minecraft.pugna.world.seeds.PugnaSeed;
import at.minecraft.pugna.world.seeds.SeedManager;
import at.minecraft.pugna.world.spawn.SpawnManager;
import org.bukkit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;

    private final int maxPlayersCount;
    private final int spawnRadius;
    private final PugnaSeed pugnaSeed;

    private World lobbyWorld;
    private World pugnaWorld;
    private World pugnaNetherWorld;

    private Location lobbySpawnLocation;
    private Location pugnaSpawnLocation;
    private Location pugnaNetherSpawnLocation;

    private final List<List<Location>> teamSpawnLocations;

    public WorldManager(PugnaConfig pugnaConfig, MessageConfig messageConfig) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;

        SeedManager seedManager = new SeedManager();
        this.maxPlayersCount = pugnaConfig.getMaxPlayersCount();
        this.spawnRadius = SpawnManager.calculateRadius(maxPlayersCount);
        this.pugnaSeed = seedManager.getRandomSeed(spawnRadius);

        this.teamSpawnLocations = new ArrayList<>();
    }

    /* === Queries === */

    public World getLobbyWorld() {
        if (lobbyWorld == null) {
            World world = Bukkit.getWorld(pugnaConfig.getLobbyWorldName());
            if (world != null) {
                lobbyWorld = world;
            } else {
                lobbyWorld = generateLobbyWorld();
            }
        }

        return lobbyWorld;
    }

    public World getPugnaWorld() {
        if (pugnaWorld == null) {
            World world = Bukkit.getWorld(pugnaConfig.getPugnaWorldName());
            if (world != null) {
                pugnaWorld = world;
            } else {
                pugnaWorld = generatePugnaWorld();
            }
        }

        return pugnaWorld;
    }

    public World getPugnaNetherWorld() {
        if (pugnaNetherWorld == null) {
            World world = Bukkit.getWorld(pugnaConfig.getPugnaNetherWorldName());
            if (world != null) {
                pugnaNetherWorld = world;
            } else {
                pugnaNetherWorld = generatePugnaNetherWorld();
            }
        }

        return pugnaNetherWorld;
    }

    public Location getLobbySpawnLocation() {
        if (lobbySpawnLocation == null) {
            World world = getLobbyWorld();
            if (world == null) {
                world = generateLobbyWorld();
            }

            Location spawnLocation = world.getSpawnLocation();
            lobbySpawnLocation = new Location(
                world,
                spawnLocation.getBlockX() + 0.5,
                spawnLocation.getBlockY(),
                spawnLocation.getBlockZ() + 0.5,
                pugnaConfig.getLobbySpawnYaw(),
                pugnaConfig.getLobbySpawnPitch()
            );
        }

        return lobbySpawnLocation;
    }

    public Location getPugnaSpawnLocation() {
        if (pugnaSpawnLocation == null) {
            World world = getPugnaWorld();
            if (world == null) {
                world = generatePugnaWorld();
            }

            Location spawnLocation = world.getSpawnLocation();
            pugnaSpawnLocation = new Location(
                world,
                spawnLocation.getBlockX() + 0.5,
                spawnLocation.getBlockY(),
                spawnLocation.getBlockZ() + 0.5,
                pugnaConfig.getPugnaSpawnYaw(),
                pugnaConfig.getPugnaSpawnPitch()
            );
        }

        return pugnaSpawnLocation;
    }

    public Location getPugnaNetherSpawnLocation() {
        if (pugnaNetherSpawnLocation == null) {
            World world = getPugnaNetherWorld();
            if (world == null) {
                world = generatePugnaNetherWorld();
            }

            Location spawnLocation = world.getSpawnLocation();
            pugnaNetherSpawnLocation = new Location(
                world,
                spawnLocation.getBlockX() + 0.5,
                spawnLocation.getBlockY(),
                spawnLocation.getBlockZ() + 0.5,
                pugnaConfig.getPugnaSpawnYaw(),
                pugnaConfig.getPugnaSpawnPitch()
            );
        }

        return pugnaNetherSpawnLocation;
    }

    public List<List<Location>> getTeamSpawnLocations() {
        return teamSpawnLocations;
    }

    /* === Operations === */

    public void setTeamSpawnLocations(List<List<Location>> locations) {
        if (locations == null) {
            return;
        }

        this.teamSpawnLocations.clear();
        this.teamSpawnLocations.addAll(locations);
    }

    public void setup() {
        if (pugnaConfig.isDevelopment()) {
            pugnaWorld = generatePugnaWorld();
            pugnaNetherWorld = generatePugnaNetherWorld();

            SpawnManager.buildSpawnHoles(pugnaWorld, 4);
            SpawnManager.buildSpawnHoles(pugnaWorld, 8);
            SpawnManager.buildSpawnHoles(pugnaWorld, 16);
            SpawnManager.buildSpawnHoles(pugnaWorld, 32);
            SpawnManager.buildSpawnHoles(pugnaWorld, 64);

            SpawnManager.buildNetherPortal(pugnaWorld);
            SpawnManager.buildSpawnChests(pugnaWorld, maxPlayersCount);

            return;
        }

        lobbyWorld = getLobbyWorld();
        pugnaWorld = getPugnaWorld();
        pugnaNetherWorld = getPugnaNetherWorld();

        SpawnManager.buildTeamSpawns(pugnaWorld, pugnaConfig.getMaxTeamsCount(), pugnaConfig.getMaxTeamCapacity(), spawnRadius);
        SpawnManager.buildNetherPortal(pugnaWorld);
        SpawnManager.buildSpawnChests(pugnaWorld, maxPlayersCount);
    }

    private World generateLobbyWorld() {
        World world = Bukkit.getWorld(pugnaConfig.getLobbyWorldName());
        if (world == null) {
            WorldCreator worldCreator = new WorldCreator(pugnaConfig.getLobbyWorldName());
            worldCreator.environment(World.Environment.NORMAL).seed(pugnaSeed.getSeed()).type(WorldType.FLAT).generateStructures(false);
            world = Bukkit.createWorld(worldCreator);

            int x = (int) pugnaConfig.getLobbySpawnX();
            int z = (int) pugnaConfig.getLobbySpawnZ();
            int y = world.getHighestBlockYAt(x, z);
            world.setSpawnLocation(x, y, z);
        } else {
            int x = (int) pugnaConfig.getLobbySpawnX();
            int y = (int) pugnaConfig.getLobbySpawnY();
            int z = (int) pugnaConfig.getLobbySpawnZ();
            world.setSpawnLocation(x, y, z);
        }

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
            worldBorder.setSize(pugnaConfig.getBorderEndSize());
        }

        return world;
    }

    private World generatePugnaWorld() {
        World world = Bukkit.getWorld(pugnaConfig.getPugnaWorldName());
        if (world == null) {
            WorldCreator worldCreator = new WorldCreator(pugnaConfig.getPugnaWorldName());
            worldCreator.environment(World.Environment.NORMAL).seed(pugnaSeed.getSeed()).type(pugnaSeed.getWorldType()).generateStructures(true);
            world = Bukkit.createWorld(worldCreator);

            int x = (int) pugnaConfig.getPugnaSpawnX();
            int z = (int) pugnaConfig.getPugnaSpawnZ();
            int y = world.getHighestBlockYAt(x, z);
            world.setSpawnLocation(x, y, z);

            world.setDifficulty(Difficulty.HARD);
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(0L);

            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("randomTickSpeed", "100");
        }

        return world;
    }

    private World generatePugnaNetherWorld() {
        World world = Bukkit.getWorld(pugnaConfig.getPugnaNetherWorldName());
        if (world == null) {
            WorldCreator worldCreator = new WorldCreator(pugnaConfig.getPugnaNetherWorldName());
            worldCreator.environment(World.Environment.NETHER).seed(pugnaSeed.getSeed()).type(pugnaSeed.getWorldType()).generateStructures(true);
            world = Bukkit.createWorld(worldCreator);

            world.setDifficulty(Difficulty.HARD);
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(0L);

            WorldBorder worldBorder = world.getWorldBorder();
            if (worldBorder != null) {
                worldBorder.setCenter(pugnaConfig.getPugnaSpawnX(), pugnaConfig.getPugnaSpawnZ());
                worldBorder.setSize(pugnaConfig.getBorderBaseSize());
            }
        }

        return world;
    }

    public void deleteWorldData() {
        if (pugnaWorld != null) {
            Bukkit.unloadWorld(pugnaWorld, false);
        }

        if (pugnaNetherWorld != null) {
            Bukkit.unloadWorld(pugnaNetherWorld, false);
        }

        File container = Bukkit.getWorldContainer();
        File pugnaWorldDirectory = new File(container, pugnaConfig.getPugnaWorldName());
        File pugnaNetherWorldDirectory = new File(container, pugnaConfig.getPugnaNetherWorldName());
        File playerDataDirectory = new File(container, pugnaConfig.getLobbyWorldName() + File.separator + "playerdata");

        FileSystemUtils.deleteDirectory(pugnaWorldDirectory);
        FileSystemUtils.deleteDirectory(pugnaNetherWorldDirectory);
        FileSystemUtils.deleteDirectory(playerDataDirectory);

        Bukkit.getLogger().info(messageConfig.getRawPrefix() + "WorldManager.deleteWorlds: The server has been cleaned successfully.");
    }
}
