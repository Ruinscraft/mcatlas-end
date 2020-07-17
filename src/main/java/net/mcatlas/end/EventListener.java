package net.mcatlas.end;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    public void onPlayerQuit(PlayerQuitEvent event) {
        // if player is in end world, put last login time
    }

    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        // remove player entry if world left was an end world

        // cancel event if going to closed end world somehow
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        // check if 1st world and is near/in portal area
        // teleport to world spawn of current end world if so
    }

}
