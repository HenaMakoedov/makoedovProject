package viewModel;


import constants.MessageConstants;
import constants.PathsConstants;
import constants.SettingsConstants;
import db.DBManager;
import db.MainLoader;
import db.MainPrinter;
import db.MySQLConnectionStrategy;
import db.constants.DBObjects;
import db.loader.LoadManager;
import db.printer.PrintManager;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;
import org.apache.log4j.Logger;
import service.HelpShower;
import service.ProjectLoader;
import service.ProjectSaver;
import service.TextPainter;
import treerealization.Node;
import treerealization.Tree;
import view.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;


/**
 * Created by Makoiedov.H on 11/8/2017.
 */
public class MainViewModel {
    private static final Logger logger = Logger.getLogger(MainViewModel.class);
    private final SimpleObjectProperty<Border> borderAnchorPaneProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<TreeItem<ViewNode>> rootItemProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<MultipleSelectionModel<TreeItem<ViewNode>>> changeTreeProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<EventHandler<MouseEvent>> mouseEventProperty = new SimpleObjectProperty<>();
    private final SimpleStringProperty txtSearch = new SimpleStringProperty();
    private final SimpleBooleanProperty visibleIndicatorProperty = new SimpleBooleanProperty();
    private final SimpleObjectProperty<ObservableList<Details>> tableViewProperty = new SimpleObjectProperty<>(FXCollections.emptyObservableList());
    private final Property<Callback<TableColumn.CellDataFeatures<Details, String>, ObservableValue<String>>> clmAttributeProperty = new SimpleObjectProperty<>();
    private final Property<Callback<TableColumn.CellDataFeatures<Details, String>, ObservableValue<String>>> clmValueProperty = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty disableTree = new SimpleBooleanProperty();
    private final SimpleBooleanProperty disableSaveScript = new SimpleBooleanProperty();
    private final SimpleStringProperty fontSizeProperty = new SimpleStringProperty();
    private final SimpleBooleanProperty visibleStatusIndicator = new SimpleBooleanProperty();
    private final SimpleBooleanProperty visibleStatusExecuted = new SimpleBooleanProperty();
    private final SimpleStringProperty txtMessage = new SimpleStringProperty();
    private final SimpleBooleanProperty onlineModePrp = new SimpleBooleanProperty();


    private LoadManager loadManager;
    private PrintManager printManager;
    private CollectionDetails collectionDetails;
    private String tmpStringSearch = "";
    private List<TreeItem<ViewNode>> selectionItemsList;
    private int selectCount;

    private ConnectionModel connectionModel;
    private boolean onlineMode;
    private ProjectSettings projectSettings;
    private boolean colorScript;
    private TextFlow textFlow;

    /**
     * Parametrized constructor
     * @param connectionModel model
     * @param onlineMode mode
     * @param settings project settings
     * @param textFlow textFlow
     */
    public MainViewModel(ConnectionModel connectionModel, boolean onlineMode, ProjectSettings settings, TextFlow textFlow) {
        setOnCloseRequest();
        logger.info("initialize data for main menu");
        this.textFlow = textFlow;
        this.onlineMode = onlineMode;
        onlineModePrp.set(onlineMode);
        this.connectionModel = connectionModel;
        projectSettings = (settings == null)? new ProjectSettings(null, null, true, ProjectSettings.Size.MEDIUM, this.connectionModel) : settings;
        applySettings();
        selectionItemsList = new ArrayList<>();
        selectCount = 0;
        try {
            initializeLoadManager();
            initializePrintManager();
        } catch (Exception e) {
            logger.error(e);
        }
        initializeTreeRoot(connectionModel.getDBName());
        initializeTableProperties();
        showStatusExecuted(MessageConstants.PROGRAM_READY);
    }

