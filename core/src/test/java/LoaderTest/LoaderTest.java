package LoaderTest;

import db.MainLoader;
import db.MainPrinter;
import db.MySQLConnectionStrategy;
import strategypattern.SerializableClient;
import strategypattern.XMLSerializableStrategy;
import treerealization.Tree;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;


/**
 * Created by Makoiedov.H on 10/5/2017.
 */
public class LoaderTest {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://dev-mysql.cjj06khxetlc.us-west-2.rds.amazonaws.com:3306";
        String user = "min_privs";
        String pass = "min_privs";
        String schemaName = "test_mysql";

        //dev-mysql.cjj06khxetlc.us-west-2.rds.amazonaws.com:3306
        //min_privs
        //test_mysql

        Connection conn = DriverManager.getConnection(url, user, pass);

        MainLoader mainLoader = new MainLoader(url, user, pass, new MySQLConnectionStrategy());
        Tree tree = mainLoader.getDB(schemaName);

        SerializableClient client = new SerializableClient();
        client.setSerializableStrategy(new XMLSerializableStrategy());
        client.performSerialize(tree, new File("refactorTest.xml"));
    }
}

