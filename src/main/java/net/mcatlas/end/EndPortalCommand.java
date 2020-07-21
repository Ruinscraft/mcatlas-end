package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import net.mcatlas.end.portal.EndPortalManager;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EndPortalCommand implements CommandExecutor {

    private EndPlugin endPlugin;

    public EndPortalCommand(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showEndPortalInfo(sender);
            return true;
        }





        return true;
    }

    private void showEndPortalInfo(CommandSender sender) {
        EndPortalManager endPortalManager = endPlugin.getEndPortalManager();

        if (endPortalManager.portalActive()) {
            EndPortal current = endPortalManager.getCurrent();

            sender.sendMessage(ChatColor.GOLD + "There is an active End Portal:");
            sender.sendMessage(ChatColor.GOLD + "Coordinates (X,Z): " + current.getX() + "," + current.getZ());
            sender.sendMessage(ChatColor.GOLD + "Associated End World ID: " + current.getEndWorld().getId());
            sender.sendMessage(ChatColor.GOLD + "Close time: " + timeUntil(current.getCloseTime()));
        } else {
            sender.sendMessage(ChatColor.GOLD + "There is currently no End Portal active.");
            sender.sendMessage(ChatColor.GOLD + "Next scheduled End Portal opening: " + timeUntil(endPlugin.getEndWorldCheckerTask().getNextCreationTime()));
        }
    }

    private String timeUntil(long then) {
        long now = System.currentTimeMillis();
        long diff = then - now;

        if (diff < 0) {
            return "?";
        }

        return DurationFormatUtils.formatDurationWords(diff, true, true) + " from now";
    }

}
