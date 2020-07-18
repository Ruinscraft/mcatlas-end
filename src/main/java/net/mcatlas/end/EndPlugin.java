package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EndPlugin extends JavaPlugin {

    public static final Random random = new Random();
    public static final long STARTUP_TIME;

    private static EndPlugin instance;

    private List<World> endWorlds;

    static {
        STARTUP_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    public static EndPlugin get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.endWorlds = this.getCurrentEndWorlds();

        Bukkit.getScheduler().runTaskTimer(this, new EndWorldCheckerTask(), 20 * 10, 60 * 20);

        // check end portal table and create portal
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public List<World> getCurrentEndWorlds() {
        List<World> worlds = new ArrayList<>();
        for (World world : this.getServer().getWorlds()) {
            if (world.getName().startsWith("endworld")) worlds.add(world);
        }
        return worlds;
    }

    public Location findNewPortalLocation() {
        int maxWidth = getConfig().getInt("worldWidth") * 2;
        int maxHeight = getConfig().getInt("worldHeight") * 2;

        int newWidth = ((int) (maxWidth * random.nextDouble())) + (maxWidth / 2);
        int newHeight = ((int) (maxHeight * random.nextDouble())) + (maxWidth / 2);

        Location portalLocation = new Location(Bukkit.getWorlds().get(0), newWidth, 64, newHeight);
        String townName = TownyAPI.getInstance().getTownName(portalLocation);
        if (townName == null || townName.equals("")) {
            return findNewPortalLocation();
        }

        return portalLocation;
    }

    public static String generateWorldName() {
        String uu = UUID.randomUUID().toString().substring(0, 8);
        return "endworld_" + uu;
    }

}
