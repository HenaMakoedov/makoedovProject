package view;


import constants.PathsConstants;
import constants.SettingsConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.ProjectSettings;
import org.apache.log4j.*;
import service.HelpShower;
import service.InitialCreator;
import service.ProjectLoader;

import java.io.*;
import java.util.Properties;


/**
 * Created by Makoiedov.H on 11/29/2017.
 */
public class StartView extends Application {
    private static final Logger logger = Logger.getLogger(StartView.class);
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        InitialCreator.createProgramFiles();
        logger.info("start application");
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/StartView.fxml"));
        scene = new Scene(root, 600,220);

        primaryStage.setTitle("Welcome");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Method shows create project window
     * @param event
     */
    public void createProject(ActionEvent event) {
        ConnectionView connectionView = new ConnectionView();
        try {
            logger.info("open create project menu");
            connectionView.show(event);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Method shows open project window
     * @param event
     */
    public void openProject(ActionEvent event) throws Exception {
        logger.info("Open Project");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Project");

        InputStream inputStream = null;
        Properties properties = new Properties();
        File settingsFile = new File(PathsConstants.DEFAULT_SETTINGS_FILE);

        String projectPath = null;
        try {
            logger.info("find default project path");
            inputStream = new FileInputStream(settingsFile.getAbsolutePath());
            properties.load(inputStream);
            projectPath = properties.getProperty(SettingsConstants.PROJECT_PATH);
            logger.info("project path has found");
            inputStream.close();
        } catch (Exception e) {
            logger.warn(e);
        }
        if (projectPath == null) {
            logger.error("project path has not found");
            return;
        }

        directoryChooser.setInitialDirectory(new File(projectPath));
        Node src = (Node)event.getSource();
        Stage welStage = (Stage)src.getScene().getWindow();
        File directory = directoryChooser.showDialog(welStage);
        if (directory == null) {
            return;
        }

        ProjectSettings projectSettings = ProjectLoader.loadProject(directory);

        if (projectSettings != null) {
            new Thread(() -> {
                if (projectSettings == null || projectSettings.getRootItem() == null) {
                    logger.warn("error project folder");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Project open Error");
                    alert.setHeaderText("Incorrect project folder");
                    alert.setContentText("Please, choose valid folder");
                    alert.showAndWait();
                }
                logger.info("opening project");
                MainView mainView = new MainView(projectSettings.getConnectionModel(), projectSettings, false);
                Node source = (Node)event.getSource();
                Stage stage = (Stage)source.getScene().getWindow();
                Platform.runLater(() -> {
                    stage.close();
                    mainView.show();
                    MainView.getPrimaryStage().setTitle(directory.getAbsolutePath());
                });
            }).start();
        }
        else {
            logger.warn("error project folder");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error project folder");
            alert.setHeaderText("Error project directory");
            alert.setContentText("Please, choose valid project folder");
            alert.showAndWait();
        }

    }

    /**
     * Method shows program settings window
     * @param event
     */
    public void openSettings(ActionEvent event) {
        try {
            logger.info("open settings");
            ProgramSettingsView settingView = new ProgramSettingsView();
            Node source = (Node)event.getSource();
            Scene ownerScene = source.getScene();
            settingView.show(ownerScene);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Method shows info about program
     * @param event
     */
    public void openAbout(ActionEvent event) {
        logger.info("open about menu");
        HelpShower.showAbout();
    }

    /**
     * Method closes program
     * @param event
     */
    public void exit(ActionEvent event) {
        logger.info("close program");
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    /**
     * Scene getter
     * @return scene of start view
     */
    public static Scene getScene() {
        return scene;
    }
}
