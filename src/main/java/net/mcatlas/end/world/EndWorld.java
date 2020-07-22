package net.mcatlas.end.world;

import net.mcatlas.end.EndPlugin;
import net.mcatlas.end.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

public class EndWorld {

    public static final String END_WORLD_PREFIX = "end_world_";

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

    public String getWorldName() {
        return END_WORLD_PREFIX + id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(long deletedTime) {
        this.deletedTime = deletedTime;
    }

    public boolean isDeleted() {
        return deletedTime != 0;
    }

    public Optional<World> findBukkitWorld() {
        return Bukkit.getWorlds().stream().filter(w -> w.getName().equals(getWorldName())).findFirst();
    }

    public void teleportPlayer(Player player) {
        findBukkitWorld().ifPresent(world -> {
            player.teleport(world.getSpawnLocation());
        });
    }

    public boolean worldLoaded() {
        return findBukkitWorld().isPresent();
    }

    public void loadWorld(EndPlugin endPlugin) {
        WorldUtil.createBukkitEndWorld(endPlugin, this);
    }

    public boolean hasActivePortal(EndPlugin endPlugin) {
        if (endPlugin.getEndPortalManager().portalActive()) {
            return endPlugin.getEndPortalManager().getCurrent().getEndWorld().equals(this);
        }

        return false;
    }

    public int onlinePlayerCount() {
        if (!worldLoaded()) {
            return 0;
        }

        return findBukkitWorld().get().getPlayers().size();
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
