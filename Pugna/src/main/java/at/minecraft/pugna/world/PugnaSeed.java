package at.minecraft.pugna.world;

import org.bukkit.WorldType;

public class PugnaSeed {
    private final long seed;
    private final WorldType worldType;
    private final int minRadius;
    private final int maxRadius;

    public PugnaSeed(long seed, WorldType worldType, int minRadius, int maxRadius) {
        this.seed = seed;
        this.worldType = worldType;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
    }

    /* === Getters === */

    public long getSeed() {
        return seed;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public int getMinRadius() {
        return minRadius;
    }

    public int getMaxRadius() {
        return maxRadius;
    }
}
