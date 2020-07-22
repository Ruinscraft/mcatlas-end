package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import net.mcatlas.end.world.EndWorld;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class WorldUtil {

    private static final Random RANDOM = new Random();

    public static boolean isInEndWorld(Player player) {
        return isEndWorld(player.getWorld());
    }

    public static boolean isEndWorld(World world) {
        return world.getEnvironment() == World.Environment.THE_END;
    }

    public static Location findUnclaimedLocation(World world, int xBound, int zBound) {
        final double randomX = ThreadLocalRandom.current().nextDouble(-xBound, xBound);
        final double randomZ = ThreadLocalRandom.current().nextDouble(-zBound, zBound);

        Location location = new Location(world, randomX, 64D, randomZ);
        String townName = TownyAPI.getInstance().getTownName(location);

        // there was a town claim at the randomly chosen location
        if (townName != null) {
            return findUnclaimedLocation(world, xBound, zBound);
        }

        return location;
    }

    public static void createBukkitEndWorld(EndPlugin endPlugin, EndWorld endWorld) {
        endPlugin.getServer().getScheduler().runTask(endPlugin, () -> {
            WorldCreator worldCreator = WorldCreator.name(endWorld.getWorldName());

            worldCreator.environment(World.Environment.THE_END);
            worldCreator.generateStructures(true);
            worldCreator.seed(RANDOM.nextLong());
            worldCreator.type(WorldType.NORMAL);

            World world = worldCreator.createWorld();

            endPlugin.getServer().getWorlds().add(world);
            endPlugin.getLogger().info("Loaded world: " + world.getName());
        });
    }

    public static void deleteBukkitEndWorld(EndPlugin endPlugin, EndWorld endWorld) {
        endPlugin.getServer().getScheduler().runTask(endPlugin, () -> {
            World bukkitWorld = Bukkit.getWorld(endWorld.getWorldName());

            if (bukkitWorld != null) {
                // In case there are any players left in the world
                for (Player player : bukkitWorld.getPlayers()) {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }

                endPlugin.getServer().unloadWorld(bukkitWorld, false);

                for (Chunk chunk : bukkitWorld.getLoadedChunks()) {
                    chunk.unload(false);
                }
            }

            endPlugin.getServer().getScheduler().runTaskAsynchronously(endPlugin, () -> {
                File file = new File(Bukkit.getWorldContainer(), endWorld.getWorldName());

                if (deleteDirectory(file)) {
                    endPlugin.getLogger().info("Deleted world: " + endWorld.getWorldName());
                }
            });
        });
    }

    public static List<String> getEndWorldDirectories() {
        List<String> endWorldDirectories = new ArrayList<>();

        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }

            if (file.getName().startsWith(EndWorld.END_WORLD_PREFIX)) {
                endWorldDirectories.add(file.getName());
            }
        }

        return endWorldDirectories;
    }

    public static Set<World> getBukkitEndWorlds() {
        return Bukkit.getWorlds()
                .stream()
                .filter(w -> w.getEnvironment() == World.Environment.THE_END)
                .collect(Collectors.toSet());
    }

    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File files[] = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }

        return directory.delete();
    }

}
