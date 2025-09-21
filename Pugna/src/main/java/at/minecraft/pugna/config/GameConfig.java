package at.minecraft.pugna.config;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameConfig {
    private final Pugna plugin;
    private final String fileName = "game.yml";

    private final File configFile;
    private FileConfiguration configuration;

    public GameConfig(final Pugna plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), this.fileName);
    }

    /* === Getters === */

    public FileConfiguration getConfig() {
        if (this.configuration == null) {
            reload();
        }

        return this.configuration;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isRunning() {
        return configuration.getBoolean("game.running", false);
    }

    public int getSeconds() {
        return Math.max(0, configuration.getInt("game.seconds", 0));
    }

    public int getTime() {
        return Math.max(0, configuration.getInt("game.time", 0));
    }

    public boolean killedFriedolin() {
        return configuration.getBoolean("game.killed_friedolin", false);
    }

    public boolean killedBerta() {
        return configuration.getBoolean("game.killed_berta", false);
    }

    public boolean foundDiamonds() {
        return configuration.getBoolean("game.found_diamonds", false);
    }

    public List<Team> getTeams() {
        int maxTeamCapacity = Pugna.getInstance().getPugnaConfig().getMaxTeamCapacity();
        List<Team> teams = new ArrayList<>();

        ConfigurationSection section = configuration.getConfigurationSection("teams");
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

            String name = configuration.getString("teams." + key + ".name");
            if (name == null || name.isEmpty()) {
                continue;
            }

            List<String> uuidStrings = configuration.getStringList("teams." + key + ".players");
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

    public List<Location> getProtectedAreas() {
        List<Location> locations = new ArrayList<>();

        ConfigurationSection section = configuration.getConfigurationSection("world.protected_areas");
        if (section == null) {
            return locations;
        }

        for (String key : section.getKeys(false)) {
            String worldName = configuration.getString("world.protected_areas." + key + ".world");
            if (worldName == null || worldName.isEmpty()) {
                continue;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }

            int x = configuration.getInt("world.protected_areas." + key + ".x");
            int y = configuration.getInt("world.protected_areas." + key + ".y");
            int z = configuration.getInt("world.protected_areas." + key + ".z");

            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }

    public double getInitialBorderSize() {
        return Math.max(0, configuration.getDouble("world.initial_border_size", 0));
    }

    public double getCurrentBorderSize() {
        return Math.max(0, configuration.getDouble("world.current_border_size", 0));
    }

    /* === Operations === */

    public void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder.");
        }

        if (!configFile.exists()) {
            try {
                plugin.saveResource(this.fileName, false);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().severe("Default resource '" + this.fileName + "' not found in JAR. Did you add it to src/main/resources/?");
            }
        }
    }

    public void save() {
        if (this.configuration == null) {
            return;
        }

        try {
            this.configuration.save(this.configFile);
        } catch (Exception exception) {
            plugin.getLogger().severe("Could not save '" + this.fileName + "': " + exception.getMessage());
        }
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);

        try (InputStream defaultStream = plugin.getResource(this.fileName)) {
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );

                this.configuration.setDefaults(defaultConfig);
                this.configuration.options().copyDefaults(true);
                this.configuration.options().copyHeader(true);
                save();
            } else {
                plugin.getLogger().fine("No default resource found for '" + this.fileName + "'.");
            }
        } catch (Exception exception) {
            plugin.getLogger().severe("Could not reload '" + this.fileName + "': " + exception.getMessage());
        }
    }

    public void reset() {
        saveRunningState(false);
        saveSeconds(0);
        saveTime(0L);
        saveKilledFriedolin(false);
        saveKilledBerta(false);
        saveFoundDiamonds(false);
        clearProtectedAreas();
        clearTeams();
        saveInitialBorderSize(0);
        saveCurrentBorderSize(0);
    }

    public void saveRunningState(boolean running) {
        configuration.set("game.running", running);
        save();
    }

    public void saveSeconds(int seconds) {
        configuration.set("game.seconds", seconds);
        save();
    }

    public void saveTime(long time) {
        configuration.set("game.time", time);
        save();
    }

    public void saveKilledFriedolin(boolean killedFriedolin) {
        configuration.set("game.killed_friedolin", killedFriedolin);
        save();
    }

    public void saveKilledBerta(boolean killedBerta) {
        configuration.set("game.killed_berta", killedBerta);
        save();
    }

    public void saveFoundDiamonds(boolean foundDiamonds) {
        configuration.set("game.found_diamonds", foundDiamonds);
        save();
    }

    public void saveTeams(List<Team> teams) {
        configuration.set("teams", null);

        for (Team team : teams) {
            int id = team.getId();
            String name = team.getName();

            configuration.set("teams." + id + ".name", name);
            List<String> uuidStrings = new ArrayList<>();
            for (UUID uuid : team.getMembers()) {
                uuidStrings.add(uuid.toString());
            }

            configuration.set("teams." + id + ".players", uuidStrings);
        }

        save();
    }

    private void clearTeams() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("teams", null);
        save();
    }

    public void saveProtectedArea(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }

        FileConfiguration configuration = Pugna.getInstance().getConfig();

        String key = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();

        configuration.set("world.protected_areas." + key + ".world", location.getWorld().getName());
        configuration.set("world.protected_areas." + key + ".x", location.getBlockX());
        configuration.set("world.protected_areas." + key + ".y", location.getBlockY());
        configuration.set("world.protected_areas." + key + ".z", location.getBlockZ());

        save();
        BlockUtils.reloadProtectedAreasCache();
    }

    public void saveProtectedAreas(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return;
        }

        FileConfiguration configuration = Pugna.getInstance().getConfig();

        for (Location location : locations) {
            if (location == null || location.getWorld() == null) {
                continue;
            }

            String key = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();

            configuration.set("world.protected_areas." + key + ".world", location.getWorld().getName());
            configuration.set("world.protected_areas." + key + ".x", location.getBlockX());
            configuration.set("world.protected_areas." + key + ".y", location.getBlockY());
            configuration.set("world.protected_areas." + key + ".z", location.getBlockZ());
        }

        save();
        BlockUtils.reloadProtectedAreasCache();
    }

    private void clearProtectedAreas() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();
        configuration.set("world.protected_areas", null);
        save();
        BlockUtils.reloadProtectedAreasCache();
    }

    public void saveInitialBorderSize(double size) {
        configuration.set("world.initial_border_size", size);
        save();
    }

    public void saveCurrentBorderSize(double size) {
        configuration.set("world.current_border_size", size);
        save();
    }
}
