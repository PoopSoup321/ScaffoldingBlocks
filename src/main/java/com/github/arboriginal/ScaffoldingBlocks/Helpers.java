package com.github.arboriginal.ScaffoldingBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

class Helpers {
    final BlockFace[] cardinals = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    HashSet<String>   locks     = new HashSet<String>();

    private Plugin p;

    // Constructors ----------------------------------------------------------------------------------------------------

    Helpers(Plugin plugin) {
        p = plugin;
    }

    // Package methods -------------------------------------------------------------------------------------------------

    void breakScaffolding(Block origin, Player player, Material type) {
        World world = origin.getWorld();

        Collection<ItemStack> drops = p.silk ? Arrays.asList(new ItemStack(type)) : origin.getDrops();
        origin.setType(Material.AIR);

        Location place;

        if (p.fall) place = origin.getLocation();
        else {
            place = player.getLocation();
            ArrayList<ItemStack> remains = new ArrayList<ItemStack>();
            for (ItemStack drop : drops) remains.addAll(player.getInventory().addItem(drop).values());
            drops = remains;
        }

        if (!drops.isEmpty()) for (ItemStack drop : drops) world.dropItem(place, drop);

        for (int i = 1; i < world.getMaxHeight() - origin.getY(); i++) {
            Block block = origin.getRelative(BlockFace.UP, i);
            if (propagable(player, block, type, BlockFace.UP)) breakScaffolding(block, player, type);
            else break;
        }

        for (BlockFace dir : cardinals) for (int i = 1; i < world.getViewDistance() * 16; i++) {
            Block block = origin.getRelative(dir, i);
            if (propagable(player, block, type, dir)) breakScaffolding(block, player, type);
            else break;
        }
    }

    boolean holdTool(PlayerInventory inv) {
        return p.tools.contains(inv.getItemInOffHand().getType().toString());
    }

    boolean isAlternative(Material type) {
        return p.alts.isEmpty() || p.alts.contains(type.toString());
    }

    String lockKey(Block block) {
        return block.getLocation().toString();
    }

    void placeScaffolding(Block origin, Player player, Material type, BlockFace dir, int max, ItemStack item) {
        Block previous = origin;

        if (dir == BlockFace.UP) max -= origin.getY();

        for (int i = 1; i < max; i++) {
            Block adjacent = origin.getRelative(dir, i);

            if (adjacent.getType() != type) {
                if (p.nulls.contains(adjacent.getType().toString())) {
                    BlockPlaceEvent event = new BlockPlaceEvent(adjacent,
                            adjacent.getState(), adjacent.getRelative(dir), item, player, true, EquipmentSlot.HAND);

                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) return;

                    adjacent.setType(type);
                    adjacent.setBlockData(previous.getBlockData());

                    if (player.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount() - 1);
                }

                return;
            }

            previous = adjacent;
        }
    }

    // Private methods -------------------------------------------------------------------------------------------------

    private boolean isAttached(Block block, Material type, BlockFace dir) {
        if (dir == BlockFace.UP) return block.getRelative(BlockFace.DOWN).getType().isSolid();

        BlockFace opposite = dir.getOppositeFace();

        for (BlockFace face : cardinals) if (face != opposite) for (int i = 1; i < 7; i++) {
            Block adjacent = block.getRelative(face, i);
            if (adjacent.getType() != type) break;
            if (adjacent.getRelative(BlockFace.DOWN).getType().isSolid()) return true;
        }

        return false;
    }

    private boolean propagable(Player player, Block block, Material type, BlockFace dir) {
        if (block.getType() != type || isAttached(block, type, dir)) return false;

        BlockBreakEvent event = new BlockBreakEvent(block, player);
        String          key   = lockKey(block);

        locks.add(key);
        Bukkit.getPluginManager().callEvent(event);
        locks.remove(key);

        return !event.isCancelled();
    }
}
