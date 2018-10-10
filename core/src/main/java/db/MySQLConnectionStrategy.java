package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Makoiedov.H on 9/21/2017.
 */
public class MySQLConnectionStrategy implements DBConnectionStrategy {

    public Connection getConnection(String URL, String user, String password) throws SQLException, ClassNotFoundException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, user, password);
    }
}
