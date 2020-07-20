package net.mcatlas.end;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // if player is in end world, put last login time
        if (EndPlugin.isInEndWorld(player)) {
            EndPlugin.get().getStorage().savePlayer(player.getUniqueId().toString(),
                    player.getWorld().getName(), System.currentTimeMillis());
        }
    }

    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        // remove player entry if world left was an end world
        World left = event.getFrom();
        if (EndPlugin.isEndWorld(left)) {
            EndPlugin.get().getStorage().removePlayer(event.getPlayer().getUniqueId().toString());
        }

        // cancel event if going to closed end world somehow (need to add method for checking)
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        if (!EndPlugin.isInOverworld(event.getPlayer())) return;

        // check if is near/in portal area
        // teleport to world spawn of current end world if so
    }

}
