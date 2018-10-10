import commands.Command;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Makoiedov.H on 9/12/2017.
 */
public class TestCommands {

    @Test
    public void testEnum() {
        Assert.assertEquals("input", Command.Commands.input.name());
    }
}
