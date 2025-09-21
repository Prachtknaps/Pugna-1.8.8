package at.minecraft.pugna.teams;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Team {
    private final MessageConfig messageConfig;

    private final int id;
    private final int capacity;
    private final List<UUID> members;
    private final List<Location> spawns;
    private String name;

    public Team(int id, int capacity) {
        this.messageConfig = Pugna.getInstance().getMessageConfig();

        this.id = id;
        this.capacity = capacity;
        this.members = new ArrayList<>(capacity);
        this.spawns = new ArrayList<>(capacity);
    }

    /* === Getters === */

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    /* === Operations === */

    public void broadcast(String message) {
        for (UUID uuid : members) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }

    public void add(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        if (members.contains(player.getUniqueId()) || isFull()) {
            return;
        }

        String teamMessage = messageConfig.getChatMessage(Message.TEAM_JOIN_OTHERS).player(player.getName()).toString();
        broadcast(teamMessage);

        members.add(player.getUniqueId());
        String playerMessage = messageConfig.getChatMessage(Message.TEAM_ASSIGNED).team(name).toString();
        player.sendMessage(playerMessage);
    }

    public void add(UUID uuid) {
        if (members.contains(uuid) || isFull()) {
            return;
        }

        members.add(uuid);
    }

    public void remove(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        members.remove(player.getUniqueId());
    }

    public void join(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        if (members.contains(player.getUniqueId())) {
            String message = messageConfig.getMessage(Message.TEAM_JOIN_SAME_TEAM);
            player.sendMessage(message);
            return;
        }

        if (isFull()) {
            String message = messageConfig.getMessage(Message.TEAM_JOIN_FULL);
            player.sendMessage(message);
            return;
        }

        String teamMessage = messageConfig.getChatMessage(Message.TEAM_JOIN_OTHERS).player(player.getName()).toString();
        broadcast(teamMessage);

        members.add(player.getUniqueId());
        String playerMessage = messageConfig.getChatMessage(Message.TEAM_JOIN_SELF).team(name).toString();
        String hintMessage = messageConfig.getMessage(Message.TEAM_RENAME_HINT);
        player.sendMessage(playerMessage);
        player.sendMessage(hintMessage);
    }

    public void leave(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        members.remove(player.getUniqueId());
        String playerMessage = messageConfig.getChatMessage(Message.TEAM_LEAVE_SELF).team(name).toString();
        player.sendMessage(playerMessage);

        String teamMessage = messageConfig.getChatMessage(Message.TEAM_LEAVE_OTHERS).player(player.getName()).toString();
        broadcast(teamMessage);
    }

    public void rename(Player player, String name) {
        if (name.equalsIgnoreCase(this.name)) {
            if (player != null && player.isOnline()) {
                String message = messageConfig.getChatMessage(Message.TEAM_RENAME_SAME_NAME).team(name).toString();
                player.sendMessage(message);
            }
            return;
        }

        Team existingTeam = TeamUtils.getTeam(name);
        if (existingTeam != null && existingTeam.getId() != id) {
            if (player != null && player.isOnline()) {
                String message = messageConfig.getMessage(Message.TEAM_RENAME_TAKEN);
                player.sendMessage(message);
            }
            return;
        }

        if (!isValidName(name)) {
            if (player != null && player.isOnline()) {
                String message = messageConfig.getMessage(Message.TEAM_RENAME_INVALID);
                player.sendMessage(message);
            }
            return;
        }

        this.name = ChatColor.stripColor(name).trim();
        String message = messageConfig.getChatMessage(Message.TEAM_RENAME_SUCCESS).team(name).toString();
        broadcast(message);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpawns(List<Location> locations) {
        spawns.clear();
        if (locations != null) {
            spawns.addAll(locations);
        }

        Collections.shuffle(spawns);
    }

    public void teleport() {
        if (spawns.size() < members.size()) {
            String message = messageConfig.getRawPrefix() + " Could not teleport players of team '" + name + "': There are less spawns that players (" + spawns.size() + "/" + members.size() + ").";
            Bukkit.getLogger().warning(message);
            return;
        }

        for (int i = 0; i < members.size(); i++) {
            Player player = Bukkit.getPlayer(members.get(i));
            if (player == null || !player.isOnline()) {
                String message = messageConfig.getRawPrefix() + " Team '" + name + "': Player with UUID '" + members.get(i) + "' is null or offline and could not be teleported.";
                Bukkit.getLogger().warning(message);
                continue;
            }

            Location spawn = spawns.get(i);
            if (spawn == null) {
                String message = messageConfig.getRawPrefix() + " Team '" + name + "': Could not teleport player '" + player.getName() + "', because spawn is null.";
                Bukkit.getLogger().warning(message);
                continue;
            }

            player.teleport(spawn);

            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setArmorContents(new ItemStack[4]);

            String message = messageConfig.getMessage(Message.TEAM_TELEPORT);
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }

    /* === Helpers === */

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public boolean isFull() {
        return members.size() >= capacity;
    }

    public boolean hasPlayer(Player player) {
        if (player == null) {
            return false;
        }

        return members.contains(player.getUniqueId());
    }

    public boolean isValidName(String name) {
        if (name == null) {
            return false;
        }

        String plain = ChatColor.stripColor(name).trim();

        return plain.matches("^[A-Za-z0-9]{4,16}$");
    }
}
