package net.mcatlas.end.storage;

import net.mcatlas.end.portal.EndPortal;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EndStorage {

    // put logout time of player for world on logout
    CompletableFuture<Void> savePlayer(Player player, long logoutTime);

    // update logout time
    CompletableFuture<Void> updatePlayer(Player player, long logoutTime);

    // remove player entry on world leave
    CompletableFuture<Void> removePlayer(Player player);

    // get players in world
    CompletableFuture<Map<UUID, Long>> getPlayers(World world);

    // remove all entries for world on world deletion
    CompletableFuture<Void> clearPlayers(String worldName);

    // put world in world table on creation
    CompletableFuture<Void> saveWorld(World world, long creationTime);
    // add stats on server shutdown and on world deletion?

    // put portal
    CompletableFuture<Void> savePortal(EndPortal endPortal);

    // get portal list
    CompletableFuture<List<EndPortal>> getPortals();

}
