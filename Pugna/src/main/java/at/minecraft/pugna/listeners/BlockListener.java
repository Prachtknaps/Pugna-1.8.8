package at.minecraft.pugna.listeners;

import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.utils.BlockUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.material.Dispenser;

public class BlockListener implements Listener {
    private final GameManager gameManager;

    public BlockListener(GameManager gameManager) {
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

        if (BlockUtils.isProtectedBlock(event.getBlock()) || BlockUtils.isSafeArea(event.getBlock().getLocation())) {
            player.sendMessage(ChatConfig.getMessage(Message.NETHER_PORTAL_BLOCK_MODIFICATION));
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
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

        if (BlockUtils.isProtectedBlock(event.getBlock()) || BlockUtils.isSafeArea(event.getBlock().getLocation())) {
            player.sendMessage(ChatConfig.getMessage(Message.NETHER_PORTAL_BLOCK_MODIFICATION));
            event.setCancelled(true);
            return;
        }

        if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
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
        if (BlockUtils.isSafeArea(fromBlock.getLocation()) || BlockUtils.isSafeArea(toBlock.getLocation())) {
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

        if (state != GameState.GAME_RUNNING && state != GameState.GAME_RESTARTING) {
            event.setCancelled(true);
            return;
        }

        Location location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
        if (BlockUtils.isSafeArea(location)) {
            String message = ChatConfig.getMessage(Message.NETHER_PORTAL_BLOCK_MODIFICATION);
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

        if (BlockUtils.isSafeArea(targetBlock.getLocation())) {
            event.setCancelled(true);
        }
    }
}
