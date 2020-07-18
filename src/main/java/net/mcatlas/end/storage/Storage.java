package net.mcatlas.end.storage;

import java.util.Map;
import java.util.UUID;

public interface Storage {

    // put logout time of player for world on logout
    void savePlayer(String uuid, String worldName, long logoutTime);
    // update logout time
    void updatePlayer(String uuid, long logoutTime);
    // remove player entry on world leave
    void removePlayer(String uuid);
    // get players in world
    Map<UUID, Long> getPlayers(String worldName);
    // remove all entries for world on world deletion
    void clearPlayers(String worldName);

    // put world in world table on creation
    void saveWorld(String worldName, long creationTime);
    // add stats on server shutdown and on world deletion?

    // put portal
    void savePortal(String worldName, int x, int z, long expiryDate);
    // get portal list
    /* gotta make an object for this */


}
