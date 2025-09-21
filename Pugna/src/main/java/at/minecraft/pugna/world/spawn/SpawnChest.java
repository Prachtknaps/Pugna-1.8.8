package at.minecraft.pugna.world.spawn;

import at.minecraft.pugna.loot.LootManager;
import at.minecraft.pugna.utils.BlockUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class SpawnChest {
    private final World world;
    private final int centerX;
    private final int centerZ;
    private final int blockX;
    private final int blockZ;

    public SpawnChest(World world, int centerX, int centerZ, int blockX, int blockZ) {
        this.world = world;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.blockX = centerX + blockX;
        this.blockZ = centerZ + blockZ;
    }

    /* === Operations === */

    public void build() {
        if (world == null) {
            return;
        }

        int blockY = world.getHighestBlockYAt(blockX, blockZ);

        Block chestBlock = world.getBlockAt(blockX, blockY, blockZ);
        chestBlock.setType(Material.CHEST);

        /* === Remove block above, so chest can be opened === */
        Block above = chestBlock.getRelative(BlockFace.UP);
        if (above.getType().isOccluding()) {
            above.setType(Material.AIR);
        }

        /* === Place blocks, so chest is not in water === */
        for (int y = blockY - 1; y > 0; y--) {
            Block block = world.getBlockAt(blockX, y, blockZ);
            if (!BlockUtils.isSolid(block)) {
                if (y == blockY - 1) {
                    block.setType(Material.GRASS);
                } else if (y >= 60) {
                    block.setType(Material.DIRT);
                } else {
                    block.setType(Material.STONE);
                }
            } else {
                break;
            }
        }

        /* === Fill chest and set block face === */
        BlockState state = chestBlock.getState();
        if (state instanceof Chest) {
            org.bukkit.material.Chest data = (org.bukkit.material.Chest) state.getData();
            data.setFacingDirection(getOutwardFacing(blockX, blockZ, centerX, centerZ));
            state.setData(data);
            state.update(true, true);

            Chest chestState = (Chest) chestBlock.getState();
            Inventory inventory = chestState.getBlockInventory();
            inventory.clear();

            LootManager.fillChest(inventory);
        }
    }

    private BlockFace getOutwardFacing(int blockX, int blockZ, int centerX, int centerZ) {
        int offsetXFromCenter = blockX - centerX;
        int offsetZFromCenter = blockZ - centerZ;

        int absOffsetX = Math.abs(offsetXFromCenter);
        int absOffsetZ = Math.abs(offsetZFromCenter);

        if (absOffsetX >= absOffsetZ) {
            return offsetXFromCenter >= 0 ? BlockFace.EAST : BlockFace.WEST;
        } else {
            return offsetZFromCenter >= 0 ? BlockFace.SOUTH : BlockFace.NORTH;
        }
    }
}
