package at.minecraft.pugna.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

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
        // TODO: Implement method
        return false;
    }

    public static void addProtectedBlock(Block block) {
        // TODO: Implement method
    }

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
        // TODO: Implement method
        return false;
    }
}
