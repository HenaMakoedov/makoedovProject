import consoleLogic.Console;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Created by Makoiedov.H on 9/12/2017.
 */
public class TestConsole {

    ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("tmp.xml").getFile());

    String setCommands = "-help -strategy xml -input " +
            file.getAbsolutePath() +
            " -output tmp6.xml" +
            " -search -depth -name sixth\n -exit";
    ByteArrayInputStream inContent = new ByteArrayInputStream(setCommands.getBytes());


    @Before
    public void setUpStreams() {
        System.setIn(new BufferedInputStream(inContent));
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void executeTest() {
        Console console = new Console();
        console.execute();

        Assert.assertTrue(outContent.toString().contains("Help command. OK"));
        Assert.assertTrue(outContent.toString().contains("strategy command. OK"));
        Assert.assertTrue(outContent.toString().contains("input command. OK"));
        Assert.assertTrue(outContent.toString().contains("output command. OK"));
        Assert.assertTrue(outContent.toString().contains("Search command. OK"));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setIn(null);
    }
}
