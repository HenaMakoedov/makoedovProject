package constants;

public class PathsConstants {
    public static final String HOME_DIRECTORY = System.getProperty("user.home");
    public static final String RELATIVE_MAIN_DIRECTORY = "DBObserver";

    public static final String RELATIVE_CONNECTION_PULL_FILE = "\\connectionPull.xml";
    public static final String RELATIVE_SETTINGS_FILE = "\\Settings.properties";
    public static final String RELATIVE_LOG_FILE = "\\log4j.properties";
    public static final String RELATIVE_RESERVED_WORD_FILE = "\\reservedWords.txt";

    public static final String RELATIVE_CONNECTION_PULL_FILE_TEMPLATE = "/connectionPull.xml";
    public static final String RELATIVE_SETTINGS_FILE_TEMPLATE = "/Setting.properties";
    public static final String RELATIVE_LOG_FILE_TEMPLATE = "/log4j.properties";
    public static final String RELATIVE_RESERVED_WORD_FILE_TEMPLATE = "/reservedWords.txt";

    public static final String DEFAULT_CONNECTION_PULL_FILE = System.getProperty("user.home") + "\\DBObserver\\connectionPull.xml";
    public static final String DEFAULT_SETTINGS_FILE = System.getProperty("user.home") + "\\DBObserver\\Settings.properties";
    public static final String DEFAULT_LOG_FILE = System.getProperty("user.home") + "\\DBObserver\\log4j.properties";
    public static final String DEFAULT_RESERVED_WORD_FILE = System.getProperty("user.home") + "\\DBObserver\\reservedWords.txt";
    public static final String JDBC_PATH = "jdbc:mysql://";

}
