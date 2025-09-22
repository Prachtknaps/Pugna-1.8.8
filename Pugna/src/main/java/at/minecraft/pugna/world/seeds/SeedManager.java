package at.minecraft.pugna.world.seeds;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.world.spawn.SpawnManager;
import org.bukkit.WorldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SeedManager {
    private final PugnaConfig pugnaConfig;
    private final PugnaSeed developmentSeed;
    private final List<PugnaSeed> seeds = new ArrayList<>();

    public SeedManager() {
        pugnaConfig = Pugna.getInstance().getPugnaConfig();

        if (pugnaConfig.isDevelopment()) {
            Long configSeed = pugnaConfig.getDevelopmentSeed();
            if (configSeed != null) {
                developmentSeed = new PugnaSeed(configSeed, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64));
                return;
            } else {
                developmentSeed = null;
            }
        } else {
            developmentSeed = null;
        }

        seeds.add(new PugnaSeed(-8204113709833529609L, WorldType.NORMAL, SpawnManager.calculateRadius(8), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(-5232363710034341358L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(-1318522836486262120L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64)));
        seeds.add(new PugnaSeed(-1124061784166597672L, WorldType.LARGE_BIOMES, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(13000111L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(14000054L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(159001039L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(171001122L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(4)));
        seeds.add(new PugnaSeed(174001683L, WorldType.NORMAL, SpawnManager.calculateRadius(8), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(178001189L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(194001283L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(2078843530080609644L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64))); // No Jungle
        seeds.add(new PugnaSeed(209002057L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(221001428L, WorldType.NORMAL, SpawnManager.calculateRadius(64), SpawnManager.calculateRadius(64)));
        seeds.add(new PugnaSeed(2577061104386389634L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(265001909L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
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
        seeds.add(new PugnaSeed(421003246L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(431003302L, WorldType.NORMAL, SpawnManager.calculateRadius(16), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(440003445L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(496003718L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(504004314L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(4)));
        seeds.add(new PugnaSeed(504004314L, WorldType.NORMAL, SpawnManager.calculateRadius(16), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(524003966L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(4)));
        seeds.add(new PugnaSeed(551004926L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(565006217L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(665004908L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(8)));
        seeds.add(new PugnaSeed(670006026L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(64)));
        seeds.add(new PugnaSeed(673006047L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(728006554L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(16)));
        seeds.add(new PugnaSeed(762008129L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));
        seeds.add(new PugnaSeed(96000991L, WorldType.NORMAL, SpawnManager.calculateRadius(4), SpawnManager.calculateRadius(32)));

        Collections.shuffle(seeds);
    }

    /* === Queries === */

    public PugnaSeed getRandomSeed(int radius) {
        if (pugnaConfig.isDevelopment() && developmentSeed != null) {
            return developmentSeed;
        }

        for (int seedIndex = 0; seedIndex < seeds.size(); seedIndex++) {
            PugnaSeed seed = seeds.get(seedIndex);
            if (radius >= seed.getMinRadius() && radius <= seed.getMaxRadius()) {
                return seeds.remove(seedIndex);
            }
        }

        long randomSeed = ThreadLocalRandom.current().nextLong();
        return new PugnaSeed(randomSeed, WorldType.NORMAL);
    }
}
