package at.minecraft.pugna.teams;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {
    // TODO: Implement class
    private final int id;
    private final int capacity;
    private final List<UUID> players;
    private final List<Location> spawns;
    private String name;

    public Team(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.players = new ArrayList<>();
        this.spawns = new ArrayList<>();
        this.name = "Team" + id;
    }

    /* === Getters === */

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    /* === Operations === */

    public void remove(Player player) {
        // TODO: Implement method
    }

    public void leave(Player player) {
        // TODO: Implement method
    }
}
