package net.mcatlas.end;

import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.Random;

public class EndWorldCheckerTask implements Runnable {

    private static Random random = new Random();

    private static final long DAY_LENGTH = 86400000;
    private static final long TWELVE_HOURS_LENGTH = 43200000;

    private long nextCreationTime = -1L;

    // once a minute
    @Override
    public void run() {
        checkCreationTime();

        // run task to check

    }

    public void checkCreationTime() {
        if (nextCreationTime == -1L) {
            nextCreationTime = TWELVE_HOURS_LENGTH + ((int) (DAY_LENGTH * random.nextDouble()));
        }

        if (System.currentTimeMillis() > this.nextCreationTime) {
            Bukkit.getLogger().info("New end world");
            // create new end world
            nextCreationTime = -1L;
        }
    }

}
