package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import viewModel.ProgramSettingsViewModel;


import java.io.IOException;

/**
 * Created by Makoiedov.H on 11/6/2017.
 */
public class ProgramSettingsView {
    private Stage primaryStage;
    private Scene scene;


    public ProgramSettingsView() {
    }

    /**
     * Method shows program settings window
     */
    public void show(Scene ownerScene) throws IOException {
        primaryStage = new Stage();
        Parent root = FXMLLoader.load(MainView.class.getClassLoader().getResource("view/ProgramSetting.fxml"));
        scene = new Scene(root, 450,180);
        primaryStage.setTitle("Program settings");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initOwner(ownerScene.getWindow());
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.showAndWait();

    }


    @FXML private TextField txtLogsPath;
    @FXML private TextField txtProjectsPath;
    @FXML private TextField txtCacheCount;

    private ProgramSettingsViewModel viewModel;

    /**
     * Gui objects binding
     */
    @FXML
    public void initialize() {
        viewModel = new ProgramSettingsViewModel();
        txtLogsPath.textProperty().bindBidirectional(viewModel.txtLogsPathProperty());
        txtProjectsPath.textProperty().bindBidirectional(viewModel.txtProjectsPathProperty());
        txtCacheCount.textProperty().bindBidirectional(viewModel.txtCacheCountProperty());
    }


    /**
     * Method apples current settings
     * @param event
     */
    public void applySetting(ActionEvent event) {
        try {
            viewModel.applySetting(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method closes program settings window
     * @param event
     */
    public void close(ActionEvent event) {
        viewModel.close(event);
    }

    /**
     * Method initializes logs path as selected directory
     * @param event
     */
    public void setTxtLogsPath(ActionEvent event) {
        viewModel.setTxtLogsPath(event);
    }

    /**
     * Method initializes projects path as selected directory
     * @param event
     */
    public void setTxtProjectsPath(ActionEvent event) {
        viewModel.setTxtProjectsPath(event);
    }
}
