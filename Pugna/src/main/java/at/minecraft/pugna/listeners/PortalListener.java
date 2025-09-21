package at.minecraft.pugna.listeners;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.utils.NetherUtils;
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
    private final PugnaConfig pugnaConfig;
    private final MessageConfig messageConfig;
    private final GameConfig gameConfig;
    private final WorldManager worldManager;

    private boolean addedProtectedAreas = false;

    public PortalListener(PugnaConfig pugnaConfig, MessageConfig messageConfig, GameConfig gameConfig, WorldManager worldManager) {
        this.pugnaConfig = pugnaConfig;
        this.messageConfig = messageConfig;
        this.gameConfig = gameConfig;
        this.worldManager = worldManager;
    }

    /* === Events === */

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if (!NetherUtils.allowNether()) {
            String message = messageConfig.getMessage(Message.NETHER_TELEPORT_NOT_ALLOWED);
            player.sendMessage(message);
            event.setCancelled(true);
            return;
        }

        World fromWorld = event.getFrom().getWorld();
        if (fromWorld == null) {
            event.setCancelled(true);
            return;
        }

        if (fromWorld.getName().equals(pugnaConfig.getPugnaWorldName())) {
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

                gameConfig.saveProtectedAreas(protectedAreas);
                addedProtectedAreas = true;
            }

            event.useTravelAgent(true);
            event.setTo(travelAgent.findOrCreate(targetLocation));
            return;
        }

        if (fromWorld.getName().equals(pugnaConfig.getPugnaNetherWorldName())) {
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
