package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortalEffectsTask;
import net.mcatlas.end.portal.EndPortalManager;
import net.mcatlas.end.storage.EndStorage;
import net.mcatlas.end.storage.MySQLEndStorage;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class EndPlugin extends JavaPlugin {

    private EndStorage endStorage;
    private EndPortalManager endPortalManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setupEndPortalManager();
        setupEndStorage();

        getServer().getPluginManager().registerEvents(new EndPortalListener(this), this);
        getServer().getScheduler().runTaskTimer(this, new EndWorldCheckerTask(this), 20 * 60 * 15, 20 * 60);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new EndPortalEffectsTask(this), 20 * 5, 2);

        getCommand("endportal").setExecutor(new EndPortalCommand(this));
    }

    public EndStorage getEndStorage() {
        return endStorage;
    }

    public EndPortalManager getEndPortalManager() {
        return endPortalManager;
    }

    private void setupEndStorage() {
        String host = getConfig().getString("storage.mysql.host");
        int port = getConfig().getInt("storage.mysql.port");
        String database = getConfig().getString("storage.mysql.database");
        String username = getConfig().getString("storage.mysql.username");
        String password = getConfig().getString("storage.mysql.password");

        endStorage = new MySQLEndStorage(host, port, database, username, password);
    }

    private void setupEndPortalManager() {
        int xBound = getConfig().getInt("portal-world.x-bound");
        int zBound = getConfig().getInt("portal-world.z-bound");
        String portalWorldName = getConfig().getString("portal-world.world");
        World portalWorld = getServer().getWorld(portalWorldName);

        if (portalWorld == null) {
            getLogger().warning("Portal world not found. Check the config.");
            return;
        }

        endPortalManager = new EndPortalManager(portalWorld, xBound, zBound);
    }

}
