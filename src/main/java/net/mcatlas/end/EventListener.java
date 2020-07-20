package net.mcatlas.end;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventListener implements Listener {

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // if player is in end world, put last login time
        if (EndPlugin.isInEndWorld(player)) {
            EndPlugin.get().getStorage().savePlayer(player.getUniqueId().toString(),
                    player.getWorld().getName(), System.currentTimeMillis());
        }
    }

    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World to = player.getWorld();
        Location fromLocation = event.getFrom();
        World from = fromLocation.getWorld();

        if (to.getName().equals(from.getName())) return;

        if (EndPlugin.isEndWorld(from)) {
            EndPlugin.get().getStorage().removePlayer(event.getPlayer().getUniqueId().toString());
            return;
        }

        if (EndPlugin.isEndWorld(to)) {
            EndPortal currentPortal = EndPlugin.get().getCurrentPortal();
            if (currentPortal == null || !currentPortal.isOpen()) {
                event.setCancelled(true);
                return;
            }
            if (!currentPortal.getEndWorldName().equals(to.getName())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!EndPlugin.isInOverworld(player)) return;

        EndPortal portal = EndPlugin.get().getCurrentPortal();
        if (portal == null) return;

        Location location = new Location(player.getWorld(), portal.getX(), player.getLocation().getY(), portal.getZ());
        double dist = location.distanceSquared(player.getLocation());
        if (dist < 36) { // 6 blocks
            World world = Bukkit.getWorld(portal.getEndWorldName());
            player.teleport(world.getSpawnLocation());
        }
    }

}
