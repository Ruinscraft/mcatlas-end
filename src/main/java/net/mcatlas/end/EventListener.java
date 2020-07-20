package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventListener implements Listener {

    private EndPlugin endPlugin;

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // if player is in end world, put last login time
        if (WorldUtil.isInEndWorld(player)) {
            endPlugin.getEndStorage().savePlayer(player, System.currentTimeMillis());
        }
    }

    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World to = player.getWorld();
        Location fromLocation = event.getFrom();
        World from = fromLocation.getWorld();

        if (to.equals(from)) {
            return;
        }

        if (WorldUtil.isEndWorld(from)) {
            endPlugin.getEndStorage().deletePlayer(player);
        } else if (WorldUtil.isEndWorld(to)) {
            EndPortal currentPortal = endPlugin.getEndPortalManager().getCurrent();

            // cancel if no portal, portal isnt open, player teleported to an end world the portal doesnt lead to
            if (currentPortal == null || !currentPortal.isOpen()) {
                event.setCancelled(true);
            }

            if (!currentPortal.getEnd().equals(to)) {
                event.setCancelled(true);
            }
        }
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!WorldUtil.isInOverworld(player)) {
            return;
        }

        EndPortal portal = endPlugin.getEndPortalManager().getCurrent();

        if (portal == null || !portal.isOpen()) {
            return;
        }

        // teleport to current end world if close to portal
        Location location = new Location(player.getWorld(), portal.getX(), player.getLocation().getY(), portal.getZ());
        double dist = location.distanceSquared(player.getLocation());

        if (dist < 36) { // 6 blocks
            player.teleport(portal.getEnd().getSpawnLocation());
        }
    }

}
