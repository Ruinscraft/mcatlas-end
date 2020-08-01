package net.mcatlas.end;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashSet;
import java.util.Set;

public class PVPCoolDownListener implements Listener {

    private EndPlugin endPlugin;
    private Set<Player> onGrace;

    public PVPCoolDownListener(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
        onGrace = new HashSet<>();
    }

    @EventHandler
    public void onEndPortalTeleport(EndPortalTeleportEvent event) {
        Player player = event.getPlayer();

        long delayTicks = 20 * 30L;

        player.sendMessage(ChatColor.GOLD + "You have a 30 second PVP grace period (in the End).");

        onGrace.add(player);

        endPlugin.getServer().getScheduler().runTaskLater(endPlugin, () -> {
            onGrace.remove(player);

            if (player.isOnline()) {
                player.sendMessage(ChatColor.GOLD + "Your PVP grace period has ended.");
            }
        }, delayTicks);
    }

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player
                && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();

            if (damaged.getWorld().getEnvironment() != World.Environment.THE_END) {
                return;
            }

            if (onGrace.contains(damaged)) {
                damager.sendMessage(ChatColor.RED + damaged.getName() + " is protected by their grace period.");

                event.setCancelled(true);
            }
        }

    }

}
