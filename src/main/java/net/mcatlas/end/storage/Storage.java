package net.mcatlas.end.storage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    // put logout time of player for world on logout
    CompletableFuture<Void> savePlayer(String uuid, String worldName, long logoutTime);
    // update logout time
    CompletableFuture<Void> updatePlayer(String uuid, long logoutTime);
    // remove player entry on world leave
    CompletableFuture<Void> removePlayer(String uuid);
    // get players in world
    CompletableFuture<Map<UUID, Long>> getPlayers(String worldName);
    // remove all entries for world on world deletion
    CompletableFuture<Void> clearPlayers(String worldName);

    // put world in world table on creation
    CompletableFuture<Void> saveWorld(String worldName, long creationTime);
    // add stats on server shutdown and on world deletion?

    // put portal
    CompletableFuture<Void> savePortal(String worldName, int x, int z, long expiryDate);
    // get portal list
    /* gotta make an object for this */


}
