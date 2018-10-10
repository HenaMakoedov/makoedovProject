package service;

import constants.SettingsConstants;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Makoiedov.H on 11/30/2017.
 */
public class HelpShower {

    /**
     * Method shows info about program
     */
    public static void showAbout() {
        InputStream inputStream = null;
        Properties properties = new Properties();
        ClassLoader classLoader = HelpShower.class.getClassLoader();
        String about = null;
        try {
            inputStream = classLoader.getResourceAsStream("About.properties");
            properties.load(inputStream);
            about = properties.getProperty(SettingsConstants.ABOUT);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("About");
        aboutAlert.setHeaderText(null);
        aboutAlert.setContentText(about);
        aboutAlert.showAndWait();
    }
}
