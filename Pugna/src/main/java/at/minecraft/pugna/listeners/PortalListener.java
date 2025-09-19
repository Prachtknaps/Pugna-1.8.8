package at.minecraft.pugna.listeners;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.ArrayList;
import java.util.List;

public class PortalListener implements Listener {
    private final WorldManager worldManager;
    private final GameManager gameManager;

    private boolean addedProtectedAreas = false;

    public PortalListener(WorldManager worldManager, GameManager gameManager) {
        this.worldManager = worldManager;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if (!gameManager.allowNether()) {
            String message = MessageConfig.getMessage(Message.NETHER_TELEPORT_NOT_ALLOWED);
            player.sendMessage(message);
            event.setCancelled(true);
            return;
        }

        World fromWorld = event.getFrom().getWorld();
        if (fromWorld == null) {
            event.setCancelled(true);
            return;
        }

        if (fromWorld.getName().equals(GameConfig.getPugnaWorldName())) {
            World target = worldManager.getPugnaNetherWorld();
            if (target == null) {
                event.setCancelled(true);
                return;
            }

            TravelAgent travelAgent = event.getPortalTravelAgent();
            travelAgent.setCanCreatePortal(true);
            travelAgent.setCreationRadius(16);

            Location targetLocation = travelAgent.findOrCreate(worldManager.getPugnaNetherSpawnLocation());
            int portalX = targetLocation.getBlockX();
            int portalY = targetLocation.getBlockY();
            int portalZ = targetLocation.getBlockZ();

            if (!addedProtectedAreas) {
                List<Location> protectedAreas = new ArrayList<>();

                for (int x = portalX - 4; x <= portalX + 4; x++) {
                    for (int y = portalY - 1; y <= portalY + 4; y++) {
                        for (int z = portalZ - 4; z <= portalZ + 4; z++) {
                            Location location = new Location(target, x, y, z);
                            protectedAreas.add(location);
                        }
                    }
                }

                GameConfig.saveProtectedAreas(protectedAreas);
                addedProtectedAreas = true;
            }

            event.useTravelAgent(true);
            event.setTo(travelAgent.findOrCreate(targetLocation));
            return;
        }

        if (fromWorld.getName().equals(GameConfig.getPugnaNetherWorldName())) {
            World target = worldManager.getPugnaWorld();
            if (target == null) {
                event.setCancelled(true);
                return;
            }

            TravelAgent travelAgent = event.getPortalTravelAgent();
            travelAgent.setCanCreatePortal(true);
            travelAgent.setCreationRadius(16);

            Location targetLocation = travelAgent.findOrCreate(worldManager.getPugnaSpawnLocation());

            event.useTravelAgent(true);
            event.setTo(targetLocation);
            return;
        }

        event.setCancelled(true);
    }
}
