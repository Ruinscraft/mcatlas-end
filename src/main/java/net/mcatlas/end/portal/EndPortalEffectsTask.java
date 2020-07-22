package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class EndPortalEffectsTask implements Runnable {

    private EndPlugin endPlugin;
    private EndPortalManager manager;

    private double y;
    private int lightningTimer;

    public EndPortalEffectsTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
        this.manager = endPlugin.getEndPortalManager();
        y = 128;
        lightningTimer = 20 * 60 * 5;
    }

    @Override
    public void run() {
        if (!manager.portalActive()) return;

        y += .6;
        if (y > 255) {
            y = 0;
        }

        World world = manager.getPortalWorld();
        double x = manager.getCurrent().getX();
        double z = manager.getCurrent().getZ();

        lightningTimer++;
        if (lightningTimer > 20 * 60 * 5) {
            world.strikeLightning(world.getHighestBlockAt((int) x, (int) z).getLocation());
            lightningTimer = 0;
        }

        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1);

        world.spawnParticle(Particle.VILLAGER_ANGRY, x, y - .4, z, 1);

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
            world.spawnParticle(Particle.REDSTONE, x, yChange, z, 0, 0, 0, 0, dust);
        }
    }

}
