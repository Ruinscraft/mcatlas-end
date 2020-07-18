package net.mcatlas.end.storage;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class MySQLStorage implements SQLStorage {

    private HikariDataSource dataSource;

    // PLAYER (world_name, uuid, last_online)
    // WORLD (world_name, world creation time, world deletion time, stats??)
    private String create_players_table;
    private String create_worlds_table;
    private String create_portals_table;

    public MySQLStorage(String host, int port, String database, String username, String password,
                        String playersTable, String worldsTable, String portalsTable) {
        this.create_players_table = "CREATE TABLE IF NOT EXISTS " + playersTable + " (world_name VARCHAR(20), uuid VARCHAR(36), last_online BIGINT);";
        this.create_worlds_table = "CREATE TABLE IF NOT EXISTS " + worldsTable + " (world_name VARCHAR(20), created INT, deleted INT)"; // more
        this.create_portals_table = "CREATE TABLE IF NOT EXISTS " + portalsTable + " (world_name VARCHAR(20), x INT, z INT, portal_close_time INT);";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("MySQL driver not found");
        }

        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setPoolName("mcatlas-end-pool");
        dataSource.setMaximumPoolSize(3);
        dataSource.setConnectionTimeout(3000);
        dataSource.setLeakDetectionThreshold(3000);
    }

    @Override
    public void savePlayer(String uuid, String worldName, long logoutTime) {

    }

    @Override
    public void updatePlayer(String uuid, long logoutTime) {

    }

    @Override
    public void removePlayer(String uuid) {

    }

    @Override
    public Map<UUID, Long> getPlayers(String worldName) {
        return null;
    }

    @Override
    public void clearPlayers(String worldName) {

    }

    @Override
    public void saveWorld(String worldName, long creationTime) {

    }

    @Override
    public void savePortal(String worldName, int x, int z, long expiryDate) {

    }

    @Override
    public void createTables() throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement update = c.prepareStatement(create_players_table);
             PreparedStatement update2 = c.prepareStatement(create_worlds_table)) {
            update.execute();
            update2.execute();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
