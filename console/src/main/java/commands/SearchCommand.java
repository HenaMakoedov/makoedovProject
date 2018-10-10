package commands;

import consoleLogic.ConsoleReceiver;

/**
 * Created by Makoiedov.H on 9/8/2017.
 */
public class SearchCommand extends AbstractCommand {

    public SearchCommand(ConsoleReceiver receiver) {
        super(receiver);
    }

    /**
     * This method finds node of tree
     * depending on the search algorithm and search criteria
     * and displays the data about the found node to the console
     * @param param parameters of command
     */
    public void execute(String param) {
        double startTime = System.nanoTime();
        if (receiver.getTree() == null) {
            System.out.println("Search command error.");
            System.out.println("Please, deserialize tree from file,");
            System.out.println("with command -input \"filePath\". ");
            return;
        }

        String[] split = param.split(" ");
        if (split[0].equals("-wide")) {
            if (split[1].equals("-name")) {
                receiver.nameWideSearch(split[2]);
            }
            else if (split[1].equals("-attr")) {
                receiver.attrWideSearch(split[2], split[3]);
            }
            else {
                System.out.println("Search command Error.");
                System.out.println(split[1] + " is't criteria of search. ");
                return;
            }

        }
        else if (split[0].equals("-depth")) {
            if (split[1].equals("-name")) {
                receiver.nameDepthSearch(split[2]);
            }
            else if (split[1].equals("-attr")) {
                receiver.attrDepthSearch(split[2], split[3]);
            }
            else {
                System.out.println("Search command Error.");
                System.out.println(split[1] + " is't criteria of search. ");
                return;
            }
        }
        else {
            System.out.println("Search command Error.");
            System.out.println(split[0] + " is't algorithm of search. ");
            return;
        }
        System.out.println("Search command. OK ");
        double finishTime = System.nanoTime();
        double executeTimeInSec = ((finishTime - startTime) / Math.pow(10, 9));

        System.out.print("method execution time: ");
        System.out.printf("%.9f", executeTimeInSec);
        System.out.println(" sec. \n");
    }
}
