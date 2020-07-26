package net.mcatlas.end;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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

}
