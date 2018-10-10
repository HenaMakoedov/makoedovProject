package consoleLogic;

import commands.AbstractCommand;

import java.util.List;
import java.util.Map;

/**
 * Created by Makoiedov.H on 9/7/2017.
 */
public class ConsoleInvoker {
    private AbstractCommand setStrategy;
    private AbstractCommand input;
    private AbstractCommand output;
    private AbstractCommand help;
    private AbstractCommand search;

    public ConsoleInvoker(AbstractCommand setStrategy, AbstractCommand input,
                          AbstractCommand output, AbstractCommand help,
                          AbstractCommand search) {
        this.setStrategy = setStrategy;
        this.input = input;
        this.output = output;
        this.help = help;
        this.search = search;
    }


    public void executeHelpCommand() {
        help.execute(null);
    }


    /**
     * Execute certain command depending on the type
     * @param commandsWithParam
     * @key of map - our command
     * @value of map - list of sets parameters for certain command
     * if our key contain value list with a length of more than 1
     * this means that one command will be executed several times
     * with different sets of parameters.
     */
    public void executeCommand(Map<String, List<String>> commandsWithParam) {
       for(Map.Entry<String, List<String>> pair : commandsWithParam.entrySet()) {
           String key = pair.getKey();
           List<String> value = pair.getValue();

           for (String str : value) {
               String[] split = str.split(" ");

               switch (key) {
                   case "-strategy":
                       setStrategy.execute(split[0]);
                       break;
                   case "-input":
                       input.execute(split[0]);
                       break;
                   case "-output":
                       output.execute(split[0]);
                       break;
                   case "-search":
                       search.execute(str);
                       break;
                   default:
                       break;
               }
           }
       }
    }
}
