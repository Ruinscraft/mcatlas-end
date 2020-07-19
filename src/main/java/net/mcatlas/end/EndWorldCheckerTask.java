package net.mcatlas.end;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class EndWorldCheckerTask implements Runnable {

    private static final long DAY_LENGTH = 86400000;
    private static final long TWELVE_HOURS_LENGTH = 43200000;
    private static final long OFFLINE_BEFORE_DELETE_LENGTH = 3600000;

    private long nextCreationTime = newCreationTime();

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
        }
    }

    public void checkDeletionTime() {
        for (World world : EndPlugin.get().getCurrentEndWorlds()) {
            if (world.getPlayers().size() > 0) continue;
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

    public static long newCreationTime() {
        return TWELVE_HOURS_LENGTH + ((int) (DAY_LENGTH * EndPlugin.random.nextDouble()));
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