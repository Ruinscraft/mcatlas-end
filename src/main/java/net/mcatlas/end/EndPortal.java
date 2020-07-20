package net.mcatlas.end;

public class EndPortal {

    private String endWorld;
    private int x;
    private int z;
    private long closingTime;

    public EndPortal(String endWorld, int x, int z, long closingTime) {
        this.endWorld = endWorld;
        this.x = x;
        this.z = z;
        this.closingTime = closingTime;
    }

    public String getEndWorldName() {
        return this.endWorld;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public long getClosingTime() {
        return this.closingTime;
    }

    public boolean isOpen() {
        return this.closingTime > System.currentTimeMillis();
    }

}
