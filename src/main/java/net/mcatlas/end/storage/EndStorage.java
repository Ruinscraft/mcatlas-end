package net.mcatlas.end.storage;

import net.mcatlas.end.EndPlayerLogout;
import net.mcatlas.end.EndWorld;
import net.mcatlas.end.portal.EndPortal;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EndStorage {

    /*
     *  End Worlds
     */
    CompletableFuture<Void> saveEndWorld(EndWorld endWorld);

    CompletableFuture<List<EndWorld>> queryEndWorlds();

    CompletableFuture<EndWorld> queryEndWorld(String id);

    /*
     *  End Portals
     */
    CompletableFuture<Void> saveEndPortal(EndPortal endPortal);

    CompletableFuture<List<EndPortal>> queryEndPortals();

    /*
     *  End Player Logouts
     */
    CompletableFuture<Void> saveEndPlayerLogout(EndPlayerLogout endPlayerLogout);

    CompletableFuture<List<EndPlayerLogout>> queryEndPlayerLogouts();

    CompletableFuture<List<EndPlayerLogout>> queryEndPlayerLogouts(EndWorld endWorld);

    CompletableFuture<Void> deleteEndPlayerLogouts(EndWorld endWorld);

}
