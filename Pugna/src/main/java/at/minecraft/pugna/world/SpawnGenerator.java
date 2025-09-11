package at.minecraft.pugna.world;

import at.minecraft.pugna.Pugna;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public final class SpawnGenerator {
    private SpawnGenerator() {}

    private static Pugna plugin = null;
    private static WorldManager worldManager = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static WorldManager getWorldManager() {
        if (worldManager == null) {
            worldManager = getPlugin().getWorldManager();
        }

        return worldManager;
    }

    /* === Operations === */

    public static void buildSpawnHoles(World world, int teamsCount, int teamCapacity, int radius) {
        if (world == null) {
            return;
        }

        List<List<Location>> teamSpawns = new ArrayList<>();
        for (int teamIndex = 0; teamIndex < teamsCount; teamIndex++) {
            teamSpawns.add(new ArrayList<>(teamCapacity));
        }

        Location spawnLocation = world.getSpawnLocation();
        int centerX = spawnLocation.getBlockX();
        int centerZ = spawnLocation.getBlockZ();

        int spawnCount = teamsCount * teamCapacity;

        for (int spawnIndex = 0; spawnIndex < spawnCount; spawnIndex++) {
            double angleRadians = (2.0 * Math.PI * spawnIndex) / (double) spawnCount;
            double circleX = centerX + radius * Math.cos(angleRadians);
            double circleZ = centerZ + radius * Math.sin(angleRadians);

            int blockX = (int) Math.round(circleX);
            int blockZ = (int) Math.round(circleZ);

            PlayerSpawn playerSpawn = new PlayerSpawn(world, blockX, blockZ);
            playerSpawn.build();

            int teamIndex = spawnIndex / teamCapacity;
            teamSpawns.get(teamIndex).add(playerSpawn.getLocation());
        }

        getWorldManager().setTeamSpawns(teamSpawns);
    }

    public static void buildOverworldPortal(World world) {
        if (world == null) {
            return;
        }

        Location spawnLocation = world.getSpawnLocation();
        int centerX = spawnLocation.getBlockX();
        int centerZ = spawnLocation.getBlockZ();

        // TODO: Implement method
    }

    public static void buildNetherPortal(World world) {
        if (world == null) {
            return;
        }

        Location spawnLocation = world.getSpawnLocation();
        int centerX = spawnLocation.getBlockX();
        int baseY = spawnLocation.getBlockY();
        int centerZ = spawnLocation.getBlockZ();

        // TODO: Implement method
    }

    public static void buildSpawnChests(World world) {
        if (world == null) {
            return;
        }

        // TODO: Implement method
    }
}
