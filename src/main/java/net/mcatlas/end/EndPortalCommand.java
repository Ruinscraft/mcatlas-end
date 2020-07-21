package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import net.mcatlas.end.portal.EndPortalManager;
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
            sender.sendMessage(ChatColor.GOLD + "Close time: " + current.getCloseTime()); // TODO: format human readable
        } else {
            sender.sendMessage(ChatColor.RED + "There is currently no End Portal active.");
        }
    }

}
