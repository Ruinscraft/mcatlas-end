package net.mcatlas.end;

public class EndPortal {

    private String worldName;
    private int x;
    private int z;
    private long closingTime;

    public EndPortal(String worldName, int x, int z, long closingTime) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.closingTime = closingTime;
    }

    public String getWorldName() {
        return this.worldName;
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

}
