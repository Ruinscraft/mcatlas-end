package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortalEffectsTask;
import net.mcatlas.end.portal.EndPortalManager;
import net.mcatlas.end.storage.EndStorage;
import net.mcatlas.end.storage.MySQLEndStorage;
import net.mcatlas.end.world.EndWorld;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class EndPlugin extends JavaPlugin {

    private static final long MINUTE_IN_TICKS = 20 * 60;

    private EndStorage endStorage;
    private EndPortalManager endPortalManager;
    private EndWorldCheckerTask endWorldCheckerTask;
    private EndPortalEffectsTask endPortalEffectsTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setupEndStorage();
        setupEndPortalManager();
        setupEndWorldCheckerTask();
        setupEndPortalEffectsTask();

        // Register listeners
        getServer().getPluginManager().registerEvents(new EndPortalListener(this), this);

        // Register commands
        getCommand("endportal").setExecutor(new EndPortalCommand(this));
    }

    @Override
    public void onDisable() {
        // Save any players that were in End Worlds to end_player_logouts
        for (World world : WorldUtil.getBukkitEndWorlds()) {
            String worldId = world.getName().replace(EndWorld.END_WORLD_PREFIX, "");

            endStorage.queryEndWorld(worldId).thenAccept(o -> {
               o.ifPresent(endWorld -> {
                   if (endWorld.isDeleted()) {
                       return;
                   }

                   for (Player player : world.getPlayers()) {
                       EndPlayerLogout endPlayerLogout = new EndPlayerLogout(endWorld, player.getUniqueId(), System.currentTimeMillis());

                       endStorage.saveEndPlayerLogout(endPlayerLogout);
                   }
               });
            });
        }
    }

    public EndStorage getEndStorage() {
        return endStorage;
    }

    public EndPortalManager getEndPortalManager() {
        return endPortalManager;
    }

    public EndWorldCheckerTask getEndWorldCheckerTask() {
        return endWorldCheckerTask;
    }

    public EndPortalEffectsTask getEndPortalEffectsTask() {
        return endPortalEffectsTask;
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
        int xBound = getConfig().getInt("portal-world.x-bound", 20000);
        int zBound = getConfig().getInt("portal-world.z-bound", 20000);
        String portalWorldName = getConfig().getString("portal-world.world", "world");
        World portalWorld = getServer().getWorld(portalWorldName);
        long portalOpenTimeMillis = getConfig().getLong("portal-open-time-millis", TimeUnit.HOURS.toMillis(1));

        if (portalWorld == null) {
            getLogger().warning("Portal world not found. Check the config.");
            return;
        }

        endPortalManager = new EndPortalManager(portalWorld, xBound, zBound, portalOpenTimeMillis);

        // Load current portal from storage
        endStorage.queryOpenPortal().join().ifPresent(endPortal -> {
            endPortalManager.setCurrent(endPortal);
            getLogger().info("Loaded End Portal @ " + endPortal.getX() + "," + endPortal.getZ());
        });
    }

    private void setupEndWorldCheckerTask() {
        endWorldCheckerTask = new EndWorldCheckerTask(this);

        getServer().getScheduler().runTaskTimer(this, endWorldCheckerTask, 0L, MINUTE_IN_TICKS / 30);
    }

    private void setupEndPortalEffectsTask() {
        endPortalEffectsTask = new EndPortalEffectsTask(this);

        long periodTicks = 1;

        getServer().getScheduler().runTaskTimerAsynchronously(this, endPortalEffectsTask, MINUTE_IN_TICKS / 2, periodTicks);
    }

}
