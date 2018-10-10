package IntegrationTest;

import db.MainLoader;
import db.MainPrinter;
import db.MySQLConnectionStrategy;
import org.junit.Assert;
import org.junit.Test;
import treerealization.Tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Makoiedov.H on 10/26/2017.
 */

public class DataBaseIT {

    @Test
    public void dataBaseIT() throws SQLException, IOException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306?useSSL=false&autoReconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String user = "root";
        String pass = null;
        String schemaName = "sakila";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("DDL.sql").getFile());

        MainLoader mainLoader = new MainLoader(url, user, pass, new MySQLConnectionStrategy());
        Tree tree = mainLoader.getDB(schemaName);

        MainPrinter printer = new MainPrinter();
        printer.createDDLScript(tree, new File("DDL2.sql"));

        BufferedReader readerInputFile = new BufferedReader(new FileReader(file));
        BufferedReader readerOutputFile = new BufferedReader(new FileReader("DDL2.sql"));

        while (true) {
            String stringInputFile = readerInputFile.readLine();
            String stringOutputFile = readerOutputFile.readLine();

            if (stringInputFile == null && stringOutputFile == null) {
                break;
            }
            if (!stringInputFile.equals(stringOutputFile)) {
                Assert.fail("different strings");
            }
        }
        Assert.assertTrue(true);
    }
}
