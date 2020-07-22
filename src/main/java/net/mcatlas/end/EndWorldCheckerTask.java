package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortalManager;
import net.mcatlas.end.storage.EndStorage;
import net.mcatlas.end.world.EndWorld;
import org.bukkit.World;

import java.util.List;

public class EndWorldCheckerTask implements Runnable {

    private EndPlugin endPlugin;

    public EndWorldCheckerTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @Override
    public void run() {
        EndStorage storage = endPlugin.getEndStorage();
        List<String> endWorldDirs = WorldUtil.getEndWorldDirectories();

        /*
         *  Delete any worlds which meet the criteria
         */
        for (EndWorld undeleted : storage.queryUndeletedEndWorlds().join()) {
            // Check if it's the world the active portal goes to
            if (endPlugin.getEndPortalManager().portalActive()) {
                if (undeleted.equals(endPlugin.getEndPortalManager().getCurrent().getEndWorld())) {
                    continue;
                }
            }

            // Check if the world still has a directory
            if (!endWorldDirs.contains(undeleted.getWorldName())) {
                deleteEndWorld(undeleted);
                continue;
            }

            if (undeleted.findBukkitWorld().isPresent()) {
                World bukkitWorld = undeleted.findBukkitWorld().get();

                // There are online players in the world, continue
                if (!bukkitWorld.getPlayers().isEmpty()) {
                    continue;
                }

                List<EndPlayerLogout> logouts = storage.queryEndPlayerLogouts(undeleted).join();
                long activePlayerCount = logouts.stream().filter(l -> !l.expired()).count();

                if (activePlayerCount == 0) {
                    deleteEndWorld(undeleted);
                } else {
                    System.out.println("did not delete because of logouts");
                }
            } else {
                WorldUtil.createBukkitEndWorld(endPlugin, undeleted.getId()); // load the world, we'll check it next go-around
            }
        }

        storage.queryOpenPortal().join().ifPresent(endPortal -> {
            if (!endPortal.getEndWorld().isDeleted()) {
                System.out.println(endPortal);
                endPlugin.getEndPortalManager().setCurrent(endPortal);
            }
        });

        if (EndPortalManager.generateNewPortal()) {
            endPlugin.getEndPortalManager().createRandom(endPlugin);
            endPlugin.getLogger().info("A new End Portal has been created.");
        }
    }

    private void deleteEndWorld(EndWorld endWorld) {
        EndStorage storage = endPlugin.getEndStorage();

        // Set deleted time to now
        endWorld.setDeletedTime(System.currentTimeMillis());

        // Save that the end world was deleted
        storage.saveEndWorld(endWorld);

        // Delete player logouts for that world
        storage.deleteEndPlayerLogouts(endWorld);

        // Delete the assoc Bukkit world
        WorldUtil.deleteBukkitEndWorld(endPlugin, endWorld.getWorldName());
    }

}
