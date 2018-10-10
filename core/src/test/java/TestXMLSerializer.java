import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;
import serializers.XMLSerializer;
import treerealization.Node;
import treerealization.Tree;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Makoiedov.H on 9/6/2017.
 */
public class TestXMLSerializer {

    XMLSerializer serializer = new XMLSerializer();

    @Test
    public void testDeserialize() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("tmp.xml").getFile());
        try {
            Tree deserializeTree = serializer.deserialize(file);
            Assert.assertTrue(true);
        } catch (IOException e) {
            Assert.fail("IOException");
        } catch (SAXException e) {
            Assert.fail("SAXException");
        }

    }

    @Test
    public void TestSerialize() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("number", "one");
        map1.put("position", "root");

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("number", "two");

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("number", "tree");

        Map<String, String> map4 = new HashMap<String, String>();
        map4.put("number", "four");

        Map<String, String> map5 = new HashMap<String, String>();
        map5.put("number", "five");

        Map<String, String> map6 = new HashMap<String, String>();
        map6.put("number", "six");

        Map<String, String> map7 = new HashMap<String, String>();
        map7.put("number", "seven");

        Node node1 = new Node("root", map1);
        Node node2 = new Node("second", map2);
        Node node3 = new Node("third", map3);
        Node node4 = new Node("fourth", map4);
        Node node5 = new Node("fifth", map5);
        Node node6 = new Node("sixth", map6);
        Node node7 = new Node("seventh", map7);

        node1.getChildren().add(node2);
        node1.getChildren().add(node3);
        node1.getChildren().add(node4);

        node2.getChildren().add(node6);
        node2.getChildren().add(node5);
        node3.getChildren().add(node7);

        Tree tree = new Tree(node1);

        try {
            serializer.serialize(tree, new File("src\\main\\resources\\tmp.xml"));
            Assert.assertTrue(true);
        } catch (ParserConfigurationException e) {
            Assert.fail("ParserConfigurationException");
        } catch (TransformerException e) {
            Assert.fail("TransformerException");
        } catch (IOException e) {
            Assert.fail("IOException e");
        }
    }
}
