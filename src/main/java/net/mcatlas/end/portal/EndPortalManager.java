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
    private long openTimeMillis;

    public EndPortalManager(World portalWorld, int xBound, int zBound, long openTimeMillis) {
        this.portalWorld = portalWorld;
        this.xBound = xBound;
        this.zBound = zBound;
        this.openTimeMillis = openTimeMillis;
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
        long closeTime = System.currentTimeMillis() + openTimeMillis;
        EndPortal endPortal = new EndPortal(endWorld, randomLocation, closeTime);

        WorldUtil.generateEndWorld(worldId);

        return current = endPortal;
    }

}
