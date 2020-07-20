package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import net.mcatlas.end.portal.EndPortalManager;
import net.mcatlas.end.storage.EndStorage;
import net.mcatlas.end.storage.MySQLEndStorage;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class EndPlugin extends JavaPlugin {

    private EndStorage endStorage;
    private EndPortalManager endPortalManager;
    private List<World> endWorlds;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupStorage();

        endWorlds = getCurrentEndWorlds();

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getScheduler().runTaskTimer(this, new EndWorldCheckerTask(this), 20 * 60 * 15, 20 * 60);

        // update current portal from db if it exists
        endStorage.getPortals().thenAccept(portals -> {
            for (EndPortal portal : portals) {
                if (portal.isOpen()) {
                    endPortalManager.setCurrent(portal);
                }
            }
        });
    }

    @Override
    public void onDisable() {

    }

    public EndStorage getEndStorage() {
        return endStorage;
    }

    public EndPortalManager getEndPortalManager() {
        return endPortalManager;
    }

    private void setupStorage() {
        String host = getConfig().getString("storage.mysql.host");
        int port = getConfig().getInt("storage.mysql.port");
        String database = getConfig().getString("storage.mysql.database");
        String username = getConfig().getString("storage.mysql.username");
        String password = getConfig().getString("storage.mysql.password");

        endStorage = new MySQLEndStorage(host, port, database, username, password);
    }

    public List<World> getCurrentEndWorlds() {
        List<World> worlds = new ArrayList<>();

        for (World world : this.getServer().getWorlds()) {
            if (world.getEnvironment() == World.Environment.THE_END) {
                worlds.add(world);
            }
        }

        return worlds;
    }

}
