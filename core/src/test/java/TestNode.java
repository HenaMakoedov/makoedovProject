import org.junit.Assert;
import org.junit.Test;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 9/5/2017.
 */
public class TestNode {

    @Test
    public void defaultConstructorTest(){
        Node node = new Node();
        Assert.assertNotNull(node.getAttributes());
        Assert.assertNotNull(node.getChildren());
    }

    @Test
    public void settersTest(){
        Node node = new Node();

        node.setChildren(null);
        node.setAttributes(null);
        node.setName(null);
        node.setParent(null);
        node.setRoot(null);

        Assert.assertNull(node.getAttributes());
        Assert.assertNull(node.getChildren());
        Assert.assertNull(node.getName());
        Assert.assertNull(node.getParent());
        Assert.assertNotNull(node.getRoot());
    }

    @Test
    public void hashCodeTest() {
        Assert.assertTrue(!new Integer(new Node().hashCode()).toString().isEmpty());
    }

    @Test
    public void toStringTest() {
        Assert.assertTrue(new Node().toString() instanceof String);
    }
}
