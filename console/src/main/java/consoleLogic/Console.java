package consoleLogic;

import commands.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Makoiedov.H on 9/7/2017.
 */
public class Console {
    private ConsoleReceiver receiver;
    private ConsoleInvoker invoker;

    public Console() {
        receiver = new ConsoleReceiver();
        invoker = new ConsoleInvoker(
                new SetStrategyCommand(receiver),
                new InputCommand(receiver),
                new OutputCommand(receiver),
                new HelpCommand(receiver),
                new SearchCommand(receiver));
    }

    /**
     * checks if the string is a command of the console
     * @param command
     * @return param it's command?
     */
    private boolean isCommand(String command) {
        for (Command.Commands c: Command.Commands.values()) {
            if (command.equals("-" + c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method launches console for execution.
     * Reads from the console line, next builds map
     * where - key it's command, value - sets of parameters for each command
     * for transfer this map to consoleInvoker,
     * which will determine what to do with a specific command
     */
    public void execute() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s = null;

        while (true) {
            Map<String, List<String>> commandsWithParams = new LinkedHashMap<String, List<String>>();
            try {
                s = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] split = s.split(" ");

            //build our map from readLine string
            //for further transfer to the ConsoleInvoker
            for(int i = 0; i < split.length;) {
                String key;
                StringBuilder value = new StringBuilder();
                if (isCommand(split[i])) {

                    //leave from this method and close the console
                    if (("-" + Command.Commands.exit).equals(split[i])) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    key = split[i];

                    //if command doesn't have any parameters
                    if (i + 1 == split.length || isCommand(split[i + 1]) ) {

                        //exit command it's one command which doesn't have parameters
                        if (("-" + Command.Commands.help).equals(split[i])) {
                            invoker.executeHelpCommand();
                        } else {
                            System.out.println("don't found parameters for " + split[i] + " command");
                        }
                        i++;
                        continue;

                    } else {
                        //build string for our list in the map
                        int count = 1;
                        while ((i + count) < split.length && (!isCommand(split[i + count]))) {
                            value.append(split[i + count] + " ");
                            count++;
                        }
                        /////////////////////////////
                        i += count;
                    }
                } else {
                    System.out.println(split[i] + " is not command");
                    i++;
                    continue;
                }
                if(commandsWithParams.containsKey(key)) {
                    commandsWithParams.get(key).add(value.toString());
                }   else {
                    List<String> valueList = new ArrayList<String>();
                    valueList.add(value.toString());
                    commandsWithParams.put(key, valueList);
                }
            }
            invoker.executeCommand(commandsWithParams);
        }
    }

    public static void main(String[] args) {
        Console console = new Console();
        console.execute();
    }
}
