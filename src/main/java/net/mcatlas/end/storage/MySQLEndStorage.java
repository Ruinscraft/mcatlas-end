package net.mcatlas.end.storage;

import net.mcatlas.end.portal.EndPortal;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQLEndStorage implements EndStorage {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    // PLAYER (world_name, uuid, last_online)
    // WORLD (world_name, world creation time, world deletion time, stats??)
    private String create_players_table;
    private String create_worlds_table;
    private String create_portals_table;

    private String insert_player;
    private String update_player;
    private String delete_player;
    private String query_players;
    private String delete_players;

    private String insert_world;
    private String insert_portal;
    private String query_portals;

    public MySQLEndStorage(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        create_players_table = "CREATE TABLE IF NOT EXISTS end_players (" +
                "world_name VARCHAR(20) NOT NULL, " +
                "uuid VARCHAR(36) NOT NULL, " +
                "last_online BIGINT, " +
                "UNIQUE (uuid));";
        create_worlds_table = "CREATE TABLE IF NOT EXISTS end_worlds (" +
                "world_name VARCHAR(20) NOT NULL, " +
                "created INT, " +
                "deleted INT, " +
                "UNIQUE (world_name));";
        create_portals_table = "CREATE TABLE IF NOT EXISTS end_portals (" +
                "world_name VARCHAR(20) NOT NULL, " +
                "x INT NOT NULL, " +
                "z INT NOT NULL, " +
                "portal_close_time INT, " +
                "UNIQUE (world_name));";

        CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(create_players_table);
                    statement.execute(create_worlds_table);
                    statement.execute(create_portals_table);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        insert_player = "INSERT INTO end_players (world_name, uuid, last_online) VALUES (?, ?, ?);";
        update_player = "UPDATE end_players SET last_online = ? WHERE uuid = ?;";
        delete_player = "DELETE FROM end_players WHERE uuid = ?;";
        query_players = "SELECT uuid, last_online FROM end_players WHERE world_name = ?;";
        delete_players = "DELETE FROM end_players WHERE world_name = ?;";

        insert_world = "INSERT INTO end_worlds (world_name, created) VALUES (?, ?);"; // maybe will need to be updated
        insert_portal = "INSERT INTO end_portals (world_name, x, z, portal_close_time) VALUES (?, ?, ?, ?);";
        query_portals = "SELECT * FROM end_portals;";
    }

    @Override
    public CompletableFuture<Void> savePlayer(Player player, long logoutTime) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(insert_player)) {
                    statement.setString(0, player.getLocation().getWorld().getName());
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setLong(2, logoutTime);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> updatePlayer(Player player, long logoutTime) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(update_player)) {
                    statement.setLong(0, logoutTime);
                    statement.setString(1, player.getUniqueId().toString());
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> removePlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(delete_player)) {
                    statement.setString(0, player.getUniqueId().toString());
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Map<UUID, Long>> getPlayers(World world) {
        return CompletableFuture.supplyAsync(() -> {
            Map<UUID, Long> players = new HashMap<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query_players)) {
                    statement.setString(0, world.getName());

                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            UUID uuid = UUID.fromString(rs.getString("uuid"));
                            long lastOnline = rs.getLong("last_online");
                            players.put(uuid, lastOnline);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return players;
        });
    }

    @Override
    public CompletableFuture<Void> clearPlayers(String worldName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(delete_players)) {
                    statement.setString(0, worldName);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveWorld(World world, long creationTime) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(insert_world)) {
                    statement.setString(0, world.getName());
                    statement.setLong(1, creationTime);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePortal(EndPortal portal) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(insert_portal)) {
                    statement.setString(0, portal.getEnd().getName());
                    statement.setInt(1, portal.getX());
                    statement.setInt(2, portal.getZ());
                    statement.setLong(3, portal.getClosingTime());
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<List<EndPortal>> getPortals() {
        return CompletableFuture.supplyAsync(() -> {
            List<EndPortal> portals = new ArrayList<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query_portals);
                     ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        String worldName = rs.getString("world_name");
                        int x = rs.getInt("x");
                        int z = rs.getInt("z");
                        long portalCloseTime = rs.getLong("portal_close_time");
                        World world = Bukkit.getWorld(worldName);

                        if (world != null) {
                            portals.add(new EndPortal(world, x, z, portalCloseTime));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return portals;
        });
    }

    // Close when done
    public Connection getConnection() {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
