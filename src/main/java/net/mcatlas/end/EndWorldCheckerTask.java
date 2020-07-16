package net.mcatlas.end;

import org.bukkit.Bukkit;

public class EndWorldCheckerTask implements Runnable {

    private static final long DAY_LENGTH = 86400000;
    private static final long TWELVE_HOURS_LENGTH = 43200000;

    private long nextCreationTime = newCreationTime();

    // once a minute
    @Override
    public void run() {
        checkCreationTime();

        // run task to check for world deletion

    }

    public void checkCreationTime() {
        if (System.currentTimeMillis() > this.nextCreationTime) {
            Bukkit.getLogger().info("New end world");
            // create new end world
            nextCreationTime = newCreationTime();
        }
    }

    public static long newCreationTime() {
        return TWELVE_HOURS_LENGTH + ((int) (DAY_LENGTH * EndPlugin.random.nextDouble()));
    }

}
