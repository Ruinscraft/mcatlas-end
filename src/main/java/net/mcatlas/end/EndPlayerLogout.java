package net.mcatlas.end;

import java.util.UUID;

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

}
