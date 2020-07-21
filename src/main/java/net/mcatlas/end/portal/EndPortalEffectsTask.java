package net.mcatlas.end.portal;

import net.mcatlas.end.EndPlugin;

public class EndPortalEffectsTask implements Runnable {

    private EndPlugin endPlugin;
    private EndPortalManager manager;
    private double y;

    public EndPortalEffectsTask(EndPlugin endPlugin) {
        this.endPlugin = endPlugin;
        this.manager = endPlugin.getEndPortalManager();
        y = 128;
    }

    @Override
    public void run() {
        if (!manager.portalActive()) return;


    }

}
