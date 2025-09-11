package at.minecraft.pugna.listeners;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.utils.BlockUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalListener implements Listener {
    private final WorldManager worldManager;
    private final GameManager gameManager;

    public PortalListener(WorldManager worldManager, GameManager gameManager) {
        this.worldManager = worldManager;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if (!gameManager.allowNether()) {
            String message = ChatConfig.getMessage(Message.NETHER_TELEPORT_NOT_ALLOWED);
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
            World target = worldManager.getPugnaNetherWorldSpawn().getWorld();
            if (target == null) {
                event.setCancelled(true);
                return;
            }

            TravelAgent travelAgent = event.getPortalTravelAgent();
            travelAgent.setCanCreatePortal(true);
            travelAgent.setCreationRadius(16);

            Location targetLocation = travelAgent.findOrCreate(worldManager.getPugnaNetherWorldSpawn());
            int portalX = targetLocation.getBlockX();
            int portalY = targetLocation.getBlockY();
            int portalZ = targetLocation.getBlockZ();

            for (int x = portalX - 4; x <= portalX + 4; x++) {
                for (int y = portalY - 1; y <= portalY + 4; y++) {
                    for (int z = portalZ - 4; z <= portalZ + 4; z++) {
                        Location location = new Location(target, x, y, z);
                        BlockUtils.addSafeArea(location);
                    }
                }
            }

            event.useTravelAgent(true);
            event.setTo(travelAgent.findOrCreate(targetLocation));
            return;
        }

        if (fromWorld.getName().equals(GameConfig.getPugnaNetherWorldName())) {
            World target = worldManager.getPugnaWorldSpawn().getWorld();
            if (target == null) {
                event.setCancelled(true);
                return;
            }

            TravelAgent travelAgent = event.getPortalTravelAgent();
            travelAgent.setCanCreatePortal(true);
            travelAgent.setCreationRadius(16);

            Location targetLocation = travelAgent.findOrCreate(worldManager.getPugnaWorldSpawn());

            event.useTravelAgent(true);
            event.setTo(targetLocation);
            return;
        }

        event.setCancelled(true);
    }
}
