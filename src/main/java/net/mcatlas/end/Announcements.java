package net.mcatlas.end;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Announcements {

    public static void announcePortalOpen() {
        Bukkit.broadcastMessage(ChatColor.BLACK + "A portal to a new dimension has opened.");
    }

    public static void announcePortalClose() {
        Bukkit.broadcastMessage(ChatColor.BLACK + "The portal to a different dimension has shut.");
    }

}
