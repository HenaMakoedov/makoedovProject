import consoleLogic.ConsoleReceiver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import treerealization.Tree;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

/**
 * Created by Makoiedov.H on 9/8/2017.
 */
public class TestConsoleReceiver {
   ByteArrayOutputStream outputContent = new ByteArrayOutputStream();
   ConsoleReceiver receiver = new ConsoleReceiver();
   Tree tree;


    @Before
    public void setUpStreams() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("tmp.xml").getFile());
        receiver.setStrategy("xml");
        receiver.input(file.getAbsolutePath());
        tree = receiver.getTree();
        System.setOut(new PrintStream(outputContent));
    }

    @Test
    public void attrDepthSearchTest() {
        receiver.attrDepthSearch("number", "two");
        Assert.assertTrue(outputContent.toString().contains("name='second'"));
    }

    @Test
    public void nameWideSearchTest() {
        receiver.nameWideSearch("second");
        Assert.assertTrue(outputContent.toString().contains("name='second'"));
    }

    @Test
    public void attrWideSearchTest() {
        receiver.attrWideSearch("number", "two");
        Assert.assertTrue(outputContent.toString().contains("name='second'"));
    }

    @Test
    public void nameDepthSearchTest() {
        receiver.nameDepthSearch("second");
        Assert.assertTrue(outputContent.toString().contains("name='second'"));
    }
}