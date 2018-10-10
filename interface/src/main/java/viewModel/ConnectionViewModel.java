package viewModel;

import constants.PathsConstants;
import db.DBManager;
import db.MySQLConnectionStrategy;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.ConnectionModel;
import org.apache.log4j.Logger;
import service.ConnectionCacheManager;
import view.MainView;

import java.io.IOException;
import java.sql.*;

/**
 * Created by Makoiedov.H on 11/8/2017.
 */
public class ConnectionViewModel {
    private static final Logger logger = Logger.getLogger(ConnectionViewModel.class);
    private final SimpleStringProperty url = new SimpleStringProperty();
    private final SimpleStringProperty user = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();
    private final SimpleStringProperty DBName = new SimpleStringProperty();
    private final SimpleBooleanProperty checkBoxIsSelected = new SimpleBooleanProperty();
    private final SimpleBooleanProperty passDisabled = new SimpleBooleanProperty();
    private final SimpleBooleanProperty progressVisible = new SimpleBooleanProperty();
    private final Property<EventHandler<? super MouseEvent>> pressedEvent = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ObservableList<ConnectionModel>> choiceBoxItems = new SimpleObjectProperty<>();
    private final Property<SingleSelectionModel<ConnectionModel>> choiceBoxModelProperty = new SimpleObjectProperty<>();
    private final SimpleStringProperty port = new SimpleStringProperty();
    private final SimpleBooleanProperty visibleErrorPort = new SimpleBooleanProperty();
    private final SimpleStringProperty portBorderColor = new SimpleStringProperty();
    private final SimpleBooleanProperty btnConnectDisable = new SimpleBooleanProperty();
    private ConnectionCacheManager cacheManager;

    /**
     * Method adds listener for port field
     * if port is incorrect, will be disabled connect button
     */
    private void portInitProperty() {
        port.addListener((observable, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
                portBorderColorProperty().set("");
                visibleErrorPort.set(false);
                btnConnectDisable.set(false);
            } catch (Exception e) {
                logger.warn("incorrect port");
                portBorderColorProperty().set("-fx-text-box-border: red; -fx-focus-color: red ;");
                visibleErrorPort.set(true);
                btnConnectDisable.set(true);
            }
        });
    }

    /**
     * Method adds listener for choice box menu
     * if item is selected, DB connection fields will be filled
     */
    public void addChoiceBoxModelProperty() {
        choiceBoxModelProperty.getValue().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            logger.info("select caching Database: " + newValue.getDBName());
            url.set(newValue.getUrl());
            port.set(newValue.getPort());
            user.set(newValue.getUser());
            DBName.set(newValue.getDBName());
        });
    }

    /**
     * Default constructor, sets the behavior logic for the main window
     */
    public ConnectionViewModel() {
        logger.info("initialize view");
        cacheManager = ConnectionCacheManager.getInstance();
        logger.info("initialize connection caches");
        portInitProperty();
        choiceBoxItems.set(cacheManager.getConnectionPull());
        progressVisible.set(false);
        pressedEvent.setValue(event -> {
            if (checkBoxIsSelected.get()) {
                logger.info("null password canceled");
                passDisabled.set(false);
            } else {
                logger.info("null password selected");
                passDisabled.set(true);
            }
        });
    }

    /**
     * Method trees to connect to DB
     * @param event
     * @throws IOException
     */
    public void connect(ActionEvent event) throws IOException {
            Thread thread = new Thread(() -> {
                logger.info("try to connect to database: " + DBName.get());
                progressVisible.set(true);
                DBManager dbManager = new DBManager();
                dbManager.setStrategy(new MySQLConnectionStrategy());
                Connection connection = null;
                String textUrl = PathsConstants.JDBC_PATH + url.get() + ":" + port.get() + "?useSSL=false&characterEncoding=utf-8";
                String textUser = user.get();
                String textPass = checkBoxIsSelected.get() ? null : password.get();
                String textDBName = DBName.get();
                try {
                    connection = dbManager.getConnection(textUrl, textUser, textPass);
                    if (!isExistingBase(connection, textDBName)) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("non-existent database");
                            alert.setHeaderText("This database does not exist on the server");
                            alert.setContentText("Please, correct the name of the database, or check if your database exists on the server.");
                            alert.showAndWait();
                        });
                        progressVisible.set(false);
                        return;
                    }
                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    cacheManager.addConnectionRecord(new ConnectionModel(url.get(), port.get(), textUser, textPass, textDBName));
                    cacheManager.saveConnectionRecords();
                    ConnectionModel connectionModel = new ConnectionModel(url.get(), port.get() , textUser, textPass, textDBName);
                    Stage welcomeStage = (Stage)stage.getOwner();
                    Platform.runLater(() -> {
                        stage.close();
                        welcomeStage.close();
                        MainView mainView = new MainView(connectionModel, null, true);
                        mainView.show();
                        MainView.getPrimaryStage().setTitle(textDBName);
                        logger.info("connection is successful");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e);
                    progressVisible.set(false);
                    logger.warn("connection is not successful");
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error data");
                        alert.setHeaderText("Error input data");
                        alert.setContentText("Please, enter the correct data");
                        alert.showAndWait();
                    });
                }
            });
            thread.start();
    }


    private boolean isExistingBase(Connection connection, String schemaName) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getCatalogs();
        while (resultSet.next()) {
            String databaseName = resultSet.getString(1);
            if (databaseName.equals(schemaName)) {
                resultSet.close();
                return true;
            }
        }
        resultSet.close();
        return false;
    }

    public boolean isCheckBoxIsSelected() {
        return checkBoxIsSelected.get();
    }

    public SimpleBooleanProperty checkBoxIsSelectedProperty() {
        return checkBoxIsSelected;
    }

    public boolean isPassDisabled() {
        return passDisabled.get();
    }

    public SimpleBooleanProperty passDisabledProperty() {
        return passDisabled;
    }

    public Property<EventHandler<? super MouseEvent>> pressedEventProperty() {
        return pressedEvent;
    }

    public String getUrl() {
        return url.get();
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public String getUser() {
        return user.get();
    }

    public SimpleStringProperty userProperty() {
        return user;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public String getDBName() {
        return DBName.get();
    }

    public SimpleStringProperty DBNameProperty() {
        return DBName;
    }

    public boolean isProgressVisible() {
        return progressVisible.get();
    }

    public SimpleBooleanProperty progressVisibleProperty() {
        return progressVisible;
    }

    public ObservableList<ConnectionModel> getChoiceBoxItems() {
        return choiceBoxItems.get();
    }

    public SimpleObjectProperty<ObservableList<ConnectionModel>> choiceBoxItemsProperty() {
        return choiceBoxItems;
    }

    public Property<SingleSelectionModel<ConnectionModel>> choiceBoxModelPropertyProperty() {
        return choiceBoxModelProperty;
    }

    public String getPort() {
        return port.get();
    }

    public SimpleStringProperty portProperty() {
        return port;
    }

    public boolean isVisibleErrorPort() {
        return visibleErrorPort.get();
    }

    public SimpleBooleanProperty visibleErrorPortProperty() {
        return visibleErrorPort;
    }

    public String getPortBorderColor() {
        return portBorderColor.get();
    }

    public SimpleStringProperty portBorderColorProperty() {
        return portBorderColor;
    }

    public boolean isBtnConnectDisable() {
        return btnConnectDisable.get();
    }

    public SimpleBooleanProperty btnConnectDisableProperty() {
        return btnConnectDisable;
    }
}
