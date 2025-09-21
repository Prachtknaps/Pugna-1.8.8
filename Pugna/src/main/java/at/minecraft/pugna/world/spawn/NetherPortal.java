package at.minecraft.pugna.world.spawn;

import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class NetherPortal {
    private final GameConfig gameConfig;

    private final World world;
    private final int centerX;
    private final int centerZ;
    private int baseY;

    private final List<Location> placesBlockLocations = new ArrayList<>();

    public NetherPortal(GameConfig gameConfig, World world, int centerX, int centerZ) {
        this.gameConfig = gameConfig;

        this.world = world;
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    /* === Operations === */

    public void build() {
        if (world == null) {
            return;
        }

        int pillarNorthY = world.getHighestBlockYAt(centerX, centerZ - 3);
        int pillarEastY = world.getHighestBlockYAt(centerX + 4, centerZ);
        int pillarSouthY = world.getHighestBlockYAt(centerX, centerZ + 3);
        int pillarWestY = world.getHighestBlockYAt(centerX - 4, centerZ);

        int highestY = Math.max(Math.max(pillarNorthY, pillarEastY), Math.max(pillarSouthY, pillarWestY));
        baseY = highestY + 2;

        buildPillar(centerX, centerZ - 3);
        buildPillar(centerX + 4, centerZ);
        buildPillar(centerX, centerZ + 3);
        buildPillar(centerX - 4, centerZ);

        buildFloor();
        buildFrame();

        ignite();

        addSafeArea();
    }

    private void buildPillar(int x, int z) {
        for (int y = baseY; y > 0; y--) {
            Block block = world.getBlockAt(x, y, z);
            if (!BlockUtils.isSolid(block) || BlockUtils.isEdgeBlock(block)) {
                block.setType(Material.OBSIDIAN);
                placesBlockLocations.add(block.getLocation());
            }
        }
    }

    private void buildFloor() {
        for (int x = centerX + 3; x >= centerX - 3; x--) {
            for (int z = centerZ + 2; z >= centerZ - 2; z--) {
                if ((x == centerX + 3 && z == centerZ + 2) || (x == centerX - 3 && z == centerZ + 2) || (x == centerX - 3 && z == centerZ - 2) || (x == centerX + 3 && z == centerZ - 2)) {
                    continue;
                }

                Block block = world.getBlockAt(x, baseY, z);
                block.setType(Material.OBSIDIAN);
                placesBlockLocations.add(block.getLocation());
            }
        }
    }

    private void buildFrame() {
        for (int y = baseY + 1; y <= baseY + 5; y++) {
            Block block = world.getBlockAt(centerX + 2, y, centerZ);
            block.setType(Material.OBSIDIAN);
            placesBlockLocations.add(block.getLocation());
        }

        for (int y = baseY + 1; y <= baseY + 5; y++) {
            Block block = world.getBlockAt(centerX - 2, y, centerZ);
            block.setType(Material.OBSIDIAN);
            placesBlockLocations.add(block.getLocation());
        }

        for (int x = centerX + 1; x >= centerX - 1; x--) {
            Block block = world.getBlockAt(x, baseY + 5, centerZ);
            block.setType(Material.OBSIDIAN);
            placesBlockLocations.add(block.getLocation());
        }
    }

    private void ignite() {
        world.getBlockAt(centerX, baseY + 1, centerZ).setType(Material.FIRE);

        for (int x = centerX + 1; x >= centerX - 1; x--) {
            for (int y = baseY + 1; y <= baseY + 4; y++) {
                Block block = world.getBlockAt(x, y, centerZ);
                placesBlockLocations.add(block.getLocation());
            }
        }
    }

    private void addSafeArea() {
        for (int x = centerX - 5; x <= centerX + 5; x++) {
            for (int y = baseY + 1; y <= baseY + 5; y++) {
                for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                    Location location = new Location(world, x, y, z);
                    placesBlockLocations.add(location);
                }
            }
        }

        gameConfig.saveProtectedAreas(placesBlockLocations);
    }
}
