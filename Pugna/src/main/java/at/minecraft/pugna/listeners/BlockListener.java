package at.minecraft.pugna.listeners;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.BlockUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.material.Dispenser;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Iterator;

public class BlockListener implements Listener {
    private final MessageConfig messageConfig;
    private final GameManager gameManager;

    public BlockListener(MessageConfig messageConfig, GameManager gameManager) {
        this.messageConfig = messageConfig;
        this.gameManager = gameManager;
    }

    /* === Events === */

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GameState state = gameManager.getState();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (BlockUtils.isProtectedArea(event.getBlock().getLocation())) {
            player.sendMessage(messageConfig.getMessage(Message.NETHER_PORTAL_BLOCK_MODIFICATION_NOT_ALLOWED));
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        GameState state = gameManager.getState();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (BlockUtils.isProtectedArea(event.getBlock().getLocation())) {
            player.sendMessage(messageConfig.getMessage(Message.NETHER_PORTAL_BLOCK_MODIFICATION_NOT_ALLOWED));
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLavaFlow(BlockFromToEvent event) {
        Block fromBlock = event.getBlock();
        if (fromBlock.getType() != Material.LAVA && fromBlock.getType() != Material.STATIONARY_LAVA) {
            return;
        }

        Block toBlock = event.getToBlock();
        if (BlockUtils.isProtectedArea(toBlock.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLavaPlace(PlayerBucketEmptyEvent event) {
        if (event.getBucket() != Material.LAVA_BUCKET) {
            return;
        }

        GameState state = gameManager.getState();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (PlayerUtils.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.RESTARTING) {
            event.setCancelled(true);
            return;
        }

        Location location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
        if (BlockUtils.isProtectedArea(location)) {
            String message = messageConfig.getMessage(Message.NETHER_PORTAL_BLOCK_MODIFICATION_NOT_ALLOWED);
            player.sendMessage(message);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (event.getItem().getType() != Material.LAVA_BUCKET) {
            return;
        }

        Block block = event.getBlock();
        if (block.getType() != Material.DISPENSER) {
            return;
        }

        Dispenser data = (Dispenser) block.getState().getData();
        BlockFace facing = data.getFacing();
        Block targetBlock = block.getRelative(facing);

        if (BlockUtils.isProtectedArea(targetBlock.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGhastExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Fireball)) {
            return;
        }

        ProjectileSource shooter = ((Fireball) event.getEntity()).getShooter();
        if (!(shooter instanceof Ghast)) {
            return;
        }

        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();

            if (block.getType() == Material.PORTAL || block.getType() == Material.OBSIDIAN) {
                iterator.remove();
                continue;
            }

            if (BlockUtils.isProtectedArea(block.getLocation())) {
                iterator.remove();
            }
        }
    }
}