    /**
     * Method shows some actions before closing the program
     */
    private void setOnCloseRequest() {
        MainView.getPrimaryStage().setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Close project");
            alert.setHeaderText("Do you want save project?");
            alert.setContentText("Choose your option");

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeYes) {
                saveProjectAs();
            } else if (result.get() == buttonTypeNo) {
            }
            else if (result.get() == buttonTypeCancel) {
                event.consume();
            }
        });
    }


    /**
     * Method initializes main load manager for loading something nodes
     * Establishes connection if this possible
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void initializeLoadManager() throws SQLException, ClassNotFoundException {
        logger.info("initialize load manager");
        loadManager = LoadManager.getInstance();
        if (!onlineMode) {return;}
        String url = PathsConstants.JDBC_PATH + connectionModel.getUrl() + ":" + connectionModel.getPort() + "?useSSL=false&characterEncoding=utf-8";
        String user = connectionModel.getUser();
        String pass = connectionModel.getPass();
        DBManager dbManager = new DBManager();
        dbManager.setStrategy(new MySQLConnectionStrategy());
        logger.info("set connection for load manager");
        loadManager.setConnection(dbManager.getConnection(url, user, pass));
    }

    /**
     * Method initializes print manager
     */
    private void initializePrintManager() {
        logger.info("initialize print manager");
        printManager = PrintManager.getInstance();
    }

    /**
     * Method sets up attributes table
     */
    private void initializeTableProperties() {
        logger.info("initialize attributes table");
        clmAttributeProperty.setValue(new PropertyValueFactory<Details, String>("attribute"));
        clmValueProperty.setValue(new PropertyValueFactory<Details, String>("value"));
        collectionDetails = new CollectionDetails();
        tableViewProperty.set(collectionDetails.getDetailsList());
    }

    /**
     * Method initializes tree root
     * @param schemaName root name
     */
    private void initializeTreeRoot(String schemaName) {
        logger.info("initialize tree root");
        Node rootNode = new Node(DBObjects.ObjectNames.SCHEMA);
        Map<String, String> map = new HashMap<>();
        map.put(DBObjects.AttributeNames.NAME, schemaName);
        rootNode.setAttributes(map);
        rootItemProperty.set(new TreeItem<>(new ViewNode(rootNode)));
    }

    /**
     * Method sets up behavior for mouse double click on treeItem
     */
    public void initializeMouseProperty() {
        mouseEventProperty.set(event -> {
            if (event.getClickCount() == 2) {
                    new Thread(() -> {
                        logger.info("load item: " + changeTreeProperty.get().getSelectedItem().getValue().toString());
                        visibleIndicatorProperty.set(true);
                        disableTree.set(true);
                        showStatusExecution(MessageConstants.LOAD_ITEM + changeTreeProperty.get().getSelectedItem().getValue().toString());
                        try {
                            loadTreeItem(changeTreeProperty.get().getSelectedItem());
                            Platform.runLater(() -> {
                                //fill table
                                collectionDetails.clear();
                                if (changeTreeProperty.get().getSelectedItem().getValue().getNode().getAttributes().size() != 0) {
                                    collectionDetails.fillDetails(changeTreeProperty.get().getSelectedItem().getValue().getNode().getAttributes());
                                } else {
                                    Map<String, String> countMap = new HashMap<>();
                                    countMap.put("Children Count", new Integer(changeTreeProperty.get().getSelectedItem().getValue().getChildren().size()).toString());
                                    collectionDetails.fillDetails(countMap);
                                }
                                //fill script area
                                try {
                                    textFlow.getChildren().clear();
                                    String txtScript = printManager.printNode(changeTreeProperty.get().getSelectedItem().getValue().getNode());
                                    if (txtScript.equals("") || txtScript.equals(" ") || txtScript == null) {
                                        disableSaveScript.set(true);
                                    } else {
                                        disableSaveScript.set(false);
                                    }
                                    if (colorScript) {
                                        textFlow.getChildren().addAll(TextPainter.paintText(txtScript));
                                    } else {
                                        textFlow.getChildren().addAll(TextPainter.defaultText(txtScript));
                                    }
                                    bindTextFlowChildren();
                                } catch (Exception e) {
                                    logger.warn(e);
                                    disableSaveScript.set(true);
                                    textFlow.getChildren().addAll(TextPainter.paintText(""));
                                }
                            });
                        } catch (Exception e) {
                            logger.info("open confirm password menu");
                            Platform.runLater(() -> {
                                confirmPassword();
                                if (onlineMode == true) {
                                    loadItem();
                                }
                            });
                        }
                        finally {
                            visibleIndicatorProperty.set(false);
                            disableTree.set(false);
                            showStatusExecuted(MessageConstants.ITEM_LOADED + changeTreeProperty.get().getSelectedItem().getValue().toString());

                        }
                    }).start();
            }
        });
    }

    /**
     * Method sets up behaviour for selection any treeItem
     */
    public void initializeTreeChangeProperty() {
        initializeMouseProperty();
        changeTreeProperty.getValue().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Thread thread = new Thread(() -> {
                visibleIndicatorProperty.set(true);
                disableTree.set(true);
                    if (newValue != null) {
                        logger.info("select item: " + newValue.getValue().toString());
                        Platform.runLater(() -> {
                            //fill table
                            collectionDetails.clear();
                            if (newValue.getValue().getNode().getAttributes().size() != 0) {
                                collectionDetails.fillDetails(newValue.getValue().getNode().getAttributes());
                            } else {
                                Map<String, String> countMap = new HashMap<>();
                                countMap.put("Children Count", new Integer(newValue.getValue().getChildren().size()).toString());
                                collectionDetails.fillDetails(countMap);
                            }
                            //field script area
                            try {
                                textFlow.getChildren().clear();
                                String txtScript = printManager.printNode(newValue.getValue().getNode());
                                if (txtScript.equals("") || txtScript.equals(" ") || txtScript == null) {
                                    disableSaveScript.set(true);
                                } else {
                                    disableSaveScript.set(false);
                                }
                                if (colorScript) {
                                    textFlow.getChildren().addAll(TextPainter.paintText(txtScript));
                                } else {
                                    textFlow.getChildren().addAll(TextPainter.defaultText(txtScript));
                                }
                                bindTextFlowChildren();
                            } catch (Exception e) {
                                disableSaveScript.set(true);
                                textFlow.getChildren().addAll(TextPainter.paintText(""));
                            }
                            showStatusExecuted(MessageConstants.ITEM_SELECTED + newValue.getValue().toString());
                        });

                    }
                visibleIndicatorProperty.set(false);
                disableTree.set(false);
            });

            thread.start();
        });
    }

    /**
     * Method loads tree item and adds him to main treeView
     * @param treeItem
     * @throws SQLException
     */
    private void loadTreeItem(TreeItem<ViewNode> treeItem) {
        Node node = treeItem.getValue().getNode();
        if (node.getAttributes().size() != 1) {
            return;
        }
        try {
            loadManager.loadNode(node);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addLoadedNodes(treeItem, treeItem.getValue().getChildren());
    }

    /**
     * Method adds children nodes to main treeView
     * @param treeItem
     * @param childrenNodes
     */
    private void addLoadedNodes(TreeItem<ViewNode> treeItem, List<Node> childrenNodes) {
        for(Node child : childrenNodes) {
            TreeItem<ViewNode> childTreeItem = new TreeItem<>(new ViewNode(child));
            treeItem.getChildren().add(childTreeItem);
            addLoadedNodes(childTreeItem, childTreeItem.getValue().getChildren());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Method saves project state into selected directory
     * @param event
     */
    public void saveObjectScriptAs(ActionEvent event)  {
        logger.info("open file chooser for script saving");
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("SAVE file");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.getExtensionFilters().add(filter1);

        InputStream inputStream = null;
        Properties properties = new Properties();
        File settingsfile = new File(PathsConstants.DEFAULT_SETTINGS_FILE);

        String projectPath = null;
        try {
            logger.info("find default project path");
            inputStream = new FileInputStream(settingsfile.getAbsolutePath());
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
        fileChooser.setInitialDirectory(new File(projectPath));

        File file = fileChooser.showSaveDialog(MainView.getPrimaryStage());
        if (file == null) {
            logger.info("cancel saving script");
            return;
        }

        showStatusExecution(MessageConstants.SAVING_SCRIPT + changeTreeProperty.get().getSelectedItem().getValue().toString());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(printManager.printNode(changeTreeProperty.get().getSelectedItem().getValue().getNode()));
            writer.flush();
            writer.close();
            showStatusExecuted(MessageConstants.SCRIPT_SAVED + changeTreeProperty.get().getSelectedItem().getValue().toString());
            logger.info("script saved");
        } catch (IOException e) {
            logger.warn(e);
        }
    }


    /**
     * Method saves object script into selected directory
     * @param event
     */
    public void saveDBScriptAs(ActionEvent event) {
        logger.info("open file chooser for saving DB script");
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("SAVE file");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.getExtensionFilters().add(filter1);

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

        fileChooser.setInitialDirectory(new File(projectPath));
        File file = fileChooser.showSaveDialog(MainView.getPrimaryStage());
        if (file == null) {
            logger.info("cancel saving script");
            return;
        }
        new Thread(() -> {
        try {
            showStatusExecution(MessageConstants.SAVING_SCRIPT + connectionModel.getDBName() + " database");
            MainLoader mainLoader = new MainLoader(PathsConstants.JDBC_PATH + connectionModel.getUrl() + ":" + connectionModel.getPort(), connectionModel.getUser(), connectionModel.getPass(), new MySQLConnectionStrategy());
            Tree dbTree = mainLoader.getDB(connectionModel.getDBName());
            MainPrinter mainPrinter = new MainPrinter();
            mainPrinter.createDDLScript(dbTree, file);
            showStatusExecuted(MessageConstants.SCRIPT_SAVED + connectionModel.getDBName() + " database");
            logger.info("DB script saved");
        } catch (Exception e) {
            Platform.runLater(() -> confirmPassword());
        }
        }).start();
    }


    /**
     * Method shows program settings window
     * @param event
     */
    public void showSetting(ActionEvent event, Scene ownerScene) {
        try {
            logger.info("open program settings menu");
            ProgramSettingsView settingView = new ProgramSettingsView();
            settingView.show(ownerScene);
        } catch (IOException e) {
            logger.warn(e);
        }
        showStatusExecuted(MessageConstants.SET_SETTINGS);
    }

    /**
     * Method shows project settings window
     * @param event
     */
    public void showProjectSettings(ActionEvent event) {
        projectSettings.setRootItem(rootItemProperty.get());
        projectSettings.setSelectedItem(changeTreeProperty.get().getSelectedItem());
        try {
            logger.info("open project settings menu");
            ProjectSettingsView projectSettingsView = new ProjectSettingsView(projectSettings);
            projectSettingsView.show();
        } catch (IOException e) {
            logger.warn(e);
        }
        applySettings();
        showStatusExecuted(MessageConstants.SET_SETTINGS);
    }

    /**
     * Method applies current project settings
     */
    private void applySettings() {
        if (projectSettings.getRootItem() != null) {
            rootItemProperty.set(projectSettings.getRootItem());
        }
        if (changeTreeProperty.get() != null) {
            if (projectSettings.getSelectedItem() != null) {
                changeTreeProperty.get().select(projectSettings.getSelectedItem());
            }
        }
        if (projectSettings.getSize() == ProjectSettings.Size.SMALL) {
            fontSizeProperty.set("-fx-font: 12px System;");
        } else if (projectSettings.getSize() == ProjectSettings.Size.MEDIUM) {
            fontSizeProperty.set("-fx-font: 14px System;");
        } else if (projectSettings.getSize() == ProjectSettings.Size.BIG) {
            fontSizeProperty.set("-fx-font: 16px System;");
        }

        colorScript = projectSettings.isColorScript();

        textFlow.getChildren().clear();
        try {
            String txtScript = null;
            try {
                txtScript = printManager.printNode(changeTreeProperty.get().getSelectedItem().getValue().getNode());
            } catch (Exception e) {
                txtScript = "";
            }
            if (txtScript.equals("") || txtScript.equals(" ") || txtScript == null) {
                disableSaveScript.set(true);
            } else {
                disableSaveScript.set(false);
            }
            if (colorScript) {
                textFlow.getChildren().addAll(TextPainter.paintText(txtScript));
            } else {
                textFlow.getChildren().addAll(TextPainter.defaultText(txtScript));
            }
        } catch (NullPointerException e) {
            disableSaveScript.set(true);
        }
        bindTextFlowChildren();
        logger.info("apply new settings");
    }

    /**
     * Method binds text flow children with fontSize property
     */
    private void bindTextFlowChildren() {
        for(javafx.scene.Node node : textFlow.getChildren()) {
            node.styleProperty().bindBidirectional(fontSizeProperty);
        }
    }

    /**
     * Method finds a match in the main tree view
     * @param event
     */
    public void search(ActionEvent event, TreeView<ViewNode> tree) {
        logger.info("execute search");
        if (txtSearch.get() == null || txtSearch.get().equals("") || txtSearch.get().isEmpty()  || txtSearch.get().equals(" ")) {
            showStatusExecuted("Warning. Empty search field");
            return;
        }
        if (txtSearch.get().equals(tmpStringSearch)) {
            if (selectionItemsList.isEmpty()) {
                logger.info("no matches found");
                return;
            } else  {
                if (selectCount == selectionItemsList.size() - 1) {
                    selectCount = 0;
                } else {
                    selectCount++;
                }
                tree.getSelectionModel().select(selectionItemsList.get(selectCount));
                tree.scrollTo(tree.getRow(selectionItemsList.get(selectCount)) - 5);
            }
        }   else {
            selectionItemsList.clear();
            selectCount = 0;
            depthSearch(rootItemProperty.getValue());
            tmpStringSearch = txtSearch.get();
            if (!selectionItemsList.isEmpty()) {
                tree.getSelectionModel().select(selectionItemsList.get(selectCount));
                tree.scrollTo(tree.getRow(selectionItemsList.get(selectCount)) - 5);
            }
        }
        if (selectionItemsList.size() == 0) {
            showStatusExecuted("No matches found");
        }
    }

    /**
     * Method resets search data
     * using after load/reload some node
     */
    private void resetSearchData() {
        logger.info("reset search data");
        tmpStringSearch = "";
        selectionItemsList.clear();
        selectCount = 0;
    }

    private void depthSearch(TreeItem<ViewNode> item) {
        if (item.getValue().toString().toLowerCase().contains(txtSearch.get().toLowerCase())) {
            selectionItemsList.add(item);
        }
        for(TreeItem<ViewNode> childItem : item.getChildren()) {
                depthSearch(childItem);
        }
    }

    /**
     * Moves the search pointer upward
     * @param event
     */
    public void searchUP(ActionEvent event, TreeView<ViewNode> tree) {
        logger.info("search move up");
        if (selectionItemsList.isEmpty()) {
            return;
        } else if (selectionItemsList.size() == 1) {
            selectCount = 0;
        } else {
            if (selectCount == 0) {
                selectCount = selectionItemsList.size() - 1;
            }   else {
                selectCount--;
            }
            tree.getSelectionModel().select(selectionItemsList.get(selectCount));
            tree.scrollTo(tree.getRow(selectionItemsList.get(selectCount)) - 5);
        }
        showStatusExecuted(MessageConstants.CURRENT_ITEM + (selectCount + 1) + "/" + selectionItemsList.size());
    }

    /**
     * Moves the search pointer downward
     * @param event
     */
    public void searchDOWN(ActionEvent event, TreeView<ViewNode> tree) {
        logger.info("search move down");
        if (selectionItemsList.isEmpty()) {
            return;
        } else if (selectionItemsList.size() == 1) {
            selectCount = 0;
        }  else {
            if (selectCount == selectionItemsList.size() - 1) {
            selectCount = 0;
            }   else {
            selectCount++;
            }
            tree.getSelectionModel().select(selectionItemsList.get(selectCount));
            tree.scrollTo(tree.getRow(selectionItemsList.get(selectCount)) - 5);
        }
        showStatusExecuted(MessageConstants.CURRENT_ITEM + (selectCount + 1) + "/" + selectionItemsList.size());
    }

    /**
     * Method saves project state into default directory
     */
    public void saveProject() {
        logger.info("saving project");
        InputStream inputStream = null;
        Properties properties = new Properties();
        File file = new File(PathsConstants.DEFAULT_SETTINGS_FILE);

        String projectPath = null;
        try {
            logger.info("find default project path");
            inputStream = new FileInputStream(file.getAbsolutePath());
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
        File projectDirectory = new File(projectPath);

        projectSettings.setRootItem(rootItemProperty.get());
        projectSettings.setSelectedItem(changeTreeProperty.get().getSelectedItem());
        projectSettings.setConnectionModel(connectionModel);
        projectSettings.setColorScript(colorScript);

        try {
            ProjectSaver.saveProject(projectSettings, projectDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        showStatusExecuted(MessageConstants.PROJECT_SAVED + projectDirectory.getAbsolutePath());
        logger.info("project saved");
    }

    public void saveProjectAs() {
        logger.info("open file chooser for project saving");
        DirectoryChooser chooser = new DirectoryChooser();

        InputStream inputStream = null;
        Properties properties = new Properties();
        File file = new File(PathsConstants.DEFAULT_SETTINGS_FILE);

        String projectPath = null;
        try {
            logger.info("find default project path");
            inputStream = new FileInputStream(file.getAbsolutePath());
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
        chooser.setInitialDirectory(new File(projectPath));
        chooser.setTitle("Save project");
        File selectedDirectory = chooser.showDialog(MainView.getPrimaryStage());
        if (selectedDirectory == null) {
            logger.info("project saving canceled");
            return;
        }
        projectSettings.setRootItem(rootItemProperty.get());
        projectSettings.setSelectedItem(changeTreeProperty.get().getSelectedItem());
        projectSettings.setConnectionModel(connectionModel);
        projectSettings.setColorScript(colorScript);

        try {
            ProjectSaver.saveProject(projectSettings, selectedDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        showStatusExecuted(MessageConstants.PROJECT_SAVED + selectedDirectory.getAbsolutePath());
        logger.info("project saved");
    }

    /**
     * Method loads project from saved program state
     * @param event
     */
    public void loadProject(ActionEvent event) {
        logger.info("open directory chooser for opening project");
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setTitle("Open new project");

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

        chooser.setInitialDirectory(new File(projectPath));


        File selectedDirectory = chooser.showDialog(MainView.getPrimaryStage());
        if (selectedDirectory == null) {
            logger.info("opening project canceled");
            return;
        }

        Alert conAlert = new Alert(Alert.AlertType.CONFIRMATION);
        conAlert.setTitle("Save project");
        conAlert.setHeaderText("Do you want save project?");
        conAlert.setContentText("Choose your option.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        conAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = conAlert.showAndWait();
        if (result.get() == buttonTypeYes){
            saveProject();
        } else if (result.get() == buttonTypeNo) {
        } else {
            showStatusExecuted("Open project is canceled");
            return;
        }

        ProjectSettings settings = null;
        try {
            settings = ProjectLoader.loadProject(selectedDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (settings == null || settings.getRootItem() == null) {
            logger.error("invalid project directory");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error project directory");
            alert.setHeaderText("Error project directory");
            alert.setContentText("Project directory don't contains the required files \n " +
                    "or contains corrupted files");
            alert.showAndWait();
            return;
        }

        connectionModel = settings.getConnectionModel();
        onlineMode = false;
        onlineModePrp.set(onlineMode);
        loadManager.setConnection(null);
        projectSettings = settings;
        applySettings();
        showStatusExecuted(MessageConstants.PROJECT_LOADED + connectionModel.getDBName());
        logger.info("new project loaded");
        MainView.getPrimaryStage().setTitle(projectSettings.getConnectionModel().getDBName());
    }

    /**
     * Method shows confirm password window
     * if password will be confirmed, establishes connection with DB
     */
    private void confirmPassword() {
        if (onlineMode) {
            return;
        } else {
            showStatusExecuted(MessageConstants.CONFIRM_PASS);
            ReconnectView reconnectView = new ReconnectView(connectionModel);
            try {
                logger.info("open confirm password menu");
                reconnectView.show(MainView.getPrimaryStage().getScene());
            } catch (Exception e) {
                logger.warn(e);
            }
        }
        if (loadManager.getConnection() != null) {
            logger.info("password successfully verified");
            onlineMode = true;
            onlineModePrp.set(onlineMode);
            showStatusExecuted(MessageConstants.PASSWORD_SUCCESSFULLY_VERIFIED);
        } else {
            logger.info("password don't verified");
        }
    }

    /**
     * Method loads all DB entities
     * @param event
     */
    public void loadAll(ActionEvent event) {
        new Thread(() -> {
            visibleIndicatorProperty.set(true);
            disableTree.set(true);
            String schemaName = rootItemProperty.get().getValue().toString();
            showStatusExecution(MessageConstants.LOAD_DB + schemaName);
            try {
                logger.info("load all DB");
                MainLoader mainLoader = new MainLoader(PathsConstants.JDBC_PATH + connectionModel.getUrl() + ":" + connectionModel.getPort() + "?useSSL=false&characterEncoding=utf-8", connectionModel.getUser(), connectionModel.getPass(), new MySQLConnectionStrategy());
                Node rootNode = mainLoader.getDB(schemaName).getRoot();
                TreeItem<ViewNode> rootItem = new TreeItem<>(new ViewNode(rootNode));
                Platform.runLater(() -> {
                    rootItemProperty.set(rootItem);
                    changeTreeProperty.get().select(rootItem);
                    addLoadedNodes(rootItem, rootNode.getChildren());
                });
                logger.info("DB loaded");
            } catch (Exception e) {
                Platform.runLater(() -> confirmPassword());
            } finally {
                disableTree.set(false);
                visibleIndicatorProperty.set(false);
                resetSearchData();
                showStatusExecuted(MessageConstants.LOADED_DB + schemaName);
            }
        }).start();
    }

    /**
     * Method reloads selected item
     * @param event
     */
    public void reloadItem(ActionEvent event) {
        new Thread(() -> {
            disableTree.set(true);
            visibleIndicatorProperty.set(true);
            TreeItem<ViewNode> tmpItem = changeTreeProperty.get().getSelectedItem();
            showStatusExecution(MessageConstants.RELOAD_ITEM + changeTreeProperty.get().getSelectedItem().getValue().toString());
            try {
                logger.info("reload item " + changeTreePropertyProperty().get().getSelectedItem());
                if (changeTreeProperty.get().getSelectedItem().getParent() == null) {
                    Node node = new Node("SCHEMA");
                    node.getAttributes().put("name", connectionModel.getDBName());
                    TreeItem<ViewNode> rootItem = new TreeItem<>(new ViewNode(node));
                    loadTreeItem(rootItem);
                    Platform.runLater(() -> {
                            rootItemProperty.set(rootItem);
                            changeTreeProperty.get().select(rootItem);
                    });
                } else {
                    reloadTreeItem(changeTreeProperty.get().getSelectedItem());
                }
                showStatusExecuted(MessageConstants.RELOADED_ITEM + changeTreeProperty.get().getSelectedItem().getValue().toString());
                logger.info("item loaded");

                disableTree.set(false);
                visibleIndicatorProperty.set(false);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    confirmPassword();
                    if (onlineMode == true) {
                        reloadItem(event);
                    }
                });
            }  finally {
                Platform.runLater(() -> {
                    disableTree.set(false);
                    visibleIndicatorProperty.set(false);
                    resetSearchData();
                    changeTreeProperty.get().select(null);
                    changeTreeProperty.get().select(tmpItem);
                });

            }
        }).start();
    }

    private void reloadTreeItem(TreeItem<ViewNode> treeItem)  {
        Node node = treeItem.getValue().getNode();
        if (node.getAttributes().isEmpty()) {
            List<Node> childrenList = treeItem.getParent().getValue().getChildren();
            TreeItem<ViewNode> parent = treeItem.getParent();
            try {
                loadManager.reloadCategory(node);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            parent.getChildren().clear();
            addLoadedNodes(parent, childrenList);
        } else {
            System.out.println("reload TREE ITEM");
            if (loadManager.getConnection() != null) {
                treeItem.getChildren().clear();
            }
            System.out.println("reload TREE ITEM2");
            try {
                loadManager.loadNode(treeItem.getValue().getNode());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("reload TREE ITEM3");
            addLoadedNodes(treeItem, treeItem.getValue().getChildren());
        }

    }

    /**
     * Method loads selected item
     */
    public void loadItem() {
        new Thread(() -> {
            TreeItem<ViewNode> tmpItem = changeTreeProperty.get().getSelectedItem();
            visibleIndicatorProperty.set(true);
            disableTree.set(true);
            showStatusExecution(MessageConstants.LOAD_ITEM + changeTreeProperty.get().getSelectedItem().getValue().toString());
            try {
                logger.info("load item " + changeTreeProperty.get().getSelectedItem());
                fullLoadItem(changeTreeProperty.get().getSelectedItem());
                showStatusExecuted(MessageConstants.ITEM_LOADED + changeTreeProperty.get().getSelectedItem().getValue().toString());
                logger.info("item loaded");
                disableTree.set(false);
                visibleIndicatorProperty.set(false);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    confirmPassword();
                    if (onlineMode == true) {
                        loadItem();
                    }
                });

            } finally {
                disableTree.set(false);
                visibleIndicatorProperty.set(false);
                resetSearchData();
                changeTreeProperty.get().select(null);
                changeTreeProperty.get().select(tmpItem);
            }
        }).start();
    }

    /**
     * Method loads all DB
     * @param treeItem
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void fullLoadItem(TreeItem<ViewNode> treeItem) throws SQLException, ClassNotFoundException {
        MainLoader mainLoader = new MainLoader(PathsConstants.JDBC_PATH + connectionModel.getUrl() + ":" + connectionModel.getPort() + "?useSSL=false&characterEncoding=utf-8", connectionModel.getUser(), connectionModel.getPass(), new MySQLConnectionStrategy());
        switch (treeItem.getValue().toString()) {
            case DBObjects.ObjectNames.TABLES : {
                Node tables = mainLoader.getAllTables(connectionModel.getDBName());
                Platform.runLater(() -> {
                    treeItem.getChildren().clear();
                    addLoadedNodes(treeItem, tables.getChildren());
                });
                break;
            }
            case DBObjects.ObjectNames.VIEWS : {
                Node views = mainLoader.getAllViews(connectionModel.getDBName());
                Platform.runLater(() -> {
                    treeItem.getChildren().clear();
                    addLoadedNodes(treeItem, views.getChildren());
                });
                break;
            }
            case DBObjects.ObjectNames.PROCEDURES : {
                Node procedures = mainLoader.getAllProcedures(connectionModel.getDBName());
                Platform.runLater(() -> {
                    treeItem.getChildren().clear();
                    addLoadedNodes(treeItem, procedures.getChildren());
                });
                break;
            }
            case DBObjects.ObjectNames.FUNCTIONS : {
                Node functions = mainLoader.getAllFunctions(connectionModel.getDBName());
                Platform.runLater(() -> {
                    treeItem.getChildren().clear();
                    addLoadedNodes(treeItem, functions.getChildren());
                });
                break;
            }

            default: {
                loadTreeItem(treeItem);
                break;
            }
        }
    }

    /**
     * Method shows execution status
     * @param message
     */
    private void showStatusExecution(String message) {
        Platform.runLater(() -> {
            visibleStatusExecuted.set(false);
            visibleStatusIndicator.set(true);
            txtMessage.set(message);
        });
    }

    /**
     * Method shows status after execution
     * @param message
     */
    private void showStatusExecuted(String message) {
        Platform.runLater(() -> {
            visibleStatusIndicator.set(false);
            visibleStatusExecuted.set(true);
            txtMessage.set(message);
        });
    }


    /**
     * Method shows info about program
     * @param event
     */
    public void showAbout(ActionEvent event) {
        logger.info("open about menu");
        HelpShower.showAbout();
    }

    /**
     * Method closes current project
     * @param event
     */
    public void closeProject(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close project");
        alert.setHeaderText("Do you want save project?");
        alert.setContentText("Choose your option");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes) {
            saveProjectAs();
            StartView startView = new StartView();
            try {
                startView.start(new Stage());
            } catch (Exception e) {
                logger.error(e);
            } finally {
                MainView.getPrimaryStage().close();
                onlineMode = false;
                onlineModePrp.set(onlineMode);
                loadManager.setConnection(null);
            }
        } else if (result.get() == buttonTypeNo) {
            StartView startView = new StartView();
            try {
                startView.start(new Stage());
            } catch (Exception e) {
                logger.error(e);
            }  finally {
                MainView.getPrimaryStage().close();
                onlineMode = false;
                loadManager.setConnection(null);
            }
        }
        else if (result.get() == buttonTypeCancel) {
            return;
        }
    }

    /**
     * Method shows help information
     * @param event
     */
    public void showHelp(ActionEvent event) {
        try {
            HelpView.show();
        } catch (Exception e) {
            logger.error(e);
        }
    }


    /**
     * Method establishes new settings
     * @param settings
     */
    public void setSettings(ProjectSettings settings) {
        if (settings != null) {
            this.projectSettings = settings;
            applySettings();
        }
    }

    /**
     * Method close the application
     * @param event
     */
    public void exit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Do you want save project?");
        alert.setContentText("Choose your option");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes) {
            saveProjectAs();
        } else if (result.get() == buttonTypeNo) {
        }
        else if (result.get() == buttonTypeCancel) {
            return;
        }
        MainView.getPrimaryStage().close();
    }


    /**
     * Method shows reconnect menu for setting connection
     * @param event
     */
    public void connect(ActionEvent event) {
        confirmPassword();
    }

    /**
     * Method copies sql script to clipboard
     * @param event
     */
    public void copyScript(ActionEvent event) {
        logger.info("script copied to clipboard: item: " + changeTreeProperty.get().getSelectedItem().getValue().toString());
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(printManager.printNode(changeTreeProperty.get().getSelectedItem().getValue().getNode()));
        clipboard.setContent(content);
        showStatusExecuted("Script successfully copied to clipboard");
    }


    public TreeItem<ViewNode> getRootItemProperty() {
        return rootItemProperty.get();
    }

    public SimpleObjectProperty<TreeItem<ViewNode>> rootItemPropertyProperty() {
        return rootItemProperty;
    }

    public MultipleSelectionModel<TreeItem<ViewNode>> getChangeTreeProperty() {
        return changeTreeProperty.get();
    }

    public SimpleObjectProperty<MultipleSelectionModel<TreeItem<ViewNode>>> changeTreePropertyProperty() {
        return changeTreeProperty;
    }

    public Border getBorderAnchorPaneProperty() {
        return borderAnchorPaneProperty.get();
    }

    public SimpleObjectProperty<Border> borderAnchorPanePropertyProperty() {
        return borderAnchorPaneProperty;
    }



    public EventHandler<MouseEvent> getMouseEventProperty() {
        return mouseEventProperty.get();
    }

    public SimpleObjectProperty<EventHandler<MouseEvent>> mouseEventPropertyProperty() {
        return mouseEventProperty;
    }

    public boolean isVisibleIndicatorProperty() {
        return visibleIndicatorProperty.get();
    }

    public SimpleBooleanProperty visibleIndicatorPropertyProperty() {
        return visibleIndicatorProperty;
    }

    public ObservableList<Details> getTableViewProperty() {
        return tableViewProperty.get();
    }

    public SimpleObjectProperty<ObservableList<Details>> tableViewPropertyProperty() {
        return tableViewProperty;
    }

    public Property<Callback<TableColumn.CellDataFeatures<Details, String>, ObservableValue<String>>> clmAttributePropertyProperty() {
        return clmAttributeProperty;
    }

    public Property<Callback<TableColumn.CellDataFeatures<Details, String>, ObservableValue<String>>> clmValuePropertyProperty() {
        return clmValueProperty;
    }

    public String getTxtSearch() {
        return txtSearch.get();
    }

    public SimpleStringProperty txtSearchProperty() {
        return txtSearch;
    }

    public boolean isDisableTree() {
        return disableTree.get();
    }

    public SimpleBooleanProperty disableTreeProperty() {
        return disableTree;
    }

    public boolean isDisableSaveScript() {
        return disableSaveScript.get();
    }

    public SimpleBooleanProperty disableSaveScriptProperty() {
        return disableSaveScript;
    }

    public boolean isVisibleStatusIndicator() {
        return visibleStatusIndicator.get();
    }

    public SimpleBooleanProperty visibleStatusIndicatorProperty() {
        return visibleStatusIndicator;
    }

    public boolean isVisibleStatusExecuted() {
        return visibleStatusExecuted.get();
    }

    public SimpleBooleanProperty visibleStatusExecutedProperty() {
        return visibleStatusExecuted;
    }

    public String getTxtMessage() {
        return txtMessage.get();
    }

    public SimpleStringProperty txtMessageProperty() {
        return txtMessage;
    }

    public String getFontSizeProperty() {
        return fontSizeProperty.get();
    }

    public SimpleStringProperty fontSizePropertyProperty() {
        return fontSizeProperty;
    }

    public boolean isOnlineModePrp() {
        return onlineModePrp.get();
    }

    public SimpleBooleanProperty onlineModePrpProperty() {
        return onlineModePrp;
    }


}
