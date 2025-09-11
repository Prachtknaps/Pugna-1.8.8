package at.minecraft.pugna.utils;

import at.minecraft.pugna.config.GameConfig;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class BlockUtils {
    private BlockUtils() {}

    private static final List<Block> PROTECTED_OVERWORLD_BLOCKS = new ArrayList<>();
    private static final List<Block> PROTECTED_NETHER_BLOCKS = new ArrayList<>();

    private static final Set<Material> NON_SOLID = EnumSet.of(
            Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA,
            Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.SAPLING, Material.CROPS,
            Material.SEEDS, Material.SUGAR_CANE_BLOCK, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.FIRE,
            Material.SNOW, Material.TORCH, Material.SIGN_POST, Material.WALL_SIGN, Material.LADDER, Material.VINE, Material.WEB
    );

    /* === Operations === */

    public static boolean isProtectedBlock(Block block) {
        World world = block.getWorld();
        if (world == null) {
            return false;
        }

        if (world.getName().equals(GameConfig.getPugnaWorldName())) {
            return PROTECTED_OVERWORLD_BLOCKS.contains(block);
        } else if (world.getName().equals(GameConfig.getPugnaNetherWorldName())) {
            return PROTECTED_NETHER_BLOCKS.contains(block);
        }

        return false;
    }

    public static void addProtectedBlock(Block block) {
        World world = block.getWorld();
        if (world == null) {
            return;
        }

        if (world.getName().equals(GameConfig.getPugnaWorldName())) {
            PROTECTED_OVERWORLD_BLOCKS.add(block);
        } else if (world.getName().equals(GameConfig.getPugnaNetherWorldName())) {
            PROTECTED_NETHER_BLOCKS.add(block);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isSolid(Block block) {
        if (block == null) {
            return false;
        }

        Material material = block.getType();
        if (material == null) {
            return false;
        }

        return !NON_SOLID.contains(material);
    }

    public static boolean isEdgeBlock(Block block) {
        if (block == null) {
            return false;
        }

        BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
        for (BlockFace blockFace : blockFaces) {
            Block adjacentBlock = block.getRelative(blockFace);
            if (!isSolid(adjacentBlock)) {
                return true;
            }
        }

        return false;
    }
}
