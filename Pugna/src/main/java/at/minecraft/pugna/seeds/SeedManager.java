package at.minecraft.pugna.seeds;

import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.spawn.SpawnManager;
import org.bukkit.WorldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeedManager {
    private final PugnaSeed developmentSeed;
    private final List<PugnaSeed> seeds = new ArrayList<>();

    public SeedManager() {
        developmentSeed = new PugnaSeed(323003467L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64));

        seeds.add(new PugnaSeed(13000111L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(14000054L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(159001039L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(171001122L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(4)));
        seeds.add(new PugnaSeed(174001683L, WorldType.NORMAL, SpawnManager.calculateRadius(8), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(178001189L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(209002057L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(221001428L, WorldType.NORMAL, SpawnManager.calculateRadius(64), SpawnManager.calculateRadius(64)));
        seeds.add(new PugnaSeed(268001818L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(271001863L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(277001924L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(280001950L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(29000366L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(306003281L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(320002395L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(323003467L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64)));
        seeds.add(new PugnaSeed(366002905L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64)));
        seeds.add(new PugnaSeed(391003168L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(396004521L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(417003340L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(42000278L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(504004314L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(4)));
        seeds.add(new PugnaSeed(504004314L, WorldType.NORMAL, SpawnManager.calculateRadius(16), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(551004926L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(565006217L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(670006026L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64)));
        seeds.add(new PugnaSeed(673006047L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(728006554L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(762008129L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(96000991L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));

        Collections.shuffle(seeds);
    }

    /* === Queries === */

    public PugnaSeed getRandomSeed(int radius) {
        if (GameConfig.isDevelopment()) {
            return developmentSeed;
        }

        while (!seeds.isEmpty()) {
            PugnaSeed seed = seeds.remove(0);
            if (radius >= seed.getMinRadius() && radius <= seed.getMaxRadius()) {
                return seed;
            }
        }

        return null;
    }
}
