package view;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.*;
import viewModel.MainViewModel;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Makoiedov.H on 10/31/2017.
 */
public class MainView {
    private static Stage primaryStage;
    private static ConnectionModel connectionModel;
    private static boolean onlineMode;
    private static ProjectSettings settings;

    public MainView() {

    }

    /**
     * Parametrized constructor
     * @param connectionModel
     * @param settings
     * @param onlineMode
     */
    public MainView(ConnectionModel connectionModel, ProjectSettings settings, boolean onlineMode) {
        MainView.connectionModel = connectionModel;
        MainView.settings = settings;
        MainView.onlineMode = onlineMode;
    }

    /**
     * Method shows main window
     */
    public void show() {
        primaryStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(MainView.class.getClassLoader().getResource("view/MainView.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 1200,700);

        primaryStage.setTitle("DBbest");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }


    public ConnectionModel getConnectionModel() {
        return connectionModel;
    }

    public void setConnectionModel(ConnectionModel connectionModel) {
        this.connectionModel = connectionModel;
    }

    @FXML private AnchorPane anchorPane;
    @FXML private TreeView<ViewNode> treeView;
    @FXML private TableView<Details> tblVwDetails;
    @FXML private TextField txtSearch;
    @FXML private TableColumn<Details, String> clmAttribute;
    @FXML private TableColumn<Details, String> clmValue;
    @FXML private ProgressIndicator treeProgressIndicator;
    @FXML private MenuItem saveObjectScript;
    @FXML private MenuItem actionSaveObjectScript;
    @FXML private ProgressIndicator statusProgressIndicator;
    @FXML private Label statusMessage;
    @FXML private Label statusExecuted;
    @FXML private MenuItem connectItem;
    @FXML private Button btnCopy;
    @FXML private ScrollPane scrollPane;
    private TextFlow txtSqlScript;

    private MainViewModel viewModel;

    /**
     * Gui objects binding
     */
    @FXML
    public void initialize() {
        txtSqlScript = new TextFlow();
        scrollPane.setContent(txtSqlScript);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        viewModel = new MainViewModel(connectionModel, onlineMode, settings, txtSqlScript);
        viewModel.changeTreePropertyProperty().set(treeView.getSelectionModel());
        viewModel.initializeTreeChangeProperty();
        treeView.rootProperty().bindBidirectional(viewModel.rootItemPropertyProperty());
        treeView.styleProperty().bindBidirectional(viewModel.fontSizePropertyProperty());
        treeView.selectionModelProperty().bindBidirectional(viewModel.changeTreePropertyProperty());
        treeView.onMouseClickedProperty().bindBidirectional((Property)viewModel.mouseEventPropertyProperty());
        treeView.disableProperty().bindBidirectional(viewModel.disableTreeProperty());
        treeProgressIndicator.visibleProperty().bindBidirectional(viewModel.visibleIndicatorPropertyProperty());
        txtSearch.textProperty().bindBidirectional(viewModel.txtSearchProperty());

        tblVwDetails.itemsProperty().bindBidirectional(viewModel.tableViewPropertyProperty());
        tblVwDetails.styleProperty().bindBidirectional(viewModel.fontSizePropertyProperty());
        clmAttribute.cellValueFactoryProperty().bindBidirectional(viewModel.clmAttributePropertyProperty());
        clmValue.cellValueFactoryProperty().bindBidirectional(viewModel.clmValuePropertyProperty());

        saveObjectScript.disableProperty().bindBidirectional(viewModel.disableSaveScriptProperty());
        btnCopy.disableProperty().bindBidirectional(viewModel.disableSaveScriptProperty());
        actionSaveObjectScript.disableProperty().bindBidirectional(viewModel.disableSaveScriptProperty());

        statusProgressIndicator.visibleProperty().bindBidirectional(viewModel.visibleStatusIndicatorProperty());
        statusMessage.textProperty().bindBidirectional(viewModel.txtMessageProperty());
        statusExecuted.visibleProperty().bindBidirectional(viewModel.visibleStatusExecutedProperty());
        connectItem.disableProperty().bindBidirectional(viewModel.onlineModePrpProperty());

        viewModel.setSettings(settings);
    }

    /**
     * Method shows program settings window
     * @param event
     */
    public void showProgramSettings(ActionEvent event) {
        viewModel.showSetting(event, anchorPane.getScene());
    }

    /**
     * Method shows project settings window
     * @param event
     */
    public void showProjectSettings(ActionEvent event) {
        viewModel.showProjectSettings(event);
    }

    /**
     * Method finds a match in the main tree view
     * @param event
     */
    public void search(ActionEvent event) {
        viewModel.search(event, treeView);
    }

    /**
     * Moves the search pointer upward
     * @param event
     */
    public void searchUP(ActionEvent event) {
        viewModel.searchUP(event, treeView);
    }

    /**
     * Moves the search pointer downward
     * @param event
     */
    public void searchDOWN(ActionEvent event) {
        viewModel.searchDOWN(event, treeView);
    }

    /**
     * Method saves project state into selected directory
     * @param event
     */
    public void saveProjectAs(ActionEvent event) {viewModel.saveProjectAs();}

    /**
     * Method saves project state into default directory
     * @param event
     */
    public void saveProject(ActionEvent event) {viewModel.saveProject();}

    /**
     * Method saves object script into selected directory
     * @param event
     */
    public void saveObjectScriptAs(ActionEvent event) {viewModel.saveObjectScriptAs(event);}

    /**
     * Method loads project from saved program state
     * @param event
     */
    public void openProject(ActionEvent event) {viewModel.loadProject(event);}

    /**
     * Method loads all DB entities
     * @param event
     */
    public void loadAll(ActionEvent event) {viewModel.loadAll(event);}

    /**
     * Method reloads selected item
     * @param event
     */
    public void reloadItem(ActionEvent event) {viewModel.reloadItem(event);}

    /**
     * Method saves script of all DB into selected file
     * @param event
     */
    public void saveDBScriptAs(ActionEvent event) {viewModel.saveDBScriptAs(event);}

    /**
     * Method shows info about program
     * @param event
     */
    public void showAbout(ActionEvent event) {viewModel.showAbout(event);}

    /**
     * Method loads selected item
     * @param event
     */
    public void loadItem(ActionEvent event) {viewModel.loadItem();}

    /**
     * Primary stage getter
     * @return primary stage
     */

    /**
     * Method closes current project
     * @param event
     */
    public void closeProject(ActionEvent event) {
        viewModel.closeProject(event);
    }

    /**
     * Method shows help information
     * @param event
     */
    public void showHelp(ActionEvent event) {
        viewModel.showHelp(event);
    }

    /**
     * Method shows connect menu for project
     * @param event
     */
    public void connect(ActionEvent event) {
        viewModel.connect(event);
    }

    /**
     * Method closes the application
     * @param event
     */
    public void exit(ActionEvent event) {
        viewModel.exit(event);
    }

    /**
     * Method copies sql script to clipboard
     * @param event
     */
    public void copyScript(ActionEvent event) {
        viewModel.copyScript(event);
    }


    public static Stage getPrimaryStage() {
        return primaryStage;
    }



}
