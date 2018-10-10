package commands;
import consoleLogic.ConsoleReceiver;

/**
 * Created by Makoiedov.H on 9/7/2017.
 */


/**
 * This class is necessary for association
 * the common logic of all command classes
 */
public abstract class AbstractCommand implements Command {
    ConsoleReceiver receiver;

    /**
     * Parametrized constructor used in all command classes
     * @param receiver
     */
    public AbstractCommand(ConsoleReceiver receiver) {
        this.receiver = receiver;
    }

    /**
     * Method executes specific command
     * @param param parameters of command
     */
    abstract public void execute(String param);
}
