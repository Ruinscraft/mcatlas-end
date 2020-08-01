package net.mcatlas.end;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class EndWorldListener implements Listener {

    private final EndPlugin endPlugin;

    public EndWorldListener(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @EventHandler
    public void onPlayerFallIntoVoid(PlayerMoveEvent event) {
        Location location = event.getTo();
        if (location.getBlockY() > 1) {
            return;
        }
        if (location.getWorld().getEnvironment() != World.Environment.THE_END) {
            return;
        }

        World overworld = endPlugin.getEndPortalManager().getPortalWorld();

        int x = location.getBlockX();
        int z = location.getBlockZ();
        int xMax = endPlugin.getEndPortalManager().getXBound();
        int zMax = endPlugin.getEndPortalManager().getZBound();

        Player player = event.getPlayer();

        if (x > xMax) {
            x = xMax - 150;
        } else if (-1 * x < -1 * xMax) {
            x = (-1 * xMax) + 150;
        }

        if (z > zMax) {
            z = zMax - 150;
        } else if (-1 * z < -1 * zMax) {
            z = (-1 * zMax) + 150;
        }

        player.teleport(new Location(overworld, x, 400, z));
    }

    private final List<Material> ALLOWED_NEAR_END_SPAWN;

    {
        ALLOWED_NEAR_END_SPAWN = new ArrayList<>();

        ALLOWED_NEAR_END_SPAWN.add(Material.SIGN);
        ALLOWED_NEAR_END_SPAWN.add(Material.WALL_SIGN);
        ALLOWED_NEAR_END_SPAWN.add(Material.TORCH);
        ALLOWED_NEAR_END_SPAWN.add(Material.WALL_TORCH);
        ALLOWED_NEAR_END_SPAWN.add(Material.DRAGON_EGG);
        ALLOWED_NEAR_END_SPAWN.add(Material.DRAGON_HEAD);
        ALLOWED_NEAR_END_SPAWN.add(Material.DRAGON_WALL_HEAD);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();

        if (world.getEnvironment() != World.Environment.THE_END) {
            return;
        }

        Location blockLocation = block.getLocation();

        double dist = Math.sqrt(Math.pow((0 - blockLocation.getBlockX()), 2) + Math.pow((0 - blockLocation.getBlockZ()), 2));

        if (dist < 16) {
            for (Material allowed : ALLOWED_NEAR_END_SPAWN) {
                if (block.getType() == allowed) {
                    return;
                }
            }

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot build near the portal.");

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();

        if (world.getEnvironment() != World.Environment.THE_END) {
            return;
        }

        Location blockLocation = block.getLocation();

        double dist = Math.sqrt(Math.pow((0 - blockLocation.getBlockX()), 2) + Math.pow((0 - blockLocation.getBlockZ()), 2));

        if (dist < 16) {
            for (Material allowed : ALLOWED_NEAR_END_SPAWN) {
                if (block.getType() == allowed) {
                    return;
                }
            }

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot build near the portal.");

            event.setCancelled(true);
        }
    }

}
