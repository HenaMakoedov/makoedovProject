package commands;

import consoleLogic.ConsoleReceiver;

/**
 * Created by Makoiedov.H on 9/7/2017.
 */
public class InputCommand extends AbstractCommand {
    public InputCommand(ConsoleReceiver receiver) {
        super(receiver);
    }

    public void execute(String param) {
        double startTime = System.nanoTime();
        receiver.input(param);
        double finishTime = System.nanoTime();
        double executeTimeInSec = ((finishTime - startTime) / Math.pow(10, 9));

        System.out.print("method execution time: ");
        System.out.printf("%.9f", executeTimeInSec);
        System.out.println(" sec. \n");
    }
}
