package net.mcatlas.end;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Objects;
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

    public EndWorld(String id) {
        this.id = id;
        this.createdTime = System.currentTimeMillis();
        // not deleted, so don't assign deletedTime
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

    public boolean isDeleted() {
        return deletedTime != 0;
    }

    public Optional<World> findBukkitWorld() {
        return Bukkit.getWorlds().stream().filter(w -> w.getName().contains(id)).findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndWorld endWorld = (EndWorld) o;
        return createdTime == endWorld.createdTime &&
                deletedTime == endWorld.deletedTime &&
                Objects.equals(id, endWorld.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdTime, deletedTime);
    }

}
