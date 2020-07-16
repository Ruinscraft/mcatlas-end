package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.UUID;

public class EndPlugin extends JavaPlugin {

    public static final Random random = new Random();

    public static final long STARTUP_TIME;

    static {
        STARTUP_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getScheduler().runTaskTimer(this, new EndWorldCheckerTask(), 20 * 10, 60 * 20);
    }

    @Override
    public void onDisable() {

    }

    public Location findNewPortalLocation() {
        int maxWidth = getConfig().getInt("worldWidth") * 2;
        int maxHeight = getConfig().getInt("worldHeight") * 2;

        int newWidth = ((int) (maxWidth * random.nextDouble())) + (maxWidth / 2);
        int newHeight = ((int) (maxHeight * random.nextDouble())) + (maxWidth / 2);

        Location portalLocation = new Location(Bukkit.getWorlds().get(0), newWidth, 64, newHeight));
        String townName = TownyAPI.getInstance().getTownName(portalLocation);
        if (townName == null || townName.equals("")) {
            return findNewPortalLocation();
        }

        return portalLocation;
    }

    public static World createWorld() {
        WorldCreator worldCreator = WorldCreator.name(generateWorldName());
        worldCreator.environment(World.Environment.THE_END);
        worldCreator.generateStructures(true);
        worldCreator.seed(random.nextLong());
        worldCreator.type(WorldType.NORMAL);
        return worldCreator.createWorld();
    }

    public static String generateWorldName() {
        String uu = UUID.randomUUID().toString().substring(0, 8);
        return "endworld_" + uu;
    }

}
