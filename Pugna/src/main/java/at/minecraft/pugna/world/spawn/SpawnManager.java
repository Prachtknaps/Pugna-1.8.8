package at.minecraft.pugna.world.spawn;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpawnManager {
    private static Pugna plugin = null;
    private static GameConfig gameConfig = null;
    private static WorldManager worldManager = null;

    private final static int minRadius = 16;
    private final static int maxRadius = 128;

    /* === Getters === */

    public static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static GameConfig getGameConfig() {
        if (gameConfig == null) {
            gameConfig = getPlugin().getGameConfig();
        }

        return gameConfig;
    }

    public static WorldManager getWorldManager() {
        if (worldManager == null) {
            worldManager = getPlugin().getWorldManager();
        }

        return worldManager;
    }

    /* === Queries === */

    public static int calculateRadius(int playersCount) {
        double radius = 8.0 * (Math.log(playersCount) / Math.log(2.0)) + 8.0;
        radius = Math.max(minRadius, Math.min(maxRadius, radius));
        return (int) Math.round(radius);
    }

    /* === Operations === */

    public static void buildTeamSpawns(World world, int maxTeamsCount, int maxTeamCapacity, int radius) {
        if (world == null) {
            return;
        }

        List<List<Location>> teamSpawns = new ArrayList<>();
        for (int teamIndex = 0; teamIndex < maxTeamsCount; teamIndex++) {
            teamSpawns.add(new ArrayList<>(maxTeamCapacity));
        }

        Location spawnLocation = world.getSpawnLocation();
        int centerX = spawnLocation.getBlockX();
        int centerZ = spawnLocation.getBlockZ();

        int spawnsCount = maxTeamsCount * maxTeamCapacity;

        for (int spawnIndex = 0; spawnIndex < spawnsCount; spawnIndex++) {
            double angleRadians = (2.0 * Math.PI * spawnIndex) / (double) spawnsCount;
            double circleX = centerX + radius * Math.cos(angleRadians);
            double circleZ = centerZ + radius * Math.sin(angleRadians);

            int blockX = (int) Math.round(circleX);
            int blockZ = (int) Math.round(circleZ);

            PlayerSpawn playerSpawn = new PlayerSpawn(world, blockX, blockZ, centerX, centerZ);
            playerSpawn.build();

            int teamIndex = spawnIndex / maxTeamCapacity;
            teamSpawns.get(teamIndex).add(playerSpawn.getLocation());
        }

        Collections.shuffle(teamSpawns);
        getWorldManager().setTeamSpawnLocations(teamSpawns);
    }

    public static void buildSpawnHoles(World world, int spawnCount) {
        if (world == null) {
            return;
        }

        int radius = calculateRadius(spawnCount);

        Location spawnLocation = world.getSpawnLocation();
        int centerX = spawnLocation.getBlockX();
        int centerZ = spawnLocation.getBlockZ();

        for (int spawnIndex = 0; spawnIndex < spawnCount; spawnIndex++) {
            double angleRadians = (2.0 * Math.PI * spawnIndex) / (double) spawnCount;
            double circleX = centerX + radius * Math.cos(angleRadians);
            double circleZ = centerZ + radius * Math.sin(angleRadians);

            int blockX = (int) Math.round(circleX);
            int blockZ = (int) Math.round(circleZ);

            PlayerSpawn spawn = new PlayerSpawn(world, blockX, blockZ, centerX, centerZ);
            spawn.build();
        }
    }

    public static void buildNetherPortal(World world) {
        if (world == null) {
            return;
        }

        Location spawnLocation = world.getSpawnLocation();
        int centerX = spawnLocation.getBlockX();
        int centerZ = spawnLocation.getBlockZ();

        NetherPortal portal = new NetherPortal(getGameConfig(), world, centerX, centerZ);
        portal.build();
    }

    public static void buildSpawnChests(World world, int playersCount) {
        if (world == null) {
            return;
        }

        Location spawnLocation = world.getSpawnLocation();
        int centerX = spawnLocation.getBlockX();
        int centerZ = spawnLocation.getBlockZ();

        List<SpawnChest> spawnChests = new ArrayList<>();

        if (playersCount < 16) {
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 3, 6));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -3, 6));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -7, 2));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -7, -2));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -3, -6));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 3, -6));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 7, -2));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 7, 2));
        } else {
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 3, 5));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 0, 6));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -3, 5));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -6, 3));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -7, 0));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -6, -3));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, -3, -5));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 0, -6));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 3, -5));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 6, -3));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 7, 0));
            spawnChests.add(new SpawnChest(world, centerX, centerZ, 6, 3));
        }

        for (SpawnChest spawnChest : spawnChests) {
            spawnChest.build();
        }
    }
}
