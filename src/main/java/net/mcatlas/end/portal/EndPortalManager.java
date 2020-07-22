package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import net.mcatlas.end.WorldUtil;
import net.mcatlas.end.world.EndWorld;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EndPortalManager {

    private static long nextPortalTime;

    {
        generateNextPortalTime();
    }

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
        if (current.isClosed()) {
            current = null;
        }

        if (current.getEndWorld().isDeleted()) {
            current = null;
        }

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

    public int getXBound() {
        return xBound;
    }

    public int getZBound() {
        return zBound;
    }

    public EndPortal create(EndPlugin endPlugin, Location location) {
        String worldId = UUID.randomUUID().toString().substring(0, 8);
        EndWorld endWorld = new EndWorld(worldId);
        long closeTime = System.currentTimeMillis() + openTimeMillis;
        EndPortal endPortal = new EndPortal(endWorld, location, closeTime);

        // create world
        WorldUtil.createBukkitEndWorld(endPlugin, endWorld);

        // save new world then the new portal to storage
        endPlugin.getEndStorage().saveEndWorld(endWorld).thenRun(() -> endPlugin.getEndStorage().saveEndPortal(endPortal));

        current = endPortal;

        return current;
    }

    public EndPortal createRandom(EndPlugin endPlugin) {
        Location location = WorldUtil.findUnclaimedLocation(portalWorld, xBound, zBound);

        return create(endPlugin, location);
    }

    public static long getNextPortalTime() {
        return nextPortalTime;
    }

    public static void generateNextPortalTime() {
        Random random = new Random();

        nextPortalTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12) + ((int) (TimeUnit.HOURS.toMillis(24) * random.nextDouble()));
    }

    public static boolean generateNewPortal() {
        if (nextPortalTime < System.currentTimeMillis()) {
            generateNextPortalTime();
            return true;
        }

        return false;
    }

}
