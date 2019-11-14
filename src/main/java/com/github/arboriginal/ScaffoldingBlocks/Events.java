package com.github.arboriginal.ScaffoldingBlocks;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Scaffolding;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

class Events implements Listener {
    private Helpers h;
    private Plugin  p;

    // Constructors ----------------------------------------------------------------------------------------------------

    Events(Plugin plugin) {
        p = plugin;
        h = new Helpers(p);
    }

    // Listener methods ------------------------------------------------------------------------------------------------

    @EventHandler(ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (p.sneak && player.isSneaking()) return;
        if (!player.hasPermission("sb.alt_scaffoldings.break") || !h.holdTool(player.getInventory())) return;

        Block block = e.getBlock();
        if (h.locks.contains(h.lockKey(block)) || !h.isAlternative(block.getType())) return;

        e.setCancelled(true);
        h.breakScaffolding(block, player, block.getType());
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock(); // @formatter:off
        if (block.getType() != Material.SCAFFOLDING
            || !e.getPlayer().hasPermission("sb.vanilla_scaffoldings.bridge")) return;
        // @formatter:on
        Scaffolding bs = ((Scaffolding) block.getBlockData());
        if (!bs.isBottom() || bs.getDistance() < bs.getMaximumDistance()) return;

        String key = h.lockKey(block);
        h.locks.add(key);

        new BukkitRunnable() {
            @Override
            public void run() {
                h.locks.remove(key);
            }
        }.runTaskLater(p, 10);
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntityType() != EntityType.FALLING_BLOCK) return;
        if (((FallingBlock) e.getEntity()).getBlockData().getMaterial() != Material.SCAFFOLDING) return;

        Block block = e.getLocation().getBlock();

        if (h.locks.contains(h.lockKey(block))) e.setCancelled(true);
        else for (BlockFace face : h.cardinals) if (h.locks.contains(h.lockKey(block.getRelative(face)))) {
            e.setCancelled(true);
            break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        if ((p.sneak && player.isSneaking()) || !player.hasPermission("sb.alt_scaffoldings.place")) return;

        int len;

        BlockFace dir = e.getBlockFace();
        if (dir == BlockFace.UP) {
            dir = player.getFacing();
            len = 7;
        }
        else if (dir == BlockFace.DOWN || Arrays.asList(h.cardinals).contains(dir)) {
            dir = BlockFace.UP;
            len = player.getWorld().getMaxHeight();
        }
        else return;

        if (len < 1) return;

        PlayerInventory inv = player.getInventory();
        if (!h.holdTool(inv)) return;

        Block    block = e.getClickedBlock();
        Material type  = block.getType();
        if (!h.isAlternative(type)) return;

        ItemStack item = inv.getItemInMainHand();
        if (item == null || item.getType() != type) return;

        e.setCancelled(true);
        h.placeScaffolding(block, player, type, dir, len, item);
    }
}
