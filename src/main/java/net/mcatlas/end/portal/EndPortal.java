package net.mcatlas.end.portal;

import org.bukkit.World;

public class EndPortal {

    private World end;
    private int x;
    private int z;
    private long closingTime;

    public EndPortal(World end, int x, int z, long closingTime) {
        this.end = end;
        this.x = x;
        this.z = z;
        this.closingTime = closingTime;
    }

    public World getEnd() {
        return end;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long getClosingTime() {
        return this.closingTime;
    }

    public boolean isOpen() {
        return this.closingTime > System.currentTimeMillis();
    }

    public boolean isClosed() {
        return !isOpen();
    }

}
