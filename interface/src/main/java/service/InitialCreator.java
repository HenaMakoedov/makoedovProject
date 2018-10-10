package service;

import constants.PathsConstants;
import constants.SettingsConstants;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;

import java.io.*;
import java.util.Properties;

/**
 * Created by Makoiedov.H on 12/18/2017.
 */
public class InitialCreator {
    private static Logger logger = Logger.getLogger(InitialCreator.class);

    /**
     * Method creates missing program files
     */
    public static void createProgramFiles() {
        File filesDirectory = new File(PathsConstants.HOME_DIRECTORY);
        File programDirectory = new File(filesDirectory, PathsConstants.RELATIVE_MAIN_DIRECTORY);
        if (!programDirectory.exists()) {
            programDirectory.mkdirs();
        }
        File pullFile = new File(programDirectory + PathsConstants.RELATIVE_CONNECTION_PULL_FILE);
        File settingsFile = new File(programDirectory + PathsConstants.RELATIVE_SETTINGS_FILE);
        File logSettingsFile = new File(programDirectory + PathsConstants.RELATIVE_LOG_FILE);
        File reservedWordsFile = new File(programDirectory + PathsConstants.RELATIVE_RESERVED_WORD_FILE);
        try {
            if (!pullFile.exists()) {
                pullFile.createNewFile();
                InputStream pullStream = InitialCreator.class.getResourceAsStream(PathsConstants.RELATIVE_CONNECTION_PULL_FILE_TEMPLATE);
                FileUtils.copyInputStreamToFile(pullStream, pullFile);
            }
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
                InputStream settingsStream = InitialCreator.class.getResourceAsStream(PathsConstants.RELATIVE_SETTINGS_FILE_TEMPLATE);
                FileUtils.copyInputStreamToFile(settingsStream, settingsFile);

                Properties properties = new Properties();
                File propertyFile = new File(PathsConstants.DEFAULT_SETTINGS_FILE);
                FileWriter writer = new FileWriter(propertyFile);
                properties.setProperty(SettingsConstants.LOG_PATH, programDirectory.getAbsolutePath());
                properties.setProperty(SettingsConstants.PROJECT_PATH, programDirectory.getAbsolutePath());
                properties.setProperty(SettingsConstants.CACHE_COUNT, "10");
                properties.store(writer, "ProjectSettings");
                writer.close();
            }
            if (!logSettingsFile.exists()) {
                logSettingsFile.createNewFile();
                InputStream logSettingsStream = InitialCreator.class.getResourceAsStream(PathsConstants.RELATIVE_LOG_FILE_TEMPLATE);
                FileUtils.copyInputStreamToFile(logSettingsStream, logSettingsFile);

                Properties properties = new Properties();
                File propertyFile = new File(PathsConstants.DEFAULT_LOG_FILE);
                try {
                    properties.load(new FileReader(propertyFile));
                    FileWriter writer = new FileWriter(propertyFile);
                    properties.setProperty(SettingsConstants.APPENDER, programDirectory.getAbsolutePath() + "/logs.log");
                    properties.store(writer, "projectSettings");
                    writer.close();
                    PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p [%t]: %m%n");
                    FileAppender appender = new FileAppender(layout, programDirectory.getAbsolutePath() + "/logs.log", false);
                    logger.addAppender(appender);
                    logger.setLevel(Level.INFO);
                } catch (IOException e) {
                    logger.error(e);
                }
                PropertyConfigurator.configure(properties);
            }
            if (!reservedWordsFile.exists()) {
                reservedWordsFile.createNewFile();
                InputStream reservedWordsStream = InitialCreator.class.getResourceAsStream(PathsConstants.RELATIVE_RESERVED_WORD_FILE_TEMPLATE);
                FileUtils.copyInputStreamToFile(reservedWordsStream, reservedWordsFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(logSettingsFile.getAbsolutePath());
    }
}
