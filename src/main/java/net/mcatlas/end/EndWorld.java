package net.mcatlas.end;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

public class EndWorld {

    private String id;
    private long createdTime;
    private long deletedTime;

    public EndWorld(String id, long createdTime, long deletedTime) {
        this.id = id;
        this.createdTime = createdTime;
        this.deletedTime = deletedTime;
    }

    public String getId() {
        return id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getDeletedTime() {
        return deletedTime;
    }

    public Optional<World> findBukkitWorld() {
        return Bukkit.getWorlds().stream().filter(w -> w.getName().contains(id)).findFirst();
    }

}
