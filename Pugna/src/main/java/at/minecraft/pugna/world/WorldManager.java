package at.minecraft.pugna.world;

import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.game.GameState;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldManager {
    private final SeedManager seedManager;
    private List<List<Location>> teamSpawns;

    public WorldManager() {
        this.seedManager = new SeedManager();
        this.teamSpawns = new ArrayList<>();
    }

    /* === Queries === */

    public Location getLobbyWorldSpawn() {
        World world = Bukkit.getWorld(GameConfig.getLobbyWorldName());
        if (world != null) {
            Location spawnLocation = world.getSpawnLocation();
            return new Location(
                world,
                spawnLocation.getX() + 0.5,
                spawnLocation.getY(),
                spawnLocation.getZ() + 0.5,
                GameConfig.getLobbySpawnYaw(),
                GameConfig.getLobbySpawnPitch()
            );
        }

        return null;
    }

    public Location getPugnaWorldSpawn() {
        World world = Bukkit.getWorld(GameConfig.getPugnaWorldName());
        if (world != null) {
            Location spawnLocation = world.getSpawnLocation();
            return new Location(
                world,
                spawnLocation.getX() + 0.5,
                spawnLocation.getY(),
                spawnLocation.getZ() + 0.5,
                GameConfig.getPugnaSpawnYaw(),
                GameConfig.getPugnaSpawnPitch()
            );
        }

        return null;
    }

    public Location getPugnaNetherWorldSpawn() {
        World world = Bukkit.getWorld(GameConfig.getPugnaNetherWorldName());
        if (world != null) {
            Location spawnLocation = world.getSpawnLocation();
            return new Location(
                world,
                spawnLocation.getX() + 0.5,
                spawnLocation.getY(),
                spawnLocation.getZ() + 0.5,
                GameConfig.getPugnaSpawnYaw(),
                GameConfig.getPugnaSpawnPitch()
            );
        }

        return null;
    }

    public List<List<Location>> getTeamSpawns() {
        return teamSpawns;
    }

    /* === Operations === */

    public void setTeamSpawns(List<List<Location>> locations) {
        this.teamSpawns.clear();
        this.teamSpawns.addAll(locations);
    }

    public void setup() {
        World lobbyWorld = Bukkit.getWorld(GameConfig.getLobbyWorldName());
        if (lobbyWorld != null) {
            int x = (int) GameConfig.getLobbySpawnX();
            int y = (int) GameConfig.getLobbySpawnY();
            int z = (int) GameConfig.getLobbySpawnZ();
            lobbyWorld.setSpawnLocation(x, y, z);

            lobbyWorld.setStorm(false);
            lobbyWorld.setThundering(false);
            lobbyWorld.setTime(0L);
        }

        int maxPlayersCount = GameConfig.getMaxPlayersCount();
        int radius = SeedManager.getRadius(maxPlayersCount);

        PugnaSeed pugnaSeed = seedManager.getRandomSeed(radius);

        World pugnaWorld = generatePugnaWorld(pugnaSeed);
        World pugnaNetherWorld = generatePugnaNetherWorld(pugnaSeed);

        SpawnGenerator.buildSpawnHoles(pugnaWorld, GameConfig.getMaxTeamsCount(), GameConfig.getMaxTeamCapacity(), radius);
        SpawnGenerator.buildOverworldPortal(pugnaWorld);
        SpawnGenerator.buildNetherPortal(pugnaNetherWorld);
        SpawnGenerator.buildSpawnChests(pugnaWorld);

        Collections.shuffle(teamSpawns);
    }

    private World generatePugnaWorld(PugnaSeed pugnaSeed) {
        World pugnaWorld = Bukkit.getWorld(GameConfig.getPugnaWorldName());
        if (pugnaWorld == null) {
            WorldCreator worldCreator = new WorldCreator(GameConfig.getPugnaWorldName());
            worldCreator.environment(World.Environment.NORMAL).seed(pugnaSeed.getSeed()).type(pugnaSeed.getWorldType()).generateStructures(true);
            pugnaWorld = Bukkit.createWorld(worldCreator);

            pugnaWorld.setStorm(false);
            pugnaWorld.setThundering(false);
            pugnaWorld.setTime(0L);

            int x = (int) GameConfig.getPugnaSpawnX();
            int z = (int) GameConfig.getPugnaSpawnZ();
            int y = pugnaWorld.getHighestBlockYAt(x, z);
            pugnaWorld.setSpawnLocation(x, y, z);

            WorldBorder worldBorder = pugnaWorld.getWorldBorder();
            worldBorder.setCenter(getPugnaWorldSpawn());
            worldBorder.setSize(GameConfig.getBorderBaseSize());

            Bukkit.getLogger().info(ChatConfig.getRawPrefix() + "Generated world '" + GameConfig.getPugnaWorldName() + "' successfully.");
        }

        return pugnaWorld;
    }

    private World generatePugnaNetherWorld(PugnaSeed pugnaSeed) {
        World pugnaNetherWorld = Bukkit.getWorld(GameConfig.getPugnaNetherWorldName());
        if (pugnaNetherWorld == null) {
            WorldCreator worldCreator = new WorldCreator(GameConfig.getPugnaNetherWorldName());
            worldCreator.environment(World.Environment.NETHER).seed(pugnaSeed.getSeed()).type(pugnaSeed.getWorldType()).generateStructures(true);
            pugnaNetherWorld = Bukkit.createWorld(worldCreator);

            pugnaNetherWorld.setStorm(false);
            pugnaNetherWorld.setThundering(false);
            pugnaNetherWorld.setTime(0L);

            // TODO: Find safe spawn location
            int x = (int) GameConfig.getPugnaSpawnX();
            int z = (int) GameConfig.getPugnaSpawnZ();
            int y = pugnaNetherWorld.getHighestBlockYAt(x, z);
            pugnaNetherWorld.setSpawnLocation(x, y, z);

            WorldBorder worldBorder = pugnaNetherWorld.getWorldBorder();
            worldBorder.setCenter(getPugnaNetherWorldSpawn());
            worldBorder.setSize(GameConfig.getBorderBaseSize());

            Bukkit.getLogger().info(ChatConfig.getRawPrefix() + "Generated world '" + GameConfig.getPugnaWorldName() + "' successfully.");
        }

        return pugnaNetherWorld;
    }

    public void updateWorlds(GameState state) {
        // TODO: Implement method
    }

    public void cleanUp() {
        // TODO: Implement method
    }
}
