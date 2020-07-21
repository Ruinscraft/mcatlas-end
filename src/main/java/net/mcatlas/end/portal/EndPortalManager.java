package net.mcatlas.end.portal;

import net.mcatlas.end.EndWorld;
import net.mcatlas.end.WorldUtil;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class EndPortalManager {

    private EndPortal current;
    private World portalWorld;
    private int xBound;
    private int zBound;

    public EndPortalManager(World portalWorld, int xBound, int zBound) {
        this.portalWorld = portalWorld;
        this.xBound = xBound;
        this.zBound = zBound;
    }

    public EndPortal getCurrent() {
        return current;
    }

    public void setCurrent(EndPortal current) {
        this.current = current;
    }

    public boolean portalActive() {
        return current != null && current.isOpen();
    }

    public World getPortalWorld() {
        return portalWorld;
    }

    public int getxBound() {
        return xBound;
    }

    public int getzBound() {
        return zBound;
    }

    public EndPortal createRandom() {
        String worldId = UUID.randomUUID().toString().substring(0, 8);
        EndWorld endWorld = new EndWorld(worldId);
        Location randomLocation = WorldUtil.findUnclaimedLocation(portalWorld, xBound, zBound);
        EndPortal endPortal = new EndPortal(endWorld, randomLocation, 0L);

        WorldUtil.generateEndWorld(worldId);

        return current = endPortal;
    }

}
