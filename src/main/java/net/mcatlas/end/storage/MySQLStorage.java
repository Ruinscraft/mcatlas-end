package net.mcatlas.end.storage;

import net.mcatlas.end.EndPortal;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQLStorage implements SQLStorage {

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

    public MySQLStorage(String host, int port, String database, String username, String password,
                        String playersTable, String worldsTable, String portalsTable) {
        this.create_players_table = "CREATE TABLE IF NOT EXISTS " + playersTable + " (" +
                "world_name VARCHAR(20) NOT NULL, " +
                "uuid VARCHAR(36) NOT NULL, " +
                "last_online BIGINT, " +
                "UNIQUE (uuid));";
        this.create_worlds_table = "CREATE TABLE IF NOT EXISTS " + worldsTable + " (" +
                "world_name VARCHAR(20) NOT NULL, " +
                "created INT, " +
                "deleted INT, " +
                "UNIQUE (world_name));";
        this.create_portals_table = "CREATE TABLE IF NOT EXISTS " + portalsTable + " (" +
                "world_name VARCHAR(20) NOT NULL, " +
                "x INT NOT NULL, " +
                "z INT NOT NULL, " +
                "portal_close_time INT, " +
                "UNIQUE (world_name));";

        CompletableFuture.runAsync(() -> {
            try (Connection connection = this.getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(create_players_table);
                    statement.execute(create_worlds_table);
                    statement.execute(create_portals_table);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.insert_player = "INSERT INTO " + playersTable + " (world_name, uuid, last_online) VALUES (?, ?, ?);";
        this.update_player = "UPDATE " + playersTable + " SET last_online = ? WHERE uuid = ?;";
        this.delete_player = "DELETE FROM " + playersTable + " WHERE uuid = ?;";
        this.query_players = "SELECT uuid, last_online FROM " + playersTable + " WHERE world_name = ?;";
        this.delete_players = "DELETE FROM " + playersTable + " WHERE world_name = ?;";

        this.insert_world = "INSERT INTO " + worldsTable + " (world_name, created) VALUES (?, ?);"; // maybe will need to be updated
        this.insert_portal = "INSERT INTO " + portalsTable + " (world_name, x, z, portal_close_time) VALUES (?, ?, ?, ?);";
        this.query_portals = "SELECT * FROM " + portalsTable + ";";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("MySQL driver not found");
        }
    }

    @Override
    public CompletableFuture<Void> savePlayer(String uuid, String worldName, long logoutTime) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(insert_player)) {
                    statement.setString(0, worldName);
                    statement.setString(1, uuid);
                    statement.setLong(2, logoutTime);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> updatePlayer(String uuid, long logoutTime) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(update_player)) {
                    statement.setLong(0, logoutTime);
                    statement.setString(1, uuid);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> removePlayer(String uuid) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(delete_player)) {
                    statement.setString(0, uuid);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Map<UUID, Long>> getPlayers(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            Map<UUID, Long> players = new HashMap<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query_players)) {
                    statement.setString(0, worldName);

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
    public CompletableFuture<Void> saveWorld(String worldName, long creationTime) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(insert_world)) {
                    statement.setString(0, worldName);
                    statement.setLong(1, creationTime);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePortal(String worldName, int x, int z, long expiryDate) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(insert_portal)) {
                    statement.setString(0, worldName);
                    statement.setInt(1, x);
                    statement.setInt(2, z);
                    statement.setLong(3, expiryDate);
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Set<EndPortal>> getPortals() {
        return CompletableFuture.supplyAsync(() -> {
            Set<EndPortal> endPortals = new HashSet<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query_portals);
                     ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        String worldName = rs.getString("world_name");
                        int x = rs.getInt("x");
                        int z = rs.getInt("z");
                        long portalCloseTime = rs.getLong("portal_close_time");
                        endPortals.add(new EndPortal(worldName, x, z, portalCloseTime));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return endPortals;
        });
    }

    @Override
    public Connection getConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
