package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;

public final class WorldUtil {

    private static final Random RANDOM = new Random();

    public static String generateWorldName() {
        String uu = UUID.randomUUID().toString().substring(0, 8);
        return "endworld_" + uu;
    }

    public static boolean isInEndWorld(Player player) {
        return isEndWorld(player.getWorld());
    }

    public static boolean isEndWorld(World world) {
        return world.getEnvironment() == World.Environment.THE_END;
    }

    public static boolean isInOverworld(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.NORMAL;
    }

    public static Location findUnclaimedLocation(World world, int maxX, int maxZ) {
        int x = ((int) (maxX * RANDOM.nextDouble())) + (maxX / 2);
        int z = ((int) (maxZ * RANDOM.nextDouble())) + (maxX / 2);

        Location location = new Location(world, x, 64, z);
        String townName = TownyAPI.getInstance().getTownName(location);

        if (townName == null || townName.isEmpty()) {
            return findUnclaimedLocation(world, maxX, maxZ);
        }

        return location;
    }

}
