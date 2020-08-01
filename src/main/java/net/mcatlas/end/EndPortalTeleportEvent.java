package net.mcatlas.end;

import net.mcatlas.end.portal.EndPortal;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class EndPortalTeleportEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private EndPortal endPortal;

    public EndPortalTeleportEvent(Player who, EndPortal endPortal) {
        super(who);
        this.endPortal = endPortal;
    }

    public EndPortal getEndPortal() {
        return endPortal;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
