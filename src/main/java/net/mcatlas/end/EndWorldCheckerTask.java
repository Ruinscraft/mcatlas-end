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

    private EndPlugin endPlugin;
    private long nextCreationTime;

    public EndWorldCheckerTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
        nextCreationTime = newCreationTime();
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
        updateEndPortalManagerActivePortal();
        checkEndCreate();
        checkEndDelete();
    }

    private void updateEndPortalManagerActivePortal() {
        endPlugin.getEndStorage().queryEndPortals().thenAccept(portals -> {
            for (EndPortal portal : portals) {
                if (portal.isOpen()) {
                    endPlugin.getEndPortalManager().setCurrent(portal);
                }
            }
        });
    }

    private void checkEndCreate() {
        if (System.currentTimeMillis() > nextCreationTime) {
            Bukkit.getLogger().info("Creating new end world");

            nextCreationTime = newCreationTime();

            EndPortal endPortal = endPlugin.getEndPortalManager().createRandom();

            endPlugin.getEndStorage().saveEndPortal(endPortal);
        }
    }

    private void checkEndDelete() {
        EndPortal portal = endPlugin.getEndPortalManager().getCurrent();

        if (portal == null) {
            return;
        }

        endPlugin.getEndStorage().queryEndWorlds().thenAccept(result -> {
            for (EndWorld endWorld : result) {
                if (endWorld.isDeleted()) {
                    continue;
                }

                // Portal is open for the current EndWorld, continue on
                if (portal.getEndWorld().equals(endWorld) && portal.isOpen()) {
                    continue;
                }

                // An old end world that the Portal doesn't go to
                else if (!portal.getEndWorld().equals(endWorld)) {
                    checkPlayerLogouts(endWorld);
                }
            }
        });
    }

    private void checkPlayerLogouts(EndWorld endWorld) {
        endPlugin.getEndStorage().queryEndPlayerLogouts(endWorld).thenAccept(result -> {
            boolean delete = true;
            long currentTime = System.currentTimeMillis();

            for (EndPlayerLogout logout : result) {
                if (currentTime - logout.getLogoutTime() <= OFFLINE_BEFORE_DELETE_LENGTH) {
                    delete = false; // someone has been in the world recent enough
                }
            }

            if (delete) {
                deleteWorld(endWorld.findBukkitWorld().get());
            }
        });
    }

}
