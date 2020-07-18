package net.mcatlas.end.storage;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLStorage extends Storage {

    Connection getConnection() throws SQLException;

}
