package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortalManager;
import net.mcatlas.end.storage.EndStorage;
import net.mcatlas.end.world.EndWorld;

import java.util.List;

public class EndWorldCheckerTask implements Runnable {

    private EndPlugin endPlugin;
    private boolean portalWasActive;

    public EndWorldCheckerTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @Override
    public void run() {
        EndStorage storage = endPlugin.getEndStorage();

        boolean portalActive = endPlugin.getEndPortalManager().portalActive();

        /*
         *  Check portal states to announce a close/open
         */
        if (portalActive && !portalWasActive) {
            Announcements.announcePortalOpen();
        }

        else if (!portalActive && portalWasActive) {
            Announcements.announcePortalClose();
        }

        /*
         *  Check if a new End Portal should be created
         */
        if (EndPortalManager.generateNewPortal()) {
            endPlugin.getEndPortalManager().createRandom(endPlugin);
            endPlugin.getLogger().info("A new End Portal has been created.");
        }

        List<String> endWorldDirs = WorldUtil.getEndWorldDirectories();

        /*
         *  Delete any worlds which meet the criteria
         */
        for (EndWorld undeleted : storage.queryUndeletedEndWorlds().join()) {
            if (!undeleted.worldLoaded()) {
                undeleted.loadWorld(endPlugin); // The world was not loaded, cannot check for online players
                continue;
            }

            if (undeleted.hasActivePortal(endPlugin)) {
                continue; // There was an active portal for this End World
            }

            if (!endWorldDirs.contains(undeleted.getWorldName())) {
                deleteEndWorld(undeleted); // There was no world dir on disk
                continue;
            }

            if (undeleted.onlinePlayerCount() > 0) {
                continue; // There are currently players in this End World
            }

            long activePlayerCount = storage.queryEndPlayerLogouts(undeleted).join()
                    .stream()
                    .filter(l -> !l.expired()).count();

            if (activePlayerCount == 0) {
                deleteEndWorld(undeleted); // There were no active players
                continue;
            }
        }

        portalWasActive = endPlugin.getEndPortalManager().portalActive();
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
        WorldUtil.deleteBukkitEndWorld(endPlugin, endWorld);
    }

}
