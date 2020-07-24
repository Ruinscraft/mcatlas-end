package net.mcatlas.end;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class EndWorldsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "Currently active end worlds:");

        Set<World> endWorlds = WorldUtil.getBukkitEndWorlds();

        if (endWorlds.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "None.");
        }

        for (World world : WorldUtil.getBukkitEndWorlds()) {
            String name = world.getName();
            String playerList = String.join(", ", world.getPlayers().stream().map(Player::getName).collect(Collectors.toSet()));

            sender.sendMessage(ChatColor.GOLD + name + ": " + playerList);
        }

        return true;
    }

}
