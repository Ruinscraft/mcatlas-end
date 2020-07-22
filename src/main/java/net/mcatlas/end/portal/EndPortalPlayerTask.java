package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import net.mcatlas.end.WorldUtil;
import org.bukkit.*;
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

            // in portal, not time to teleport yet
            if (entry.getValue() > System.currentTimeMillis()) {
                Location location = player.getLocation();
                // do effects
                player.playSound(location, Sound.AMBIENT_CAVE, SoundCategory.NEUTRAL, 10, 2);

                for (int i = 0; i < 5; i++) {
                    Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1);

                    if (WorldUtil.RANDOM.nextInt(5) == 1) {
                        dust = new Particle.DustOptions(Color.fromRGB(250, 250, 128), 1);
                    }

                    player.spawnParticle(Particle.REDSTONE,
                            location.clone().add(
                                    WorldUtil.RANDOM.nextDouble(), WorldUtil.RANDOM.nextDouble(), WorldUtil.RANDOM.nextDouble()
                            ),
                            0, 0, 0, 0, dust);
                }

                continue;
            }

            // teleport
            portalManager.getCurrent().getEndWorld().findBukkitWorld().ifPresent(world -> {
               player.teleport(world.getSpawnLocation());
            });

            playersToRemove.add(player);
        }

        for (Player player : playersToRemove) {
            playersInPortal.remove(player);
        }
    }

}
