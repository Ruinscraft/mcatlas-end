package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EndPortalPlayerTask implements Runnable {

    private EndPlugin endPlugin;
    private EndPortalManager portalManager;

    private Map<Player, Long> playersInPortal;

    public EndPortalPlayerTask() {
        this.playersInPortal = new HashMap<>();
    }

    @Override
    public void run() {
        Set<Player> playersToRemove = new HashSet<>();
        for (Map.Entry<Player, Long> entry : playersInPortal.entrySet()) {
            Player player = entry.getKey();
            if (!portalManager.isInPortal(player)) {
                playersToRemove.add(player);
                continue;
            }

            if (entry.getValue() > System.currentTimeMillis()) {
                // do effects

                continue;
            }

            // teleport

            playersToRemove.add(player);
        }

        for (Player player : playersToRemove) {
            playersInPortal.remove(player);
        }
    }

}
