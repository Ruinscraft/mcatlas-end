package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import net.mcatlas.end.world.EndWorld;
import org.bukkit.Location;

public class EndPortal {

    private EndWorld endWorld;
    private int x;
    private int z;
    private long closeTime;

    public EndPortal(EndWorld endWorld, int x, int z, long closeTime) {
        this.endWorld = endWorld;
        this.x = x;
        this.z = z;
        this.closeTime = closeTime;
    }

    public EndPortal(EndWorld endWorld, Location location, long closeTime) {
        this(endWorld, location.getBlockX(), location.getBlockZ(), closeTime);
    }

    public EndWorld getEndWorld() {
        return endWorld;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public boolean isOpen() {
        return closeTime > System.currentTimeMillis();
    }

    public boolean isClosed() {
        return !isOpen();
    }

    public void close(EndPlugin endPlugin) {
        closeTime = System.currentTimeMillis();

        endPlugin.getEndStorage().saveEndPortal(this);
    }

    @Override
    public String toString() {
        return "EndPortal{" +
                "endWorld=" + endWorld +
                ", x=" + x +
                ", z=" + z +
                ", closeTime=" + closeTime +
                '}';
    }

}
