package viewModel;

import constants.PathsConstants;
import constants.SettingsConstants;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.log4j.*;
import service.ConnectionCacheManager;

import java.io.*;
import java.util.Properties;

/**
 * Created by Makoiedov.H on 11/7/2017.
 */
public class ProgramSettingsViewModel {
    private static final Logger logger = Logger.getLogger(ProjectSettingsViewModel.class);
    private final SimpleStringProperty txtLogsPath = new SimpleStringProperty();
    private final SimpleStringProperty txtProjectsPath = new SimpleStringProperty();
    private final SimpleStringProperty txtCacheCount = new SimpleStringProperty();

    /**
     * Default constructor, initializes program settings
     */
    public ProgramSettingsViewModel() {
        InputStream inputStream = null;
        Properties properties = new Properties();
        File file = new File(PathsConstants.DEFAULT_SETTINGS_FILE);
        try {
            inputStream = new FileInputStream(file.getAbsolutePath());
            properties.load(inputStream);
            logger.info("initialize saved paths");
            txtLogsPath.set(properties.getProperty(SettingsConstants.LOG_PATH));
            txtProjectsPath.set(properties.getProperty(SettingsConstants.PROJECT_PATH));
            txtCacheCount.set(properties.getProperty(SettingsConstants.CACHE_COUNT));
            inputStream.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Method apples current settings
     * @param event
     */
    public void applySetting(ActionEvent event) throws IOException {
        logger.info("apply settings");
        File logsDirectory = new File(txtLogsPath.get());
        File projectsDirectory = new File(txtProjectsPath.get());

        String errorMassage = "";
        boolean isErrorSetting = false;

        if (!logsDirectory.isDirectory()) {
            isErrorSetting = true;
            errorMassage += "Error logs path \n";
        }
        if (!projectsDirectory.isDirectory()) {
            isErrorSetting = true;
            errorMassage += "Error project path \n";
        }
        try {
            int cacheCount = Integer.parseInt(txtCacheCount.get());
            if (cacheCount < 0 || cacheCount > 100) {
                isErrorSetting = true;
                errorMassage += "Count must be beatween 0 and 100 \n";
            }
        } catch (Exception e) {
            isErrorSetting = true;
            errorMassage += "Count must be a number \n";
        }

        if(isErrorSetting) {
            logger.warn("entered the incorrect settings");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error settings");
            alert.setHeaderText("You have a error settings");
            alert.setContentText(errorMassage);
            alert.showAndWait();
        }

        else {
            Properties properties = new Properties();
            File propertyFile = new File(PathsConstants.DEFAULT_SETTINGS_FILE);
            try {
                FileWriter writer = new FileWriter(propertyFile);
                properties.setProperty(SettingsConstants.LOG_PATH, txtLogsPath.get());
                properties.setProperty(SettingsConstants.PROJECT_PATH, txtProjectsPath.get());
                properties.setProperty(SettingsConstants.CACHE_COUNT, txtCacheCount.get());
                properties.store(writer, "ProjectSettings");
                writer.close();
                changeLogsPath();
                Node node = (Node)event.getSource();
                Stage stage = (Stage)node.getScene().getWindow();
                stage.close();
                logger.info("new settings applied");
            } catch (Exception e) {
                logger.error(e);
            }

            ConnectionCacheManager cacheManager = ConnectionCacheManager.getInstance();
            cacheManager.resize(Integer.parseInt(txtCacheCount.get()));
            cacheManager.saveConnectionRecords();
            logger.info("resize connection pull size");
        }
    }

    /**
     * Method changes logs path
     */
    private void changeLogsPath() {
        Properties properties = new Properties();
        File propertyFile = new File(PathsConstants.DEFAULT_LOG_FILE);
        try {
            properties.load(new FileReader(propertyFile));
            FileWriter writer = new FileWriter(propertyFile);
            properties.setProperty(SettingsConstants.APPENDER, txtLogsPath.get() + "/logs.log");
            properties.store(writer, "projectSettings");
            writer.close();
            PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p [%t]: %m%n");
            FileAppender appender = new FileAppender(layout, txtLogsPath.get() + "/logs.log", false);
            logger.addAppender(appender);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.error(e);
        }
        PropertyConfigurator.configure(properties);
    }

    /**
     * Method sets up log path using directoryChooser
     * @param event
     */
    public void setTxtLogsPath(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select default logs folder");
        File selectedDirectory = chooser.showDialog(new Stage());

        if (selectedDirectory == null) {
            return;
        }

        txtLogsPath.set(selectedDirectory.getAbsolutePath());
    }

    /**
     * Method sets up project path using directoryChooser
     * @param event
     */
    public void setTxtProjectsPath(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select default logs folder");
        File selectedDirectory = chooser.showDialog(new Stage());

        if (selectedDirectory == null) {
            return;
        }

        txtProjectsPath.set(selectedDirectory.getAbsolutePath());
    }

    /**
     * Method closes program settings window
     * @param event
     */
    public void close(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }


    public String getTxtLogsPath() {
        return txtLogsPath.get();
    }

    public SimpleStringProperty txtLogsPathProperty() {
        return txtLogsPath;
    }

    public void setTxtLogsPath(String txtLogsPath) {
        this.txtLogsPath.set(txtLogsPath);
    }

    public String getTxtProjectsPath() {
        return txtProjectsPath.get();
    }

    public SimpleStringProperty txtProjectsPathProperty() {
        return txtProjectsPath;
    }

    public void setTxtProjectsPath(String txtProjectsPath) {
        this.txtProjectsPath.set(txtProjectsPath);
    }

    public String getTxtCacheCount() {
        return txtCacheCount.get();
    }

    public SimpleStringProperty txtCacheCountProperty() {
        return txtCacheCount;
    }

    public void setTxtCacheCount(String txtCacheCount) {
        this.txtCacheCount.set(txtCacheCount);
    }
}
