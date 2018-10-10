package commands;

/**
 * Created by Makoiedov.H on 9/7/2017.
 */
public interface Command {
    void execute(String param);
    enum Commands {
        strategy,
        input,
        output,
        search,
        help,
        exit
    }
}
