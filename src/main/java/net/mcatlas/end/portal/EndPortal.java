package net.mcatlas.end.portal;

import net.mcatlas.end.EndWorld;

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

}
