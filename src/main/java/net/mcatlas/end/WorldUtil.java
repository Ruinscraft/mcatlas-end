package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import net.mcatlas.end.world.EndWorld;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class WorldUtil {

    public static final Random RANDOM = new Random();

    public static boolean isInEndWorld(Player player) {
        return isEndWorld(player.getWorld());
    }

    public static boolean isEndWorld(World world) {
        return world.getEnvironment() == World.Environment.THE_END;
    }

    public static Location findUnclaimedLocation(World world, int xBound, int zBound) {
        final int randomX = (int) ThreadLocalRandom.current().nextDouble(-xBound, xBound);
        final int randomZ = (int) ThreadLocalRandom.current().nextDouble(-zBound, zBound);

        Block block = world.getHighestBlockAt(randomX, randomZ);

        if (block.getType().name().contains("LEAVES")
                || block.getRelative(BlockFace.DOWN).getType().name().contains("LEAVES")) {
            // the location was on top of a tree
            return findUnclaimedLocation(world, xBound, zBound);
        }

        String townName = TownyAPI.getInstance().getTownName(block.getLocation());

        // there was a town claim at the randomly chosen location
        if (townName != null) {
            return findUnclaimedLocation(world, xBound, zBound);
        }

        return block.getLocation();
    }

    public static Location findRandomEndSpawn(World world) {
        Location endSpawn = world.getSpawnLocation().clone();

        double angle = Math.random() * 360;
        int rad = 40;
        int x = (int) (Math.cos(angle) * rad);
        int z = (int) (Math.sin(angle) * rad);

        endSpawn.add(x, 0, z);
        endSpawn.setY(endSpawn.getWorld().getHighestBlockYAt(endSpawn));

        if (endSpawn.getBlockY() > 70) {
            // Too high, probably an end pillar
            return findRandomEndSpawn(world);
        }

        return endSpawn;
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

    public static double getDistanceBetweenChunks(Chunk a, Chunk b) {
        int x1 = a.getX();
        int x2 = b.getX();
        int z1 = a.getZ();
        int z2 = b.getZ();
        double dist = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((z1 - z2), 2));

        return dist;
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
