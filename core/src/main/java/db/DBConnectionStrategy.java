package db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Makoiedov.H on 9/21/2017.
 */
public interface DBConnectionStrategy {
    Connection getConnection(String URL, String user, String password) throws SQLException, ClassNotFoundException;
}
