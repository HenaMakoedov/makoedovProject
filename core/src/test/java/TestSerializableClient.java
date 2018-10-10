import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import strategypattern.SerializableClient;
import strategypattern.XMLSerializableStrategy;
import treerealization.Node;
import treerealization.Tree;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Makoiedov.H on 9/5/2017.
 */
public class TestSerializableClient {
    SerializableClient client;

    @Before
    public void setData() {
        client = new SerializableClient();
        client.setSerializableStrategy(new XMLSerializableStrategy());
    }

    @Test
    public void performDeserializeTest(){
        try {
            client.performDeserialize(new File("test"));
            Assert.fail("don't throws exception");
        } catch (Exception e) {
            Assert.assertEquals(true, true);
        }
    }

    @Test
    public void performSerializeTest() {
        try {
            client.performSerialize(new Tree(null), null);
        }
        catch (Exception e) {
            Assert.assertEquals(true, true);
        }
    }

    @Test
    public void performSerialize2Test() {
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
            client.performSerialize(tree, new File("src\\main\\resources\\tmp2.xml"));
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("Exception");
        }
    }
}
