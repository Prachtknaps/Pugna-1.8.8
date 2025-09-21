package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.PugnaConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BlockUtils {
    private BlockUtils() {}

    private static Pugna plugin = null;
    private static PugnaConfig pugnaConfig = null;
    private static GameConfig gameConfig = null;

    private static final Set<String> PROTECTED_KEYS_OVERWORLD = new HashSet<>();
    private static final Set<String> PROTECTED_KEYS_NETHER = new HashSet<>();
    private static boolean protectedAreasLoaded = false;

    private static final Set<Material> NON_SOLID = EnumSet.of(
            Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA,
            Material.LONG_GRASS, Material.DOUBLE_PLANT, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.SAPLING, Material.CROPS,
            Material.SEEDS, Material.SUGAR_CANE_BLOCK, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.FIRE,
            Material.SNOW, Material.TORCH, Material.SIGN_POST, Material.WALL_SIGN, Material.LADDER, Material.VINE, Material.WEB
    );

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static PugnaConfig getPugnaConfig() {
        if (pugnaConfig == null) {
            pugnaConfig = getPlugin().getPugnaConfig();
        }

        return pugnaConfig;
    }

    private static GameConfig getGameConfig() {
        if (gameConfig == null) {
            gameConfig = getPlugin().getGameConfig();
        }

        return gameConfig;
    }

    /* === Queries === */

    public static boolean isProtectedArea(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        if (!protectedAreasLoaded) {
            loadProtectedAreasIntoSets();
        }

        String key = world.getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();

        if (world.getName().equals(getPugnaConfig().getPugnaWorldName())) {
            return PROTECTED_KEYS_OVERWORLD.contains(key);
        }
        else if (world.getName().equals(getPugnaConfig().getPugnaNetherWorldName())) {
            return PROTECTED_KEYS_NETHER.contains(key);
        }

        return false;
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

    /* === Helpers === */

    public static void reloadProtectedAreasCache()
    {
        PROTECTED_KEYS_OVERWORLD.clear();
        PROTECTED_KEYS_NETHER.clear();
        loadProtectedAreasIntoSets();
    }

    private static void loadProtectedAreasIntoSets()
    {
        PROTECTED_KEYS_OVERWORLD.clear();
        PROTECTED_KEYS_NETHER.clear();

        List<Location> protectedLocations = getGameConfig().getProtectedAreas();
        for (Location protectedLocation : protectedLocations) {
            if (protectedLocation == null || protectedLocation.getWorld() == null) {
                continue;
            }

            String worldName = protectedLocation.getWorld().getName();
            String key = worldName + "_" + protectedLocation.getBlockX() + "_" + protectedLocation.getBlockY() + "_" + protectedLocation.getBlockZ();

            if (worldName.equals(getPugnaConfig().getPugnaWorldName())) {
                PROTECTED_KEYS_OVERWORLD.add(key);
            } else if (worldName.equals(getPugnaConfig().getPugnaNetherWorldName())) {
                PROTECTED_KEYS_NETHER.add(key);
            }
        }

        protectedAreasLoaded = true;
    }
}
