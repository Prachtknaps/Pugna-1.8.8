package at.minecraft.pugna.config;

import at.minecraft.pugna.Pugna;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PugnaConfig {
    private final FileConfiguration configuration;

    /* === Game === */
    private boolean development;
    private Long developmentSeed;

    /* === Players/Teams === */
    private boolean friendlyFire;
    private int maxTeamsCount;
    private int maxTeamCapacity;
    private int minPlayersCount;
    private int maxPlayersCount;
    private List<String> defaultTeamNames = new ArrayList<>();

    /* === Lobby === */
    private String lobbyWorldName;
    private double lobbySpawnX;
    private double lobbySpawnY;
    private double lobbySpawnZ;
    private float lobbySpawnYaw;
    private float lobbySpawnPitch;

    /* === Pugna === */
    private String pugnaWorldName;
    private String pugnaNetherWorldName;
    private double pugnaSpawnX;
    private double pugnaSpawnZ;
    private float pugnaSpawnYaw;
    private float pugnaSpawnPitch;

    /* === Border === */
    private double borderBaseSize;
    private double borderEndSize;
    private double maxBorderSize;
    private double borderSizePerPlayer;

    /* === Countdowns === */
    private int lobbyCountdownSeconds;
    private int gameCountdownSeconds;
    private int restartCountdownSeconds;

    /* === Events === */
    private int netherStartCountdownStartSeconds;
    private int netherStartSeconds;

    private int borderShrinkStartCountdownStartSeconds;
    private int borderShrinkStartSeconds;
    private int borderShrinkEndSeconds;

    private int netherEndCountdownStartSeconds;
    private int netherEndSeconds;

    private int enemyRevealCountdownStartSeconds;
    private int enemyRevealSeconds;

    private int gameEndCountdownStartSeconds;
    private int gameEndSeconds;

    /* === Item Names === */
    private String teamSelectionItemName;
    private String navigationItemName;
    private String leaveItemName;

    public PugnaConfig(Pugna plugin) {
        plugin.saveDefaultConfig();
        this.configuration = plugin.getConfig();
    }

    /* === Getters === */

    public boolean isDevelopment() {
        return development;
    }

    public Long getDevelopmentSeed() {
        return developmentSeed;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public int getMaxTeamsCount() {
        return maxTeamsCount;
    }

    public int getMaxTeamCapacity() {
        return maxTeamCapacity;
    }

    public int getMinPlayersCount() {
        return minPlayersCount;
    }

    public int getMaxPlayersCount() {
        return maxPlayersCount;
    }

    public List<String> getDefaultTeamNames() {
        return defaultTeamNames;
    }

    public String getLobbyWorldName() {
        return lobbyWorldName;
    }

    public double getLobbySpawnX() {
        return lobbySpawnX;
    }

    public double getLobbySpawnY() {
        return lobbySpawnY;
    }

    public double getLobbySpawnZ() {
        return lobbySpawnZ;
    }

    public float getLobbySpawnYaw() {
        return lobbySpawnYaw;
    }

    public float getLobbySpawnPitch() {
        return lobbySpawnPitch;
    }

    public String getPugnaWorldName() {
        return pugnaWorldName;
    }

    public String getPugnaNetherWorldName() {
        return pugnaNetherWorldName;
    }

    public double getPugnaSpawnX() {
        return pugnaSpawnX;
    }

    public double getPugnaSpawnZ() {
        return pugnaSpawnZ;
    }

    public float getPugnaSpawnYaw() {
        return pugnaSpawnYaw;
    }

    public float getPugnaSpawnPitch() {
        return pugnaSpawnPitch;
    }

    public double getBorderBaseSize() {
        return borderBaseSize;
    }

    public double getBorderEndSize() {
        return borderEndSize;
    }

    public double getMaxBorderSize() {
        return maxBorderSize;
    }

    public double getBorderSizePerPlayer() {
        return borderSizePerPlayer;
    }

    public int getLobbyCountdownSeconds() {
        return lobbyCountdownSeconds;
    }

    public int getGameCountdownSeconds() {
        return gameCountdownSeconds;
    }

    public int getRestartCountdownSeconds() {
        return restartCountdownSeconds;
    }

    public int getNetherStartCountdownStartSeconds() {
        return netherStartCountdownStartSeconds;
    }

    public int getNetherStartSeconds() {
        return netherStartSeconds;
    }

    public int getBorderShrinkStartCountdownStartSeconds() {
        return borderShrinkStartCountdownStartSeconds;
    }

    public int getBorderShrinkStartSeconds() {
        return borderShrinkStartSeconds;
    }

    public int getBorderShrinkEndSeconds() {
        return borderShrinkEndSeconds;
    }

    public int getNetherEndCountdownStartSeconds() {
        return netherEndCountdownStartSeconds;
    }

    public int getNetherEndSeconds() {
        return netherEndSeconds;
    }

    public int getEnemyRevealCountdownStartSeconds() {
        return enemyRevealCountdownStartSeconds;
    }

    public int getEnemyRevealSeconds() {
        return enemyRevealSeconds;
    }

    public int getGameEndCountdownStartSeconds() {
        return gameEndCountdownStartSeconds;
    }

    public int getGameEndSeconds() {
        return gameEndSeconds;
    }

    public String getTeamSelectionItemName() {
        return teamSelectionItemName;
    }

    public String getNavigationItemName() {
        return navigationItemName;
    }

    public String getLeaveItemName() {
        return leaveItemName;
    }

    /* === Operations === */

    /* === Setup === */

    public void setup() {
        development = configuration.getBoolean("game.development", false);
        Object initialDevelopmentSeed = configuration.get("game.development_seed");
        if (initialDevelopmentSeed instanceof Number) {
            developmentSeed = ((Number) initialDevelopmentSeed).longValue();
        } else if (initialDevelopmentSeed instanceof String) {
            try {
                developmentSeed = Long.parseLong((String) initialDevelopmentSeed);
            } catch (NumberFormatException exception) {
                developmentSeed = null;
            }
        } else {
            developmentSeed = null;
        }

        friendlyFire = configuration.getBoolean("players.friendly_fire", false);
        maxTeamsCount = Math.max(4, Math.min(64, configuration.getInt("players.max_teams_count", 16)));

        int initialCapacity = configuration.getInt("players.max_team_capacity", 1);
        maxTeamCapacity = (initialCapacity < maxTeamsCount && maxTeamsCount % initialCapacity == 0) ? initialCapacity : 1;

        maxPlayersCount = maxTeamsCount * maxTeamCapacity;
        minPlayersCount = Math.min(maxPlayersCount, Math.max(configuration.getInt("players.min_players_count", 2), maxTeamCapacity + 1));

        defaultTeamNames = configuration.getStringList("players.default_team_names");
        Collections.shuffle(defaultTeamNames);

        lobbyWorldName = configuration.getString("lobby.world", "lobby");
        lobbySpawnX = configuration.getDouble("lobby.spawn.x", 0.5);
        lobbySpawnY = Math.max(0.0, Math.min(256.0, configuration.getDouble("lobby.spawn.y", 64.0)));
        lobbySpawnZ = configuration.getDouble("lobby.spawn.z", 0.5);
        lobbySpawnYaw = (float) configuration.getDouble("lobby.spawn.yaw", 0.0);
        lobbySpawnPitch = (float) configuration.getDouble("lobby.spawn.pitch", 0.0);

        pugnaWorldName = configuration.getString("pugna.world", "pugna");
        pugnaNetherWorldName = configuration.getString("pugna.nether_world", "pugna_nether");
        pugnaSpawnX = configuration.getDouble("pugna.spawn.x", 0.5);
        pugnaSpawnZ = configuration.getDouble("pugna.spawn.z", 0.5);
        pugnaSpawnYaw = (float) configuration.getDouble("pugna.spawn.yaw", 0.0);
        pugnaSpawnPitch = (float) configuration.getDouble("pugna.spawn.pitch", 0.0);

        borderBaseSize = configuration.getDouble("border.base_size", 2048.0);
        borderEndSize = configuration.getDouble("border.end_size", 256.0);
        maxBorderSize = configuration.getDouble("border.max_size", 4096.0);
        borderSizePerPlayer = configuration.getDouble("border.size_per_player", 64.0);

        lobbyCountdownSeconds = Math.max(0, configuration.getInt("countdowns.lobby.seconds", 60));
        gameCountdownSeconds = Math.max(0, configuration.getInt("countdowns.game.seconds", 40));
        restartCountdownSeconds = Math.max(0, configuration.getInt("countdowns.restart.seconds", 25));

        netherStartCountdownStartSeconds = Math.max(0, configuration.getInt("events.nether_start.countdown_start_seconds", 3600));
        netherStartSeconds = Math.max(netherStartCountdownStartSeconds, configuration.getInt("events.nether_start.event_seconds", 5400));

        borderShrinkStartCountdownStartSeconds = Math.max(netherStartSeconds, configuration.getInt("events.border_shrink.countdown_start_seconds", 12600));
        borderShrinkStartSeconds = Math.max(borderShrinkStartCountdownStartSeconds, configuration.getInt("events.border_shrink.event_start_seconds", 14400));
        borderShrinkEndSeconds = Math.max(borderShrinkStartSeconds, configuration.getInt("events.border_shrink.event_end_seconds", 36000));

        netherEndCountdownStartSeconds = Math.max(borderShrinkEndSeconds, configuration.getInt("events.nether_end.countdown_start_seconds", 41400));
        netherEndSeconds = Math.max(netherEndCountdownStartSeconds, configuration.getInt("events.nether_end.event_seconds", 43200));

        enemyRevealCountdownStartSeconds = Math.max(netherEndSeconds, configuration.getInt("events.enemy_reveal.countdown_start_seconds", 48600));
        enemyRevealSeconds = Math.max(enemyRevealCountdownStartSeconds, configuration.getInt("events.enemy_reveal.event_seconds", 50400));

        gameEndCountdownStartSeconds = Math.max(enemyRevealSeconds, configuration.getInt("events.game_end.countdown_start_seconds", 55800));
        gameEndSeconds = Math.max(gameEndCountdownStartSeconds, configuration.getInt("events.game_end.event_seconds", 57600));

        teamSelectionItemName = configuration.getString("item_names.team_selection_item", "§aTeam-Auswahl§r");
        navigationItemName = configuration.getString("item_names.navigation_item", "§6Navigation§r");
        leaveItemName = configuration.getString("item_names.leave_item", "§cVerlassen§r");
    }
}
