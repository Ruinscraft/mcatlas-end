package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.Random;

public class EndWorldCheckerTask implements Runnable {

    private static final Random RANDOM = new Random();
    private static final long DAY_LENGTH = 86400000;
    private static final long DAY_HALF_LENGTH = DAY_LENGTH / 2;
    private static final long OFFLINE_BEFORE_DELETE_LENGTH = 3600000;
    private static final long PORTAL_TIME_OPEN_LENGTH = 3600000;

    private long nextCreationTime = newCreationTime();
    private EndPlugin endPlugin;

    public EndWorldCheckerTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    private static void deleteWorld(World world) {
        Bukkit.unloadWorld(world, false);

        File folder = new File(Bukkit.getWorldContainer() + "/" + world.getName());

        if (folder.exists()) {
            folder.delete();
        }
    }

    public static long newCreationTime() {
        return System.currentTimeMillis() + DAY_HALF_LENGTH + ((int) (DAY_LENGTH * RANDOM.nextDouble()));
    }

    @Override
    public void run() {
        checkEndCreate();
        checkEndDelete();
    }

    public void checkEndCreate() {
        if (System.currentTimeMillis() > nextCreationTime) {
            Bukkit.getLogger().info("Creating new end world");

            nextCreationTime = newCreationTime();

            EndPortal portal = endPlugin.getEndPortalManager().createRandom();

            endPlugin.getEndStorage().savePortal(portal);
        }
    }

    public void checkEndDelete() {
        for (World world : endPlugin.getCurrentEndWorlds()) {
            int playerCount = world.getPlayers().size();

            if (playerCount > 0) {
                continue;
            }

            EndPortal portal = endPlugin.getEndPortalManager().getCurrent();

            if (portal != null) {
                // if closed or not the same end world
                if (portal.isClosed() || !portal.getEnd().equals(world)) {
                    deleteWorld(world);
                }

                endPlugin.getEndStorage().getPlayers(world).thenAccept(result -> {
                    boolean delete = true;
                    long currentTime = System.currentTimeMillis();

                    for (long lastTimeInWorld : result.values()) {
                        if (currentTime - lastTimeInWorld <= OFFLINE_BEFORE_DELETE_LENGTH) {
                            delete = false; // someone has been in the world recent enough
                        }
                    }

                    if (delete) {
                        deleteWorld(world);
                    }
                });
            }
        }
    }

}
