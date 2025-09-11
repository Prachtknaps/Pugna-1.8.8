package at.minecraft.pugna.world;

import org.bukkit.World;

public class OverworldPortal {
    private final World world;
    private final int centerX;
    private final int centerZ;
    private int baseY;

    public OverworldPortal(World world, int centerX, int centerZ) {
        this.world = world;
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    /* === Operations === */

    public void build() {
        if (world == null) {
            return;
        }

        // TODO: Implement method
    }

    private void buildPillars() {
        // TODO: Implement method
    }
}
