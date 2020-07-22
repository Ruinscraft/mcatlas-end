package net.mcatlas.end.storage;

import net.mcatlas.end.EndPlayerLogout;
import net.mcatlas.end.world.EndWorld;
import net.mcatlas.end.portal.EndPortal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLEndStorage implements EndStorage {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MySQLEndStorage(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("CREATE TABLE IF NOT EXISTS end_worlds (id VARCHAR(8), created_time BIGINT, deleted_time BIGINT DEFAULT 0, PRIMARY KEY (id));");
                    statement.execute("CREATE TABLE IF NOT EXISTS end_portals (world_id VARCHAR(8), world_x INT, world_z INT, close_time BIGINT, UNIQUE (world_id), FOREIGN KEY (world_id) REFERENCES end_worlds (id));");
                    statement.execute("CREATE TABLE IF NOT EXISTS end_player_logouts (world_id VARCHAR(8), mojang_uuid VARCHAR(36), logout_time BIGINT, UNIQUE KEY (world_id, mojang_uuid), FOREIGN KEY (world_id) REFERENCES end_worlds (id));");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveEndWorld(EndWorld endWorld) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement upsert = connection.prepareStatement("INSERT INTO end_worlds (id, created_time) VALUES (?, ?) ON DUPLICATE KEY UPDATE created_time = ?, deleted_time = ?;")) {
                    upsert.setString(1, endWorld.getId()); // for initial insert
                    upsert.setLong(2, endWorld.getCreatedTime()); // for initial insert
                    upsert.setLong(3, endWorld.getCreatedTime()); // on duplicate key
                    upsert.setLong(4, endWorld.getDeletedTime()); // on duplicate key
                    upsert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<List<EndWorld>> queryEndWorlds() {
        return CompletableFuture.supplyAsync(() -> {
            List<EndWorld> endWorlds = new ArrayList<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM end_worlds;")) {
                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) {
                            String id = result.getString("id");
                            long createdTime = result.getLong("created_time");
                            long deletedTime = result.getLong("deleted_time");
                            EndWorld endWorld = new EndWorld(id, createdTime, deletedTime);

                            endWorlds.add(endWorld);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return endWorlds;
        });
    }

    @Override
    public CompletableFuture<List<EndWorld>> queryUndeletedEndWorlds() {
        return CompletableFuture.supplyAsync(() -> {
           List<EndWorld> endWorlds = new ArrayList<>();

           try (Connection connection = getConnection()) {
               try (PreparedStatement query = connection.prepareStatement("SELECT * FROM end_worlds WHERE deleted_time = 0 OR deleted_time IS NULL;")) {
                   try (ResultSet result = query.executeQuery()) {
                       while (result.next()) {
                           String worldId = result.getString("id");
                           long createdTime = result.getLong("created_time");
                           long deletedTime = 0; // duh
                           EndWorld endWorld = new EndWorld(worldId, createdTime, deletedTime);

                           endWorlds.add(endWorld);
                       }
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }

            return endWorlds;
        });
    }

    @Override
    public CompletableFuture<Optional<EndWorld>> queryEndWorld(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM end_worlds WHERE id = ?;")) {
                    query.setString(1, id);

                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) {
                            long createdTime = result.getLong("created_time");
                            long deletedTime = result.getLong("deleted_time");
                            EndWorld endWorld = new EndWorld(id, createdTime, deletedTime);

                            return Optional.of(endWorld);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> saveEndPortal(EndPortal endPortal) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO end_portals (world_id, world_x, world_z, close_time) VALUES (?, ?, ?, ?);")) {
                    insert.setString(1, endPortal.getEndWorld().getId());
                    insert.setInt(2, endPortal.getX());
                    insert.setInt(3, endPortal.getZ());
                    insert.setLong(4, endPortal.getCloseTime());
                    insert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<List<EndPortal>> queryEndPortals() {
        return CompletableFuture.supplyAsync(() -> {
            List<EndPortal> endPortals = new ArrayList<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM end_portals INNER JOIN end_worlds ON end_portals.world_id = end_worlds.id;")) {
                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) {
                            String worldId = result.getString("world_id");
                            long worldCreatedTime = result.getLong("created_time");
                            long worldDeletedTime = result.getLong("deleted_time");
                            int x = result.getInt("world_x");
                            int z = result.getInt("world_z");
                            long closeTime = result.getLong("close_time");
                            EndWorld endWorld = new EndWorld(worldId, worldCreatedTime, worldDeletedTime);
                            EndPortal endPortal = new EndPortal(endWorld, x, z, closeTime);

                            endPortals.add(endPortal);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return endPortals;
        });
    }

    @Override
    public CompletableFuture<Optional<EndPortal>> queryOpenPortal() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM end_portals INNER JOIN end_worlds ON end_portals.world_id = end_worlds.id WHERE close_time > ? LIMIT 1")) {
                    query.setLong(1, System.currentTimeMillis());

                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) {
                            String worldId = result.getString("world_id");
                            long worldCreatedTime = result.getLong("created_time");
                            long worldDeletedTime = result.getLong("deleted_time");
                            int x = result.getInt("world_x");
                            int z = result.getInt("world_z");
                            long closeTime = result.getLong("close_time");
                            EndWorld endWorld = new EndWorld(worldId, worldCreatedTime, worldDeletedTime);
                            EndPortal endPortal = new EndPortal(endWorld, x, z, closeTime);

                            return Optional.of(endPortal);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> saveEndPlayerLogout(EndPlayerLogout endPlayerLogout) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement upsert = connection.prepareStatement("INSERT INTO end_player_logouts (world_id, mojang_uuid, logout_time) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE logout_time = ?;")) {
                    upsert.setString(1, endPlayerLogout.getEndWorld().getId()); // on insert
                    upsert.setString(2, endPlayerLogout.getMojangId().toString()); // on insert
                    upsert.setLong(3, endPlayerLogout.getLogoutTime()); // on insert
                    upsert.setLong(4, endPlayerLogout.getLogoutTime()); // on duplicate key
                    upsert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<List<EndPlayerLogout>> queryEndPlayerLogouts() {
        return CompletableFuture.supplyAsync(() -> {
            List<EndPlayerLogout> endPlayerLogouts = new ArrayList<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM end_player_logouts INNER JOIN end_worlds ON end_player_logouts.world_id = end_worlds.id;")) {
                    try (ResultSet result = query.executeQuery()) {
                        String worldId = result.getString("world_id");
                        long worldCreatedTime = result.getLong("created_time");
                        long worldDeletedTime = result.getLong("deleted_time");
                        UUID mojangId = UUID.fromString(result.getString("mojang_uuid"));
                        long logoutTime = result.getLong("logout_time");
                        EndWorld endWorld = new EndWorld(worldId, worldCreatedTime, worldDeletedTime);
                        EndPlayerLogout endPlayerLogout = new EndPlayerLogout(endWorld, mojangId, logoutTime);

                        endPlayerLogouts.add(endPlayerLogout);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return endPlayerLogouts;
        });
    }

    @Override
    public CompletableFuture<List<EndPlayerLogout>> queryEndPlayerLogouts(EndWorld endWorld) {
        return CompletableFuture.supplyAsync(() -> {
            List<EndPlayerLogout> endPlayerLogouts = new ArrayList<>();

            try (Connection connection = getConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM end_player_logouts WHERE world_id = ?;")) {
                    query.setString(1, endWorld.getId());

                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) {
                            UUID mojangId = UUID.fromString(result.getString("mojang_uuid"));
                            long logoutTime = result.getLong("logout_time");
                            EndPlayerLogout endPlayerLogout = new EndPlayerLogout(endWorld, mojangId, logoutTime);

                            endPlayerLogouts.add(endPlayerLogout);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return endPlayerLogouts;
        });
    }

    @Override
    public CompletableFuture<Void> deleteEndPlayerLogouts(EndWorld endWorld) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM end_player_logouts WHERE world_id = ?;")) {
                    delete.setString(1, endWorld.getId());
                    delete.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteEndPlayerLogouts(UUID mojangId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM end_player_logouts WHERE mojang_uuid = ?;")) {
                    delete.setString(1, mojangId.toString());
                    delete.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private Connection getConnection() {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
