package at.minecraft.pugna.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class GameConfig {
    private GameConfig() {}

    /* === Game === */
    private static boolean isLongGame;
    private static boolean allowReconnect;
    private static boolean friendlyFire;

    /* === Players & Teams === */
    private static int maxTeamsCount;
    private static int maxTeamCapacity;
    private static int minPlayersCount;
    private static List<String> defaultTeamNames;

    /* === Lobby === */
    private static String lobbyWorldName;
    private static double lobbySpawnX;
    private static double lobbySpawnY;
    private static double lobbySpawnZ;
    private static float lobbySpawnYaw;
    private static float lobbySpawnPitch;

    /* === Pugna === */
    private static String pugnaWorldName;
    private static String pugnaNetherWorldName;
    private static double pugnaSpawnX;
    private static double pugnaSpawnZ;
    private static float pugnaSpawnYaw;
    private static float pugnaSpawnPitch;

    /* === Border === */
    private static int borderBaseSize;
    private static int borderPerPlayerSize;
    private static int borderEndSize;
    private static int maxBorderSize;

    /* === Countdowns === */
    private static int lobbyCountdownSeconds;
    private static int gameCountdownSeconds;
    private static int restartCountdownSeconds;

    /* === Game Events === */
    private static int netherStartSeconds;
    private static int borderShrinkStartSeconds;
    private static int borderShrinkEndSeconds;
    private static int netherEndSeconds;
    private static int enemyRevealSeconds;
    private static int gameEndSeconds;
    private static int borderShrinkWarnStartSeconds;
    private static int netherEndWarnStartSeconds;
    private static int enemyRevealWarnStartSeconds;
    private static int gameEndWarnStartSeconds;

    /* === Item Names === */
    private static String teamSelectionItemName;
    private static String leaveItemName;
    private static String navigationItemName;

    /* === Setup === */

    public static void setup(FileConfiguration configuration) {
        isLongGame = true;
        allowReconnect = true;
        friendlyFire = false;

        maxTeamsCount = 16;
        maxTeamCapacity = 1;
        minPlayersCount = maxTeamCapacity + 1;
        defaultTeamNames = Arrays.asList(
                "Alpha", "Bravo", "Delta", "Echo", "Titan", "Phantom", "Shadow", "Storm",
                "Inferno", "Campers", "Hunters", "Farmers", "Warriors", "TrapMasters", "Chaos", "PotatoArmy",
                "ChickenKillers", "MelonGang", "Hackers", "Noobs", "RiskTakers", "TeamKill", "Nodus", "HackClient",
                "Killaura", "XRay", "Clowns", "GGWP", "Rekt", "TryHard", "ComboGods", "BlockHitters",
                "eZClap", "TNTGang", "KillStreak", "Rasur", "Terror", "Critters", "Sweat99", "SpawnTrap",
                "NoSkill", "Skillaura", "eZWin", "TeamOP", "Laggers", "QuickScope", "SwordZ", "CritHit",
                "WomboCombo", "Gapplers", "Ping999", "FirstKill", "FirstBlood", "BowSpammer", "Klatsche", "Abriss",
                "Zerstoerer", "Pruegler", "Drescher", "HauDrauf", "Schnetzler", "Watsche", "KlickSpeed", "AutoKlicker"
        );
        Collections.shuffle(defaultTeamNames);

        lobbyWorldName = "lobby";
        lobbySpawnX = 0.5;
        lobbySpawnY = 64.0;
        lobbySpawnZ = 0.5;
        lobbySpawnYaw = 0.0f;
        lobbySpawnPitch = 0.0f;

        pugnaWorldName = "pugna";
        pugnaNetherWorldName = "pugna_nether";
        pugnaSpawnX = 0.5;
        pugnaSpawnZ = 0.5;
        pugnaSpawnYaw = 0.0f;
        pugnaSpawnPitch = 0.0f;

        borderBaseSize = isLongGame ? 2048 : 1024;
        borderPerPlayerSize = 64;
        borderEndSize = isLongGame ? 512 : 256;
        maxBorderSize = 4096;

        lobbyCountdownSeconds = 10;
        gameCountdownSeconds = 10;
        restartCountdownSeconds = 25;

        netherStartSeconds = isLongGame ? (2 * 60 * 60) : (30 * 60);
        borderShrinkStartSeconds = isLongGame ? (6 * 60 * 60) : (90 * 60);
        borderShrinkEndSeconds = isLongGame ? (12 * 60 * 60) : (150 * 60);
        netherEndSeconds = isLongGame ? (14 * 60 * 60) : (3 * 60 * 60);
        enemyRevealSeconds = isLongGame ? (15 * 60 * 60) : (210 * 60);
        gameEndSeconds = isLongGame ? (16 * 60 * 60) : (4 * 60 * 60);
        borderShrinkWarnStartSeconds = isLongGame ? (borderShrinkStartSeconds - 30 * 60) : (borderShrinkStartSeconds - 15 * 60);
        netherEndWarnStartSeconds = isLongGame ? (netherEndSeconds - 30 * 60) : (netherEndSeconds - 15 * 60);
        enemyRevealWarnStartSeconds = isLongGame ? (enemyRevealSeconds - 30 * 60) : (enemyRevealSeconds - 15 * 60);
        gameEndWarnStartSeconds = isLongGame ? (gameEndSeconds - 30 * 60) : (gameEndSeconds - 15 * 60);

        teamSelectionItemName = "§aTeam-Auswahl§r";
        leaveItemName = "§cVerlassen§r";
        navigationItemName = "§6Navigator§r";
    }

    /* === Game Getters === */

    public static boolean isLongGame() {
        return isLongGame;
    }

    public static boolean allowReconnect() {
        return allowReconnect;
    }

    public static boolean isFriendlyFire() {
        return friendlyFire;
    }

    /* === Players & Teams Getters === */

    public static int getMaxTeamsCount() {
        return maxTeamsCount;
    }

    public static int getMaxTeamCapacity() {
        return maxTeamCapacity;
    }

    public static int getMinPlayersCount() {
        return minPlayersCount;
    }

    public static int getMaxPlayersCount() {
        return maxTeamsCount * maxTeamCapacity;
    }

    public static List<String> getDefaultTeamNames() {
        return defaultTeamNames;
    }

    /* === Lobby Getters === */

    public static String getLobbyWorldName() {
        return lobbyWorldName;
    }

    public static double getLobbySpawnX() {
        return lobbySpawnX;
    }

    public static double getLobbySpawnY() {
        return lobbySpawnY;
    }

    public static double getLobbySpawnZ() {
        return lobbySpawnZ;
    }

    public static float getLobbySpawnYaw() {
        return lobbySpawnYaw;
    }

    public static float getLobbySpawnPitch() {
        return lobbySpawnPitch;
    }

    /* === Pugna Getters === */

    public static String getPugnaWorldName() {
        return pugnaWorldName;
    }

    public static String getPugnaNetherWorldName() {
        return pugnaNetherWorldName;
    }

    public static double getPugnaSpawnX() {
        return pugnaSpawnX;
    }

    public static double getPugnaSpawnZ() {
        return pugnaSpawnZ;
    }

    public static float getPugnaSpawnYaw() {
        return pugnaSpawnYaw;
    }

    public static float getPugnaSpawnPitch() {
        return pugnaSpawnPitch;
    }

    /* === Border Getters === */

    public static int getBorderBaseSize() {
        return borderBaseSize;
    }

    public static int getBorderPerPlayerSize() {
        return borderPerPlayerSize;
    }

    public static int getBorderEndSize() {
        return borderEndSize;
    }

    public static int getMaxBorderSize() {
        return maxBorderSize;
    }

    /* === Countdown Getters === */

    public static int getLobbyCountdownSeconds() {
        return lobbyCountdownSeconds;
    }

    public static int getGameCountdownSeconds() {
        return gameCountdownSeconds;
    }

    public static int getRestartCountdownSeconds() {
        return restartCountdownSeconds;
    }

    /* === Game Event Getters === */

    public static int getNetherStartSeconds() {
        return netherStartSeconds;
    }

    public static int getBorderShrinkStartSeconds() {
        return borderShrinkStartSeconds;
    }

    public static int getBorderShrinkEndSeconds() {
        return borderShrinkEndSeconds;
    }

    public static int getNetherEndSeconds() {
        return netherEndSeconds;
    }

    public static int getEnemyRevealSeconds() {
        return enemyRevealSeconds;
    }

    public static int getGameEndSeconds() {
        return gameEndSeconds;
    }

    public static int getBorderShrinkWarnStartSeconds() {
        return borderShrinkWarnStartSeconds;
    }

    public static int getNetherEndWarnStartSeconds() {
        return netherEndWarnStartSeconds;
    }

    public static int getEnemyRevealWarnStartSeconds() {
        return enemyRevealWarnStartSeconds;
    }

    public static int getGameEndWarnStartSeconds() {
        return gameEndWarnStartSeconds;
    }

    /* === Item Name Getters === */

    public static String getTeamSelectionItemName() {
        return teamSelectionItemName;
    }

    public static String getLeaveItemName() {
        return leaveItemName;
    }

    public static String getNavigationItemName() {
        return navigationItemName;
    }
}
