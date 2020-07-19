package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import net.mcatlas.end.storage.MySQLStorage;
import net.mcatlas.end.storage.Storage;
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

    private Storage storage;

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

        setupStorage();

        this.endWorlds = this.getCurrentEndWorlds();

        Bukkit.getScheduler().runTaskTimer(this, new EndWorldCheckerTask(), 20 * 10, 60 * 20);

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        // check end portal table and create portal

    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public Storage getStorage() {
        return this.storage;
    }

    private void setupStorage() {
        String host = getConfig().getString("storage.mysql.host");
        int port = getConfig().getInt("storage.mysql.port");
        String database = getConfig().getString("storage.mysql.database");
        String username = getConfig().getString("storage.mysql.username");
        String password = getConfig().getString("storage.mysql.password");
        String playersTable = getConfig().getString("storage.mysql.playersTable");
        String worldsTable = getConfig().getString("storage.mysql.worldsTable");
        String portalsTable = getConfig().getString("storage.mysql.portalsTable");

        storage = new MySQLStorage(host, port, database, username, password, playersTable, worldsTable, portalsTable);
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
