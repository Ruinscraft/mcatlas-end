package net.mcatlas.end;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.management.ManagementFactory;

public class EndPlugin extends JavaPlugin {

    public static final long STARTUP_TIME;

    static {
        STARTUP_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public static World createWorld() {
        
        return null;
    }

}

