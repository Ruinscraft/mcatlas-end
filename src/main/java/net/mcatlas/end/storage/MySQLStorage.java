package net.mcatlas.end.storage;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLStorage implements SQLStorage {

    private HikariDataSource dataSource;

    // PLAYER (world_name, uuid, last_online)
    // WORLD (world_name, various stats that royal wants i don't know)
    private String create_players_table;
    private String create_worlds_table;

    public MySQLStorage(String host, int port,
                        String database, String username, String password, String playersTable, String worldsTable) {
        this.create_players_table = "CREATE TABLE IF NOT EXISTS " + playersTable + " (world_name VARCHAR(20), uuid VARCHAR(36), last_online BIGINT)";
        this.create_worlds_table = "CREATE TABLE IF NOT EXISTS " + worldsTable + " (world_name VARCHAR(20))"; // more

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
