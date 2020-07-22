package net.mcatlas.end;

import net.mcatlas.end.world.EndWorld;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EndPlayerLogout {

    private EndWorld endWorld;
    private UUID mojangId;
    private long logoutTime;

    public EndPlayerLogout(EndWorld endWorld, UUID mojangId, long logoutTime) {
        this.endWorld = endWorld;
        this.mojangId = mojangId;
        this.logoutTime = logoutTime;
    }

    public EndWorld getEndWorld() {
        return endWorld;
    }

    public UUID getMojangId() {
        return mojangId;
    }

    public long getLogoutTime() {
        return logoutTime;
    }

    public boolean expired() {
        // has it been 15 minutes since the logout time?
        return logoutTime + TimeUnit.MINUTES.toMillis(15) < System.currentTimeMillis();
    }

}
