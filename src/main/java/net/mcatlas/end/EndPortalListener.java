package net.mcatlas.end;

import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.mcatlas.end.portal.EndPortal;
import net.mcatlas.end.world.EndWorld;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EndPortalListener implements Listener {

    private final EndPlugin endPlugin;

    public EndPortalListener(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @EventHandler
    public void onTownPreClaim(TownPreClaimEvent event) {
        if (!endPlugin.getEndPortalManager().portalActive()) {
            return;
        }

        World portalWorld = endPlugin.getEndPortalManager().getPortalWorld();
        TownBlock townBlock = event.getTownBlock();

        // The town claim wasn't in the portal world
        if (!portalWorld.getName().equals(townBlock.getWorld().getName())) {
            return;
        }

        Location portalLoc = endPlugin.getEndPortalManager().findPortalBukkitLocation().get();

        Chunk townBlockChunk = portalWorld.getChunkAt(townBlock.getX(), townBlock.getZ());
        Chunk portalChunk = portalLoc.getChunk();

        int x1 = townBlockChunk.getX();
        int x2 = portalChunk.getX();
        int z1 = townBlockChunk.getZ();
        int z2 = portalChunk.getZ();
        double dist = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((z1 - z2), 2));

        // Close than 2 chunks? Cancel
        if (dist < 2) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot claim near an active portal.");
        }
    }

    @EventHandler
    public void onEnderSignalSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.ENDER_SIGNAL) {
            return;
        }

        Location target;

        if (endPlugin.getEndPortalManager().portalActive()) {
            target = endPlugin.getEndPortalManager().findPortalBukkitLocation().get();
        } else {
            target = event.getLocation().clone();
            target.setY(0); // Basically, point strait down
        }

        EnderSignal enderSignal = (EnderSignal) event.getEntity();

        enderSignal.setTargetLocation(target);
    }

    @EventHandler // this handles the vanilla End Portals (end world->over world)
    public void onEndPortalEnter(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getBedSpawnLocation() != null) {
            player.teleport(player.getBedSpawnLocation());
        } else {
            World portalWorld = endPlugin.getEndPortalManager().getPortalWorld();

            player.teleport(portalWorld.getSpawnLocation());
        }
    }

    @EventHandler // prevent traps/griefing around the portal
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();

        // Not in portal world
        if (!location.getWorld().equals(endPlugin.getEndPortalManager().getPortalWorld())) {
            return;
        }

        if (endPlugin.getEndPortalManager().isInPortalArea(location)) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot build near the portal.");
        }
    }

    @EventHandler // prevent traps/griefing around the portal
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        // Not in portal world
        if (!location.getWorld().equals(endPlugin.getEndPortalManager().getPortalWorld())) {
            return;
        }

        if (endPlugin.getEndPortalManager().isInPortalArea(location)) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot build near the portal.");
        }
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
                return;
            }

            EndPortal endPortal = endPlugin.getEndPortalManager().getCurrent();

            // Going to an End world that isn't the one the portal goes to
            if (!nextWorld.getName().equals(endPortal.getEndWorld().getWorldName())) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
