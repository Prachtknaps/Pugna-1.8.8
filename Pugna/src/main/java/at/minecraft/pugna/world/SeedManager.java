package at.minecraft.pugna.world;

import org.bukkit.WorldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeedManager {
    private final List<PugnaSeed> seeds;
    private final List<PugnaSeed> fallbackSeeds;

    public SeedManager() {
        this.seeds = new ArrayList<>();
        this.fallbackSeeds = new ArrayList<>();

        seeds.add(new PugnaSeed(-962001721392497129L, WorldType.NORMAL, getRadius(4), getRadius(16)));
        seeds.add(new PugnaSeed(-9008001471532830816L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        seeds.add(new PugnaSeed(-4906307494297548254L, WorldType.NORMAL, getRadius(4), getRadius(64)));
        seeds.add(new PugnaSeed(-2143667004870707025L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        seeds.add(new PugnaSeed(1729687638268160097L, WorldType.NORMAL, getRadius(4), getRadius(16)));
        seeds.add(new PugnaSeed(527616680074691L, WorldType.NORMAL, getRadius(8), getRadius(32)));
        seeds.add(new PugnaSeed(6233107651548891927L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        seeds.add(new PugnaSeed(6366066656597008191L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        seeds.add(new PugnaSeed(734280969794065885L, WorldType.NORMAL, getRadius(4), getRadius(8)));
        seeds.add(new PugnaSeed(8577512948142201969L, WorldType.NORMAL, getRadius(4), getRadius(32)));

        fallbackSeeds.add(new PugnaSeed(-8894149038167667808L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-8885078017521719982L, WorldType.LARGE_BIOMES, getRadius(4), getRadius(8)));
        fallbackSeeds.add(new PugnaSeed(-8885078017521719982L, WorldType.LARGE_BIOMES, getRadius(64), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(-8673685519865170248L, WorldType.NORMAL, getRadius(4), getRadius(4)));
        fallbackSeeds.add(new PugnaSeed(-8551267783604565771L, WorldType.NORMAL, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(-8474078034345086550L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-8053596573931421817L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-7682308875439307992L, WorldType.NORMAL, getRadius(8), getRadius(16)));
        fallbackSeeds.add(new PugnaSeed(-743207489990997L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-7369099047549311069L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-650222785831989273L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-6424820368719679468L, WorldType.LARGE_BIOMES, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(-6034992047254750874L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-6020338691407159676L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-5914064358487272612L, WorldType.LARGE_BIOMES, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(-4848138477098945876L, WorldType.LARGE_BIOMES, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-42935482621016826L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-3977825995975137422L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-3825873060168442844L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-375659030295596811L, WorldType.NORMAL, getRadius(4), getRadius(8)));
        fallbackSeeds.add(new PugnaSeed(-3744141706916904696L, WorldType.NORMAL, getRadius(32), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-3607358769854972353L, WorldType.NORMAL, getRadius(4), getRadius(8)));
        fallbackSeeds.add(new PugnaSeed(-3205924265108597577L, WorldType.NORMAL, getRadius(4), getRadius(16)));
        fallbackSeeds.add(new PugnaSeed(-3043254397624562585L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(-2170019815194929471L, WorldType.NORMAL, getRadius(4), getRadius(8)));
        fallbackSeeds.add(new PugnaSeed(-2143667004870707025L, WorldType.LARGE_BIOMES, getRadius(4), getRadius(4)));
        fallbackSeeds.add(new PugnaSeed(-2143667004870707025L, WorldType.LARGE_BIOMES, getRadius(16), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(-1395658838234062585L, WorldType.NORMAL, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(-121822084482133075L, WorldType.NORMAL, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(2216245725184595953L, WorldType.NORMAL, getRadius(4), getRadius(16)));
        fallbackSeeds.add(new PugnaSeed(2318603286584498072L, WorldType.NORMAL, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(2803858041678594127L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(4445099738546039185L, WorldType.NORMAL, getRadius(4), getRadius(16)));
        fallbackSeeds.add(new PugnaSeed(4518686497575638976L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(4765191863613008716L, WorldType.NORMAL, getRadius(4), getRadius(8)));
        fallbackSeeds.add(new PugnaSeed(5355569999875392260L, WorldType.NORMAL, getRadius(16), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(6232711810614094321L, WorldType.NORMAL, getRadius(4), getRadius(16)));
        fallbackSeeds.add(new PugnaSeed(6366066656597008191L, WorldType.LARGE_BIOMES, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(706400880456351982L, WorldType.NORMAL, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(7779056520217402195L, WorldType.NORMAL, getRadius(4), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(8089622022112454226L, WorldType.NORMAL, getRadius(4), getRadius(64)));
        fallbackSeeds.add(new PugnaSeed(8140545318447035051L, WorldType.NORMAL, getRadius(4), getRadius(16)));
        fallbackSeeds.add(new PugnaSeed(8754405425307174726L, WorldType.NORMAL, getRadius(32), getRadius(32)));
        fallbackSeeds.add(new PugnaSeed(8848543444972649023L, WorldType.NORMAL, getRadius(4), getRadius(4)));
        fallbackSeeds.add(new PugnaSeed(8998173059688504778L, WorldType.NORMAL, getRadius(4), getRadius(32)));
    }

    public PugnaSeed getRandomSeed(int radius) {
        List<PugnaSeed> openSeeds = new ArrayList<>();
        Collections.shuffle(seeds);
        Collections.shuffle(fallbackSeeds);
        openSeeds.addAll(seeds);
        openSeeds.addAll(fallbackSeeds);

        while (!openSeeds.isEmpty()) {
            PugnaSeed seed = openSeeds.get(0);
            if (radius >= seed.getMinRadius() && radius <= seed.getMaxRadius()) {
                return seed;
            } else {
                openSeeds.remove(seed);
            }
        }

        return null;
    }

    public static int getRadius(int playerCount) {
        if (playerCount <= 4) {
            return 24;
        }
        if (playerCount <= 8) {
            return 28;
        }
        if (playerCount <= 16) {
            return 32;
        }
        if (playerCount <= 32) {
            return 36;
        }
        if (playerCount <= 64) {
            return 56;
        }

        return 56 + ((playerCount - 64) / 16) * 8;
    }
}
