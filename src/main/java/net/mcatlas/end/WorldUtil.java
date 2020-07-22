package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Random;

public final class WorldUtil {

    private static final Random RANDOM = new Random();

    public static final String END_WORLD_PREFIX = "end_world_";

    public static boolean isInEndWorld(Player player) {
        return isEndWorld(player.getWorld());
    }

    public static boolean isEndWorld(World world) {
        return world.getEnvironment() == World.Environment.THE_END;
    }

    public static Location findUnclaimedLocation(World world, int maxX, int maxZ) {
        int x = ((int) (maxX * RANDOM.nextDouble())) + (maxX / 2);
        int z = ((int) (maxZ * RANDOM.nextDouble())) + (maxX / 2);

        Location location = new Location(world, x, 64, z);
        String townName = TownyAPI.getInstance().getTownName(location);

        // there was a town claim at the randomly chosen location
        if (townName != null) {
            return findUnclaimedLocation(world, maxX, maxZ);
        }

        return location;
    }

    public static World createEndWorld(String worldId) {
        WorldCreator worldCreator = WorldCreator.name(END_WORLD_PREFIX + worldId);

        worldCreator.environment(World.Environment.THE_END);
        worldCreator.generateStructures(true);
        worldCreator.seed(RANDOM.nextLong());
        worldCreator.type(WorldType.NORMAL);

        World world = worldCreator.createWorld();

        Bukkit.getWorlds().add(world);

        return world;
    }

    public static void deleteWorld(World world) {
        if (world == null) {
            return;
        }

        Bukkit.unloadWorld(world, false);

        File folder = new File(Bukkit.getWorldContainer() + "/" + world.getName());

        if (folder.exists()) {
            folder.delete();
        }
    }

}
