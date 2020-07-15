package net.mcatlas.end;

import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.Random;

public class EndWorldCheckerTask implements Runnable {

    private static final Calendar calendar = Calendar.getInstance();
    private static Random random = new Random();

    private static final long DAY_LENGTH = 86400000;

    private long nextCreationTime = -1L;

    @Override
    public void run() {
        checkCreationTime();

        // run task to check

    }

    public void checkCreationTime() {
        calendar.setTimeInMillis(EndPlugin.STARTUP_TIME);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        long currentTime = System.currentTimeMillis();
        calendar.setTimeInMillis(currentTime);

        // will not run on the same day the server has restarted to prevent it happening twice in a day
        if (day == calendar.get(Calendar.DAY_OF_MONTH)) return;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (nextCreationTime == -1L) {
            calendar.setTimeInMillis(currentTime + DAY_LENGTH);
            int newHour = (int) (23D * random.nextDouble());
            if (newHour == 0) return; // wont run task at 12:xx AM so that two wont run at once
            // it will run again and find a new hour
            // could be a better solution for this
            calendar.set(Calendar.HOUR_OF_DAY, newHour);
            calendar.set(Calendar.MINUTE, (int) (59D * random.nextDouble()));

            this.nextCreationTime = calendar.getTimeInMillis();
        }

        if (currentTime > this.nextCreationTime) {
            Bukkit.getLogger().info("New end world");
            // create new end world
        }
    }

}
