package at.minecraft.pugna.world.spawn;

import at.minecraft.pugna.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class PlayerSpawn {
    private final World world;
    private final int spawnX;
    private final int spawnZ;
    private final int centerX;
    private final int centerZ;
    private int baseY;

    public PlayerSpawn(World world, int spawnX, int spawnZ, int centerX, int centerZ) {
        this.world = world;
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    /* === Getters === */

    public Location getLocation() {
        Location spawn = new Location(world, spawnX + 0.5, baseY - 1, spawnZ + 0.5);
        float yaw = getYawTowards(spawnX + 0.5, spawnZ + 0.5, centerX, centerZ);
        spawn.setYaw(yaw);
        spawn.setPitch(0.0f);
        return spawn;
    }

    @SuppressWarnings("deprecation")
    public void build() {
        if (world == null) {
            return;
        }

        int leftY = world.getHighestBlockYAt(spawnX - 1, spawnZ);
        int rightY = world.getHighestBlockYAt(spawnX + 1, spawnZ);
        int frontY = world.getHighestBlockYAt(spawnX, spawnZ - 1);
        int backY = world.getHighestBlockYAt(spawnX, spawnZ + 1);

        this.baseY = Math.max(Math.max(leftY, rightY), Math.max(frontY, backY));

        world.getBlockAt(spawnX, baseY, spawnZ).setType(Material.AIR);
        world.getBlockAt(spawnX, baseY - 1, spawnZ).setType(Material.AIR);
        Block andesite = world.getBlockAt(spawnX, baseY - 2, spawnZ);
        andesite.setType(Material.STONE);
        andesite.setData((byte) 5);

        placeSpawnSlab(spawnX - 1, spawnZ);
        placeSpawnSlab(spawnX + 1, spawnZ);
        placeSpawnSlab(spawnX, spawnZ - 1);
        placeSpawnSlab(spawnX, spawnZ + 1);
    }

    private void placeSpawnSlab(int x, int z) {
        world.getBlockAt(x, baseY, z).setType(Material.WOOD_STEP);

        for (int y = baseY - 1; y > 0; y--) {
            Block block = world.getBlockAt(x, y, z);
            if (y == baseY - 1) {
                if (block.getType() != Material.GRASS && block.getType() != Material.SAND && block.getType() != Material.GRAVEL && block.getType() != Material.DIRT) {
                    block.setType(Material.GRASS);
                }
                continue;
            }

            if (!BlockUtils.isSolid(block)) {
                if (y >= 60 && y < baseY - 1 && y >= baseY - 4) {
                    block.setType(Material.DIRT);
                } else {
                    block.setType(Material.STONE);
                }
            } else {
                break;
            }
        }
    }

    /* === Helpers === */

    private float getYawTowards(double fromX, double fromZ, double targetX, double targetZ) {
        double deltaX = targetX - fromX;
        double deltaZ = targetZ - fromZ;
        double yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0;
        return (float) yaw;
    }
}
