package net.mcatlas.end.portal;

import net.mcatlas.end.WorldUtil;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class EndPortalManager {

    private static final Random RANDOM = new Random();

    private EndPortal current;
    private World world;
    private int xBound;
    private int zBound;

    public EndPortalManager(World world, int xBound, int zBound) {
        this.world = world;
        this.current = current;
    }

    public EndPortal getCurrent() {
        return current;
    }

    public void setCurrent(EndPortal current) {
        this.current = current;
    }

    public EndPortal createRandom() {
        World end = WorldUtil.createEndWorld();
        Location location = WorldUtil.findUnclaimedLocation(world, xBound, zBound);
        long closingTime = 0;
        EndPortal portal = new EndPortal(end, location.getBlockX(), location.getBlockZ(), closingTime);

        return current = portal;
    }

}
