package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import net.mcatlas.end.portal.EndPortalManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EndPortalListener implements Listener {

    private final EndPlugin endPlugin;

    public EndPortalListener(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // if player is in end world, put last login time
        if (WorldUtil.isInEndWorld(player)) {
            endPlugin.getEndStorage().queryEndWorlds().thenAccept(result -> {
                EndWorld currentEndWorld = null;

                for (EndWorld endWorld : result) {
                    if (player.getWorld().getName().contains(endWorld.getId())) {
                        currentEndWorld = endWorld;
                    }
                }

                if (currentEndWorld != null) {
                    EndPlayerLogout endPlayerLogout = new EndPlayerLogout(currentEndWorld, player.getUniqueId(), System.currentTimeMillis());

                    endPlugin.getEndStorage().saveEndPlayerLogout(endPlayerLogout);
                }
            });
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World nextWorld = player.getWorld();
        World prevWorld = event.getFrom().getWorld();

        // not changing worlds
        if (prevWorld.equals(nextWorld)) {
            return;
        }

        // coming from an End world
        if (WorldUtil.isEndWorld(prevWorld)) {
            endPlugin.getEndStorage().deleteEndPlayerLogouts(player.getUniqueId());
        } else if (WorldUtil.isEndWorld(nextWorld)) {
            EndPortal currentPortal = endPlugin.getEndPortalManager().getCurrent();

            // cancel if no portal, portal isnt open, player teleported to an end world the portal doesnt lead to
            if (currentPortal == null || !currentPortal.isOpen()) {
                event.setCancelled(true);
            }

            currentPortal.getEndWorld().findBukkitWorld().ifPresent(portalEndWorld -> {
                if (!portalEndWorld.equals(nextWorld)) {
                    event.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    public void checkForEndPortal(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        EndPortalManager endPortalManager = endPlugin.getEndPortalManager();

        // if not in the world where portals are active
        if (!player.getWorld().equals(endPortalManager.getPortalWorld())) {
            return;
        }

        // if there's no portal active
        if (!endPortalManager.portalActive()) {
            return;
        }

        EndPortal portal = endPortalManager.getCurrent();

        // teleport to current end world if close to portal
        Location location = new Location(player.getWorld(), portal.getX(), player.getLocation().getY(), portal.getZ());
        double dist = location.distanceSquared(player.getLocation());

        if (dist < 36) { // 6 blocks
            portal.getEndWorld().findBukkitWorld().ifPresent(endBukkitWorld -> {
                player.teleport(endBukkitWorld.getSpawnLocation());
            });
        }
    }

}
