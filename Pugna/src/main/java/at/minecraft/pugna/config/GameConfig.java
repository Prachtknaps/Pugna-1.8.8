package at.minecraft.pugna.config;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class GameConfig {
    private GameConfig() {}
    
    /* === Game === */
    private static boolean development;
    private static boolean running;
    private static int seconds;
    private static long gameTime;

    /* === Players/Teams === */
    private static boolean friendlyFire;
    private static int maxTeamsCount;
    private static int maxTeamCapacity;
    private static int minPlayersCount;
    private static List<String> defaultTeamNames = new ArrayList<>();

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
    private static double borderBaseSize;
    private static double borderEndSize;
    private static double maxBorderSize;
    private static double borderSizePerPlayer;

    /* === Countdowns === */
    private static int lobbyCountdownSeconds;
    private static int gameCountdownSeconds;
    private static int restartCountdownSeconds;

    /* === Events === */
    private static int netherStartCountdownStartSeconds;
    private static int netherStartSeconds;

    private static int borderShrinkCountdownStartSeconds;
    private static int borderShrinkStartSeconds;
    private static int borderShrinkEndSeconds;

    private static int netherEndCountdownStartSeconds;
    private static int netherEndSeconds;

    private static int enemyRevealCountdownStartSeconds;
    private static int enemyRevealSeconds;

    private static int gameEndCountdownStartSeconds;
    private static int gameEndSeconds;


    /* === Item Names === */
    private static String teamSelectionItemName;
    private static String navigationItemName;
    private static String leaveItemName;
    
    /* === Setup === */

    public static void setup() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        
        development = configuration.getBoolean("game.development", false);
        running = configuration.getBoolean("game.running", false);
        seconds = configuration.getInt("game.seconds", 0);
        if (!running) {
            seconds = 0;
        }
        gameTime = configuration.getLong("game.game_time", 0L);

        friendlyFire = configuration.getBoolean("players.friendly_fire", false);
        maxTeamsCount = configuration.getInt("players.max_teams_count", 16);
        maxTeamCapacity = configuration.getInt("players.max_team_capacity", 1);
        minPlayersCount = configuration.getInt("players.min_players_count", 2);
        if (minPlayersCount <= maxTeamCapacity) {
            minPlayersCount = maxTeamCapacity + 1;
        }
        defaultTeamNames.clear();
        defaultTeamNames = configuration.getStringList("players.default_team_names");
        Collections.shuffle(defaultTeamNames);

        lobbyWorldName = configuration.getString("lobby.world", "lobby");
        lobbySpawnX = configuration.getDouble("lobby.spawn.x", 0.5);
        lobbySpawnY = configuration.getDouble("lobby.spawn.y", 64.0);
        lobbySpawnZ = configuration.getDouble("lobby.spawn.z", 0.5);
        lobbySpawnYaw = (float) configuration.getDouble("lobby.spawn.yaw", 0.0);
        lobbySpawnPitch = (float) configuration.getDouble("lobby.spawn.pitch", 0.0);

        pugnaWorldName = configuration.getString("pugna.world", "pugna");
        pugnaNetherWorldName = configuration.getString("pugna.world_nether", "pugna_nether");
        pugnaSpawnX = configuration.getDouble("pugna.spawn.x", 0.5);
        pugnaSpawnZ = configuration.getDouble("pugna.spawn.z", 0.5);
        pugnaSpawnYaw = (float) configuration.getDouble("pugna.spawn.yaw", 0.0);
        pugnaSpawnPitch = (float) configuration.getDouble("pugna.spawn.pitch", 0.0);

        borderBaseSize = configuration.getDouble("border.base_size", 2048.0);
        borderEndSize = configuration.getDouble("border.end_size", 256.0);
        maxBorderSize = configuration.getDouble("border.max_size", 4096.0);
        borderSizePerPlayer = configuration.getDouble("border.size_per_player", 64.0);

        lobbyCountdownSeconds = configuration.getInt("countdowns.lobby_countdown_seconds", 60);
        gameCountdownSeconds = configuration.getInt("countdowns.game_countdown_seconds", 40);
        restartCountdownSeconds = configuration.getInt("countdowns.restart_countdown_seconds", 25);

        netherStartCountdownStartSeconds = configuration.getInt("events.nether_start.countdown_start_seconds", 3600);
        netherStartSeconds = configuration.getInt("events.nether_start.event_seconds", 5400);

        borderShrinkCountdownStartSeconds = configuration.getInt("events.border_shrink.countdown_start_seconds", 12600);
        borderShrinkStartSeconds = configuration.getInt("events.border_shrink.event_start_seconds", 14400);
        borderShrinkEndSeconds = configuration.getInt("events.border_shrink.event_end_seconds", 36000);

        netherEndCountdownStartSeconds = configuration.getInt("events.nether_end.countdown_start_seconds", 41400);
        netherEndSeconds = configuration.getInt("events.nether_end.event_seconds", 43200);

        enemyRevealCountdownStartSeconds = configuration.getInt("events.enemy_reveal.countdown_start_seconds", 48600);
        enemyRevealSeconds = configuration.getInt("events.enemy_reveal.event_seconds", 50400);

        gameEndCountdownStartSeconds = configuration.getInt("events.game_end.countdown_start_seconds", 55800);
        gameEndSeconds = configuration.getInt("events.game_end.event_seconds", 57600);

        teamSelectionItemName = configuration.getString("item_names.team_selection_item", "§aTeam-Auswahl§r");
        navigationItemName = configuration.getString("item_names.navigation_item", "§6Navigation§r");
        leaveItemName = configuration.getString("item_names.leave_item", "§cVerlassen§r");
    }

    public static void reset() {
        setRunning(false);
        setSeconds(0);
        saveGameTime(0L);
        clearProtectedAreas();
        clearTeams();
        saveBorderSize(0.0);
    }

    /* === Game Getters & Setters === */

    public static boolean isDevelopment() {
        return development;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("game.running", running);
        Pugna.getInstance().saveConfig();
    }

    public static int getSeconds() {
        return seconds;
    }

    public static void setSeconds(int seconds) {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("game.seconds", seconds);
        Pugna.getInstance().saveConfig();
    }

    public static long getGameTime() {
        return gameTime;
    }

    public static void saveGameTime(long gameTime) {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("game.game_time", gameTime);
        Pugna.getInstance().saveConfig();
    }

    public static List<Location> getProtectedAreas() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        List<Location> locations = new ArrayList<>();

        ConfigurationSection section = configuration.getConfigurationSection("game.protected_areas");
        if (section == null) {
            return locations;
        }

        for (String key : section.getKeys(false)) {
            String worldName = configuration.getString("game.protected_areas." + key + ".world");
            if (worldName == null || worldName.isEmpty()) {
                continue;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }

            int x = configuration.getInt("game.protected_areas." + key + ".x");
            int y = configuration.getInt("game.protected_areas." + key + ".y");
            int z = configuration.getInt("game.protected_areas." + key + ".z");

            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }


    public static void saveProtectedArea(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }

        FileConfiguration configuration = Pugna.getInstance().getConfig();

        String key = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();

        configuration.set("game.protected_areas." + key + ".world", location.getWorld().getName());
        configuration.set("game.protected_areas." + key + ".x", location.getBlockX());
        configuration.set("game.protected_areas." + key + ".y", location.getBlockY());
        configuration.set("game.protected_areas." + key + ".z", location.getBlockZ());

        Pugna.getInstance().saveConfig();
        BlockUtils.reloadProtectedAreasCache();
    }

    public static void saveProtectedAreas(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return;
        }

        FileConfiguration configuration = Pugna.getInstance().getConfig();

        for (Location location : locations) {
            if (location == null || location.getWorld() == null) {
                continue;
            }

            String key = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();

            configuration.set("game.protected_areas." + key + ".world", location.getWorld().getName());
            configuration.set("game.protected_areas." + key + ".x", location.getBlockX());
            configuration.set("game.protected_areas." + key + ".y", location.getBlockY());
            configuration.set("game.protected_areas." + key + ".z", location.getBlockZ());
        }

        Pugna.getInstance().saveConfig();
        BlockUtils.reloadProtectedAreasCache();
    }

    private static void clearProtectedAreas() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("game.protected_areas", null);
        Pugna.getInstance().saveConfig();
        BlockUtils.reloadProtectedAreasCache();
    }

    /* === Players/Teams Getters & Setters === */

    public static boolean isFriendlyFire() {
        return friendlyFire;
    }

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

    public static List<Team> getTeams() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        List<Team> teams = new ArrayList<>();

        ConfigurationSection section = configuration.getConfigurationSection("players.teams");
        if (section == null) {
            return teams;
        }

        for (String key : section.getKeys(false)) {
            int id;
            try {
                id = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                continue;
            }

            String name = configuration.getString("players.teams." + key + ".name");
            if (name == null || name.isEmpty()) {
                continue;
            }

            List<String> uuidStrings = configuration.getStringList("players.teams." + key + ".players");
            List<UUID> playerUuids = new ArrayList<>();
            for (String uuidString : uuidStrings) {
                try {
                    playerUuids.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException ignored) {
                    continue;
                }
            }

            Team team = new Team(id, maxTeamCapacity);
            team.setName(name);

            for (UUID uuid : playerUuids) {
                team.add(uuid);
            }

            teams.add(team);
        }

        return teams;
    }

    public static void saveTeams(List<Team> teams) {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("players.teams", null);

        for (Team team : teams) {
            int id = team.getId();
            String name = team.getName();

            configuration.set("players.teams." + id + ".name", name);
            List<String> uuidStrings = new ArrayList<>();
            for (UUID uuid : team.getPlayers()) {
                uuidStrings.add(uuid.toString());
            }

            configuration.set("players.teams." + id + ".players", uuidStrings);
        }

        Pugna.getInstance().saveConfig();
    }

    private static void clearTeams() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("players.teams", null);
        Pugna.getInstance().saveConfig();
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

    /* === Border Getters & Setters === */

    public static double getBorderSize() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        return configuration.getDouble("border.size", borderBaseSize);
    }

    public static void saveBorderSize(double size) {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("border.size", size);
        Pugna.getInstance().saveConfig();
    }

    public static double getBorderBaseSize() {
        return borderBaseSize;
    }

    public static double getBorderEndSize() {
        return borderEndSize;
    }

    public static double getMaxBorderSize() {
        return maxBorderSize;
    }

    public static double getBorderSizePerPlayer() {
        return borderSizePerPlayer;
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

    /* === Event Getters === */

    public static int getNetherStartCountdownStartSeconds() {
        return netherStartCountdownStartSeconds;
    }

    public static int getNetherStartSeconds() {
        return netherStartSeconds;
    }

    public static int getBorderShrinkCountdownStartSeconds() {
        return borderShrinkCountdownStartSeconds;
    }

    public static int getBorderShrinkStartSeconds() {
        return borderShrinkStartSeconds;
    }

    public static int getBorderShrinkEndSeconds() {
        return borderShrinkEndSeconds;
    }

    public static int getNetherEndCountdownStartSeconds() {
        return netherEndCountdownStartSeconds;
    }

    public static int getNetherEndSeconds() {
        return netherEndSeconds;
    }

    public static int getEnemyRevealCountdownStartSeconds() {
        return enemyRevealCountdownStartSeconds;
    }

    public static int getEnemyRevealSeconds() {
        return enemyRevealSeconds;
    }

    public static int getGameEndCountdownStartSeconds() {
        return gameEndCountdownStartSeconds;
    }

    public static int getGameEndSeconds() {
        return gameEndSeconds;
    }

    /* === Item Name Getters === */

    public static String getTeamSelectionItemName() {
        return teamSelectionItemName;
    }

    public static String getNavigationItemName() {
        return navigationItemName;
    }

    public static String getLeaveItemName() {
        return leaveItemName;
    }
}
