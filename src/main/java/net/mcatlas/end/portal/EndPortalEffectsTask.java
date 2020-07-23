package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import org.bukkit.*;

public class EndPortalEffectsTask implements Runnable {

    private EndPlugin endPlugin;
    private EndPortalManager manager;

    private double y;
    private int lightningTimer;

    private static final int LIGHTNING_TIME = 20 * 60 * 3;

    public EndPortalEffectsTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
        this.manager = endPlugin.getEndPortalManager();
        y = 128;
        lightningTimer = LIGHTNING_TIME;
    }

    @Override
    public void run() {
        if (!manager.portalActive()) {
            lightningTimer = LIGHTNING_TIME; // ensure lightning strikes the first time the portal opens
            return;
        }

        y += .6;
        if (y > 255) {
            y = 0;
        }

        World world = manager.getPortalWorld();
        double x = manager.getCurrent().getX();
        double z = manager.getCurrent().getZ();

        lightningTimer++;
        if (lightningTimer > LIGHTNING_TIME) {
            final int xLightning = (int) x;
            final int zLightning = (int) z;
            Bukkit.getScheduler().runTask(endPlugin, () -> {
                world.strikeLightning(world.getHighestBlockAt(xLightning, zLightning).getLocation());
            });
            lightningTimer = 0;
        }

        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 3);

        world.spawnParticle(Particle.VILLAGER_ANGRY, x, y - .4, z, 1);
        world.spawnParticle(Particle.EXPLOSION_NORMAL, x, y - .4, z, 1);

        x += 1.5;
        z += 1.5;

        double yChange = y;
        for (int i = 0; i < 40; i++) {
            if (i < 10) {
                x -= .3;
                yChange -= .08;
            } else if (i < 20) {
                z -= .3;
                yChange += .08;
            } else if (i < 30) {
                x += .3;
                yChange -= .08;
            } else if (i < 40) {
                z += .3;
                yChange += .08;
            }
            for (double separateY = y + 256; y > 0; y -= 30) {
                if (y > 256) {
                    continue;
                }

                world.spawnParticle(Particle.REDSTONE, x, separateY, z, 0, 0, 0, 0, dust);
            }
        }
    }

}
