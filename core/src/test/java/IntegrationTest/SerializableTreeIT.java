package IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import strategypattern.SerializableClient;
import strategypattern.XMLSerializableStrategy;
import treerealization.Tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Makoiedov.H on 9/13/2017.
 */
public class SerializableTreeIT {
    Tree tree;
    SerializableClient client = new SerializableClient();

    @Test
    public void serializableTreeIT() {
        client.setSerializableStrategy(new XMLSerializableStrategy());
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("tmp.xml").getFile());
        try {
            tree = client.performDeserialize(file);
            client.performSerialize(tree, new File("tmp2.xml"));

            BufferedReader readerInputFile = new BufferedReader(new FileReader(file));
            BufferedReader readerOutputFile = new BufferedReader(new FileReader("tmp2.xml"));

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
        } catch (Exception e) {
            Assert.fail("don't found file with xml tree");
        }
    }
}