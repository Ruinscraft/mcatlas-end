package net.mcatlas.end.portal;

import net.mcatlas.end.EndWorld;
import net.mcatlas.end.WorldUtil;
import net.mcatlas.end.storage.EndStorage;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class EndPortalManager {

    private EndStorage endStorage;
    private EndPortal current;
    private World portalWorld;
    private int xBound;
    private int zBound;
    private long openTimeMillis;

    public EndPortalManager(EndStorage endStorage, World portalWorld, int xBound, int zBound, long openTimeMillis) {
        this.endStorage = endStorage;
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

    public EndPortal create(Location location) {
        String worldId = UUID.randomUUID().toString().substring(0, 8);
        EndWorld endWorld = new EndWorld(worldId);
        long closeTime = System.currentTimeMillis() + openTimeMillis;
        EndPortal endPortal = new EndPortal(endWorld, location, closeTime);

        // create world
        WorldUtil.createEndWorld(worldId);

        // save new world then the new portal to storage
        endStorage.saveEndWorld(endWorld).thenRun(() -> endStorage.saveEndPortal(endPortal));

        current = endPortal;

        return current;
    }

    public EndPortal createRandom() {
        Location location = WorldUtil.findUnclaimedLocation(portalWorld, xBound, zBound);

        return create(location);
    }

}
