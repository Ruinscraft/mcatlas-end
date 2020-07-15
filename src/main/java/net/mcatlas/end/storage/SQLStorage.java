package net.mcatlas.end.storage;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLStorage extends Storage {

    void createTables() throws SQLException;

    Connection getConnection() throws SQLException;

}
