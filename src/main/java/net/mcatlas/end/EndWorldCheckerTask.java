package net.mcatlas.end;

import org.bukkit.*;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class EndWorldCheckerTask implements Runnable {

    private static final long DAY_LENGTH = 86400000;
    private static final long DAY_HALF_LENGTH = DAY_LENGTH / 2;
    private static final long OFFLINE_BEFORE_DELETE_LENGTH = 3600000;
    private static final long PORTAL_TIME_OPEN_LENGTH = 3600000;

    private long nextCreationTime = newCreationTime();

    // generate new time for creating end world in millis
    public static long newCreationTime() {
        return DAY_HALF_LENGTH + ((int) (DAY_LENGTH * EndPlugin.random.nextDouble()));
    }

    // once a minute
    @Override
    public void run() {
        checkCreationTime();

        checkDeletionTime();
    }

    public void checkCreationTime() {
        if (System.currentTimeMillis() > this.nextCreationTime) {
            Bukkit.getLogger().info("New end world");
            // create new end world
            nextCreationTime = newCreationTime();
            World endWorld = createEndWorld();

            Location location = EndPlugin.get().findNewPortalLocation();
            EndPortal newPortal = new EndPortal(endWorld.getName(),
                    location.getBlockX(), location.getBlockZ(),
                    System.currentTimeMillis() + PORTAL_TIME_OPEN_LENGTH);
            EndPlugin.get().getStorage().savePortal(newPortal.getEndWorldName(), newPortal.getX(),
                    newPortal.getZ(), newPortal.getClosingTime());
            EndPlugin.get().updateEndPortal(newPortal);
        }
    }

    public void checkDeletionTime() {
        for (World world : EndPlugin.get().getCurrentEndWorlds()) {
            // players currently in world? continue
            if (world.getPlayers().size() > 0) continue;

            // if world is currently accessible, continue
            EndPortal portal = EndPlugin.get().getCurrentPortal();
            if (portal.isOpen() && portal.getEndWorldName().equals(world.getName())) continue;
            // check db for player times since they last went in the world
            // if all players r gone, deleteWorld(world.getName());
            EndPlugin.get().getStorage().getPlayers(world.getName()).thenAccept(players -> {
                boolean keep = false;
                long currentTime = System.currentTimeMillis();
                for (Map.Entry<UUID, Long> entry : players.entrySet()) {
                    UUID uuid = entry.getKey();
                    long lastTime = entry.getValue();
                    if (currentTime - lastTime <= OFFLINE_BEFORE_DELETE_LENGTH) {
                        keep = true;
                        break;
                    }
                }
                if (!keep) {
                    deleteWorld(world.getName());
                }
            });
        }
    }

    public World createEndWorld() {
        WorldCreator worldCreator = WorldCreator.name(EndPlugin.generateWorldName());
        worldCreator.environment(World.Environment.THE_END);
        worldCreator.generateStructures(true);
        worldCreator.seed(EndPlugin.random.nextLong());
        worldCreator.type(WorldType.NORMAL);
        return worldCreator.createWorld();
    }

    // run check if players are gone etc. before running this
    public void deleteWorld(String worldName) {
        Bukkit.unloadWorld(worldName, false);

        File folder = new File(Bukkit.getWorldContainer() + "/" + worldName);
        folder.delete();
    }

}
