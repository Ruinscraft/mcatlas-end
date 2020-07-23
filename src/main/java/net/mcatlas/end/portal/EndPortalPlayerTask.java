package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EndPortalPlayerTask implements Runnable {

    private static final long TIME_BEFORE_TELEPORT_MILLIS = TimeUnit.SECONDS.toMillis(10);
    private static final Color COLOR_BLACK = Color.fromRGB(255, 255, 255);
    private static final Color COLOR_PURPLE = Color.fromRGB(250, 250, 128);
    private static final Particle.DustOptions PARTICLE_OPTIONS_BLACK = new Particle.DustOptions(COLOR_BLACK, 1);
    private static final Particle.DustOptions PARTICLE_OPTIONS_PURPLE = new Particle.DustOptions(COLOR_PURPLE, 1);

    private static final Random RANDOM = new Random();

    private EndPlugin endPlugin;
    private Map<Player, Long> portalTeleportTimes;

    public EndPortalPlayerTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
        portalTeleportTimes = new HashMap<>();
    }

    @Override
    public void run() {
        EndPortalManager endPortalManager = endPlugin.getEndPortalManager();

        if (!endPortalManager.portalActive()) {
            portalTeleportTimes.clear();
            return;
        }

        EndPortal endPortal = endPortalManager.getCurrent();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (endPortalManager.isInPortalArea(player)) {
                if (portalTeleportTimes.containsKey(player)) {
                    if (portalTeleportTimes.get(player) < System.currentTimeMillis()) {
                        // It's time to teleport
                        endPortal.getEndWorld().teleportPlayer(player);
                    } else {
                        // It's not time to teleport
                        playEffects(player);
                    }
                } else {
                    portalTeleportTimes.put(player, System.currentTimeMillis() + TIME_BEFORE_TELEPORT_MILLIS);
                }
            } else {
                if (portalTeleportTimes.containsKey(player)) {
                    portalTeleportTimes.remove(player);
                }
            }
        }
    }

    private void playEffects(Player player) {
        Location origin = player.getLocation();

        player.playSound(origin, Sound.AMBIENT_CAVE, SoundCategory.NEUTRAL, 10, 2);

        for (int i = 0; i < 10; i++) {
            Particle.DustOptions options;

            if (RANDOM.nextInt(5) == 1) {
                options = PARTICLE_OPTIONS_PURPLE;
            } else {
                options = PARTICLE_OPTIONS_BLACK;
            }

            double xAdd = RANDOM.nextDouble();
            double yAdd = RANDOM.nextDouble();
            double zAdd = RANDOM.nextDouble();

            Location particleLocation = origin.clone().add(xAdd, yAdd, zAdd);

            player.spawnParticle(Particle.REDSTONE, particleLocation, 1, options);
        }
    }

}
