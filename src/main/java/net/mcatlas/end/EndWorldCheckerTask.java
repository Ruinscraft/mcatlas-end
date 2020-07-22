package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    }

    public long getNextCreationTime() {
        return nextCreationTime;
    }

    @Override
    public void run() {
        updateCurrentPortal();
        checkEndCreate();
        checkEndDelete();
        checkLoadEndWorlds();
    }

    private void updateCurrentPortal() {
        endPlugin.getEndStorage().queryEndPortals().thenAccept(portals -> {
            for (EndPortal portal : portals) {
                if (portal.isOpen()) {
                    endPlugin.getEndPortalManager().setCurrent(portal);
                }
            }
        });
    }

    public void checkLoadEndWorlds() {
        List<String> endWorldFolders = new ArrayList<>();

        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }

            if (file.getName().startsWith(WorldUtil.END_WORLD_PREFIX)) {
                endWorldFolders.add(file.getName());
            }
        }

        // check for worlds to load
        for (String endWorldFolder : endWorldFolders) {
            String worldId = endWorldFolder.replace(WorldUtil.END_WORLD_PREFIX, "");

            endPlugin.getEndStorage().queryEndWorld(worldId).thenAccept(result -> {
                if (result != null) {
                    if (!result.isDeleted()) {
                        endPlugin.getServer().getScheduler().runTask(endPlugin, () -> {
                            WorldUtil.createEndWorld(worldId);

                            endPlugin.getLogger().info("Loaded world: " + endWorldFolder);
                        });
                    }
                }
            });
        }

        // check for worlds to unload
        for (World world : Bukkit.getWorlds()) {
            if (!world.getName().startsWith(WorldUtil.END_WORLD_PREFIX)) {
                continue; // not an end world
            }

            if (!endWorldFolders.contains(world.getWorldFolder().getName())) {
                endPlugin.getServer().getScheduler().runTask(endPlugin, () -> {
                    Bukkit.unloadWorld(world, false);

                    endPlugin.getLogger().info("Unloaded deleted end world: " + world.getName());
                });
            }
        }
    }

    private void checkEndCreate() {
        if (System.currentTimeMillis() > nextCreationTime) {
            Bukkit.getLogger().info("Creating new end world");

            nextCreationTime = generateNewTime();

            endPlugin.getEndPortalManager().createRandom();
        }
    }

    private void checkEndDelete() {
        EndPortal portal = endPlugin.getEndPortalManager().getCurrent();

        if (portal == null) {
            return;
        }

        endPlugin.getEndStorage().queryEndWorlds().thenAccept(endWorlds -> {
            for (EndWorld endWorld : endWorlds) {
                if (endWorld.isDeleted()) {
                    continue;
                }

                // Portal is open for the current EndWorld, continue on
                if (portal.getEndWorld().equals(endWorld) && portal.isOpen()) {
                    continue;
                }

                // An old end world that the Portal doesn't go to
                else if (!portal.getEndWorld().equals(endWorld)) {
                    endPlugin.getEndStorage().queryEndPlayerLogouts(endWorld).thenAccept(logouts -> {
                        boolean delete = true;
                        long currentTime = System.currentTimeMillis();

                        for (EndPlayerLogout logout : logouts) {
                            if (currentTime - logout.getLogoutTime() <= OFFLINE_BEFORE_DELETE_LENGTH) {
                                delete = false; // someone has been in the world recent enough
                            }
                        }

                        if (delete) {
                            WorldUtil.deleteWorld(endWorld.findBukkitWorld().get());
                        }
                    });
                }
            }
        });
    }

    public static long generateNewTime() {
        return System.currentTimeMillis() + DAY_HALF_LENGTH + ((int) (DAY_LENGTH * RANDOM.nextDouble()));
    }

}
