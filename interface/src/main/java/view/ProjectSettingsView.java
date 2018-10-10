package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ProjectSettings;
import viewModel.ProjectSettingsViewModel;

import java.io.IOException;

/**
 * Created by Makoiedov.H on 11/27/2017.
 */
public class ProjectSettingsView {
    private Stage primaryStage;
    private Scene scene;
    private static ProjectSettings projectSettings;

    public ProjectSettingsView() {

    }

    /**
     * Parametrized constructor
     * @param projectSettings
     */
    public ProjectSettingsView(ProjectSettings projectSettings) {
        ProjectSettingsView.projectSettings = projectSettings;
    }

    /**
     * Method shows project settings window
     */
    public void show() throws IOException {
        primaryStage = new Stage();
        Parent root = FXMLLoader.load(MainView.class.getClassLoader().getResource("view/ProjectSettings.fxml"));
        scene = new Scene(root, 750,500);
        primaryStage.setTitle("Project settings");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initOwner(MainView.getPrimaryStage());
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.showAndWait();
    }

    @FXML
    public CheckBox checkBox;
    public ChoiceBox<ProjectSettings.Size> choiceBox;
    private ProjectSettingsViewModel viewModel;

    /**
     * Gui objects binding
     */
    @FXML
    public void initialize() {
        viewModel = new ProjectSettingsViewModel(ProjectSettingsView.projectSettings);
        checkBox.selectedProperty().bindBidirectional(viewModel.checkBoxColorScriptProperty());
        viewModel.choiceBoxFontSizeModelProperty().setValue(choiceBox.getSelectionModel());
        choiceBox.itemsProperty().bindBidirectional(viewModel.choiceBoxItemsProperty());
        choiceBox.selectionModelProperty().bindBidirectional(viewModel.choiceBoxFontSizeModelProperty());
        viewModel.fillChoiceBox();
    }

    /**
     * Method applies establishes settings
     * @param event
     */
    public void apply(ActionEvent event) {
        viewModel.apply(event);
    }

    /**
     * Method closes settings window
     * @param event
     */
    public void cancel(ActionEvent event) {
        viewModel.cancel(event);
    }

}
