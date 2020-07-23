package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import net.mcatlas.end.portal.EndPortalManager;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndPortalCommand implements CommandExecutor {

    private EndPlugin endPlugin;

    public EndPortalCommand(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String subCmd = args[0].toLowerCase();

            switch (subCmd) {
                case "create":
                case "createhere":
                    createEndPortal(sender, subCmd);
                    break;
                case "close":
                    closeEndPortal(sender);
                    break;
                case "tp":
                    if (sender instanceof Player) {
                        teleportToPortal((Player) sender);
                    }
                    break;
                default:
                    showValidArgs(sender);
                    break;
            }
        } else {
            showEndPortalInfo(sender);
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
            sender.sendMessage(ChatColor.GOLD + "Next scheduled End Portal opening: " + timeUntil(EndPortalManager.getNextPortalTime()));
        }
    }

    private void createEndPortal(CommandSender sender, String label) {
        if (endPlugin.getEndPortalManager().portalActive()) {
            sender.sendMessage(ChatColor.RED + "There is an active portal. A new one cannot be created.");
            return;
        }

        Location location = null;

        if (label.equals("create")) {
            World portalWorld = endPlugin.getEndPortalManager().getPortalWorld();
            int xBound = endPlugin.getEndPortalManager().getXBound();
            int zBound = endPlugin.getEndPortalManager().getZBound();

            location = WorldUtil.findUnclaimedLocation(portalWorld, xBound, zBound);
        }

        else if (label.equals("createhere")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a Player to run this.");
                return;
            }

            location = ((Player) sender).getLocation();
        }

        if (location == null) {
            sender.sendMessage(ChatColor.RED + "Invalid location.");
            return;
        }

        EndPortal endPortal = endPlugin.getEndPortalManager().create(endPlugin, location);

        sender.sendMessage(ChatColor.GOLD + "Portal created @ (X,Z) " + endPortal.getX() + "," + endPortal.getZ());
    }

    private void closeEndPortal(CommandSender sender) {
        if (!endPlugin.getEndPortalManager().portalActive()) {
            sender.sendMessage(ChatColor.RED + "There isn't an active portal to close.");
            return;
        }

        EndPortal endPortal = endPlugin.getEndPortalManager().getCurrent();

        endPortal.close(endPlugin);

        sender.sendMessage(ChatColor.GOLD + "The portal was closed.");
    }

    private void teleportToPortal(Player player) {
        endPlugin.getEndPortalManager().teleportNearPortal(player);

        player.sendMessage(ChatColor.GOLD + "Teleported near the portal.");
    }

    private void showValidArgs(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Valid arguments [create, createhere, close, tp]");
    }

    private static String timeUntil(long then) {
        long now = System.currentTimeMillis();
        long diff = then - now;

        if (diff < 0) {
            return "?";
        }

        return DurationFormatUtils.formatDurationWords(diff, true, true) + " from now";
    }

}
