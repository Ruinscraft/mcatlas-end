package net.mcatlas.end;

import com.palmergames.bukkit.towny.TownyAPI;
import net.mcatlas.end.storage.MySQLStorage;
import net.mcatlas.end.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EndPlugin extends JavaPlugin {

    public static final Random random = new Random();
    public static final long STARTUP_TIME;

    private static EndPlugin instance;

    static {
        STARTUP_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    private Storage storage;
    private List<World> endWorlds;
    private EndPortal portal;

    public static EndPlugin get() {
        return instance;
    }

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

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setupStorage();

        this.endWorlds = this.getCurrentEndWorlds();

        Bukkit.getScheduler().runTaskTimer(this, new EndWorldCheckerTask(), 20 * 60 * 15, 20 * 60);

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // update current portal from db if it exists
        this.getStorage().getPortals().thenAccept(portals -> {
            for (EndPortal portal : portals) {
                if (portal != null && portal.isOpen()) {
                    this.portal = portal;
                }
            }
            this.portal = null;
        });
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

    @Nullable
    public EndPortal getCurrentPortal() {
        if (this.portal == null || !this.portal.isOpen()) return null;
        return this.portal;
    }

    public void updateEndPortal(EndPortal portal) {
        this.portal = portal;
    }

    public Location findNewPortalLocation() {
        int maxWidth = getConfig().getInt("worldWidth") * 2;
        int maxHeight = getConfig().getInt("worldHeight") * 2;

        int newWidth = ((int) (maxWidth * random.nextDouble())) + (maxWidth / 2);
        int newHeight = ((int) (maxHeight * random.nextDouble())) + (maxWidth / 2);

        Location portalLocation = new Location(Bukkit.getWorlds().get(0), newWidth, 64, newHeight);

        // if in a town, find another location
        String townName = TownyAPI.getInstance().getTownName(portalLocation);
        if (townName == null || townName.equals("")) {
            return findNewPortalLocation();
        }

        return portalLocation;
    }

}
