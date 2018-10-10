package viewModel;


import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.stage.Stage;
import model.ProjectSettings;
import org.apache.log4j.Logger;


/**
 * Created by Makoiedov.H on 11/27/2017.
 */
public class ProjectSettingsViewModel {
    private static final Logger logger = Logger.getLogger(ProjectSettingsViewModel.class);
    private final SimpleObjectProperty<ObservableList<ProjectSettings.Size>> choiceBoxItems = new SimpleObjectProperty<>();
    private final Property<SingleSelectionModel<ProjectSettings.Size>> choiceBoxFontSizeModel = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty checkBoxColorScript = new SimpleBooleanProperty();

    private ProjectSettings projectSettings;
    public ProjectSettingsViewModel(ProjectSettings projectSettings) {
        this.projectSettings = projectSettings;
    }

    /**
     * Method fills choice box using types of font size
     */
    public void fillChoiceBox() {
        logger.info("fill choice box");
        ObservableList<ProjectSettings.Size> list = FXCollections.observableArrayList();
        list.add(ProjectSettings.Size.SMALL);
        list.add(ProjectSettings.Size.MEDIUM);
        list.add(ProjectSettings.Size.BIG);
        choiceBoxItems.set(list);
        choiceBoxFontSizeModel.getValue().select(projectSettings.getSize());
        checkBoxColorScript.set(projectSettings.isColorScript());
    }

    /**
     * Method closes project settings window
     * @param event
     */
    public void cancel(ActionEvent event) {
        logger.info("close project settings menu");
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    /**
     * Method applies new project settings
     * @param event
     */
    public void apply(ActionEvent event) {
        logger.info("apply settings");
        projectSettings.setColorScript(checkBoxColorScript.get());
        projectSettings.setSize(choiceBoxFontSizeModel.getValue().getSelectedItem());
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public ObservableList<ProjectSettings.Size> getChoiceBoxItems() {
        return choiceBoxItems.get();
    }

    public SimpleObjectProperty<ObservableList<ProjectSettings.Size>> choiceBoxItemsProperty() {
        return choiceBoxItems;
    }

    public Property<SingleSelectionModel<ProjectSettings.Size>> choiceBoxFontSizeModelProperty() {
        return choiceBoxFontSizeModel;
    }

    public boolean isCheckBoxColorScript() {
        return checkBoxColorScript.get();
    }

    public SimpleBooleanProperty checkBoxColorScriptProperty() {
        return checkBoxColorScript;
    }
}
