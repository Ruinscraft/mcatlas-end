package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.concurrent.TimeUnit;

public class EndPortalEffectsTask implements Runnable {

    private static final long LIGHTNING_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(2);
    private static final double PORTAL_RADIUS = 6D;
    private static final int PARTICLE_COUNT = 30;
    private static final Color PARTICLE_COLOR = Color.BLACK;
    private static final Particle.DustOptions PARTICLE_OPTIONS = new Particle.DustOptions(PARTICLE_COLOR, 3);

    private EndPlugin endPlugin;
    private long nextLightningTime;

    public EndPortalEffectsTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @Override
    public void run() {
        EndPortalManager endPortalManager = endPlugin.getEndPortalManager();

        if (!endPortalManager.portalActive()) {
            return;
        }

        World world = endPortalManager.getPortalWorld();
        int x = endPortalManager.getCurrent().getX();
        int z = endPortalManager.getCurrent().getZ();
        Location portalCenter = world.getHighestBlockAt(x, z).getLocation();

        if (nextLightningTime < System.currentTimeMillis()) {
            // It's time for lightning
            world.strikeLightning(portalCenter);
            nextLightningTime = System.currentTimeMillis() + LIGHTNING_INTERVAL_MILLIS;
        }

        // Draw circle
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            // Make it have some height
            for (int j = 0; j < 10; j++) {
                double angle = 2 * Math.PI * i / PARTICLE_COUNT;
                Location point = portalCenter.clone().add(PORTAL_RADIUS * Math.sin(angle), 0.0d, PORTAL_RADIUS * Math.cos(angle));
                point.add(0, j, 0); // Add the height
                world.spawnParticle(Particle.REDSTONE, point, 1, PARTICLE_OPTIONS);
            }
        }
    }

}
