package db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Makoiedov.H on 9/21/2017.
 */
public class DBManager {
    private DBConnectionStrategy strategy;

    public void setStrategy(DBConnectionStrategy strategy) {
        this.strategy = strategy;
    }

    public Connection getConnection(String URL, String user, String password) throws SQLException, ClassNotFoundException {
            return strategy.getConnection(URL, user, password);
    }
}
