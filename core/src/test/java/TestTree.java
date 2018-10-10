import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import treerealization.Node;
import treerealization.Tree;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Makoiedov.H on 9/5/2017.
 */
public class TestTree {
    private Tree tree;
    private Node node1;
    private Node node2;
    private Node node3;
    private Node node4;
    private Node node5;
    private Node node6;
    private Node node7;


    @Before
    public void setData(){
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

        node1 = new Node("root", map1);
        node2 = new Node("second", map2);
        node3 = new Node("third", map3);
        node4 = new Node("fourth", map4);
        node5 = new Node("fifth", map5);
        node6 = new Node("sixth", map6);
        node7 = new Node("seventh", map7);

        node1.getChildren().add(node2);
        node1.getChildren().add(node3);
        node1.getChildren().add(node4);

        node2.getChildren().add(node6);
        node2.getChildren().add(node5);
        node3.getChildren().add(node7);

        this.tree = new Tree(node1);
    }

    @Test
    public void nameWideSearchTest() {

        Assert.assertEquals(node4, tree.nameWideSearch("fourth"));
        Assert.assertEquals(null, tree.nameWideSearch("asdf"));
    }

    @Test
    public void nameDepthSearchTest() {
        Assert.assertEquals(node4, tree.nameDepthSearch("fourth"));
        Assert.assertEquals(null, tree.nameDepthSearch("asdf"));
    }

    @Test
    public void attrWideSearchTest() {
        Assert.assertEquals(node5, tree.attrWideSearch("number", "five"));
        Assert.assertEquals(null, tree.attrWideSearch("number", "eleven"));
    }

    @Test
    public void attrDepthSearchTest() {
        Assert.assertEquals(node5, tree.attrDepthSearch("number", "five"));
        Assert.assertEquals(null, tree.attrDepthSearch("number", "eleven"));
    }

    @Test
    public void getRootTest() {
        Assert.assertEquals(node1, tree.getRoot());
    }

    @Test
    public void equalsTest() {
        Assert.assertTrue(tree.equals(tree));
    }

    @Test
    public void defaultConstructorTest() {
        Assert.assertEquals(new Tree(null), new Tree());
    }

    @Test
    public void setRootTest() {
        Tree tmpTree = new Tree();
        tmpTree.setRoot(tree.getRoot());
        Assert.assertEquals(tree, tmpTree);
    }

    @Test
    public void attrDepthSearch2Test() {
        Assert.assertEquals(null, tree.attrDepthSearch("test", "test"));
    }

    @Test
    public void toStringTest() {
        Assert.assertTrue(new Tree().toString() instanceof String);
    }

    @Test
    public void hashCodeTest() {
        Assert.assertTrue(!new Integer(new Tree().hashCode()).toString().isEmpty());
    }
}
