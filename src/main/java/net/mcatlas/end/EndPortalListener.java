package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import net.mcatlas.end.portal.EndPortalManager;
import net.mcatlas.end.world.EndWorld;
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
            String worldId = player.getLocation().getWorld().getName().replace(EndWorld.END_WORLD_PREFIX, "");

            endPlugin.getEndStorage().queryEndWorld(worldId).thenAccept(o -> {
                o.ifPresent(endWorld -> {
                    if (!endWorld.isDeleted()) {
                        EndPlayerLogout endPlayerLogout = new EndPlayerLogout(endWorld, player.getUniqueId(), System.currentTimeMillis());

                        endPlugin.getEndStorage().saveEndPlayerLogout(endPlayerLogout);
                    }
                });
            });
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World nextWorld = player.getWorld();
        World prevWorld = event.getFrom().getWorld();

        // Not changing worlds
        if (prevWorld.equals(nextWorld)) {
            return;
        }

        // Coming from an End world
        if (WorldUtil.isEndWorld(prevWorld)) {
            endPlugin.getEndStorage().deleteEndPlayerLogouts(player.getUniqueId());
        }

        // Going to an End world
        else if (WorldUtil.isEndWorld(nextWorld)) {
            if (!endPlugin.getEndPortalManager().portalActive()) {
                event.setCancelled(true);
            }

            EndPortal endPortal = endPlugin.getEndPortalManager().getCurrent();

            // Going to an End world that isn't the one the portal goes to
            if (!nextWorld.getName().equals(endPortal.getEndWorld().getWorldName())) {
                event.setCancelled(true);
            }
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
