package viewModel;

import constants.PathsConstants;
import db.DBManager;
import db.MySQLConnectionStrategy;
import db.loader.LoadManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.ConnectionModel;
import org.apache.log4j.Logger;


import java.sql.Connection;

/**
 * Created by Makoiedov.H on 12/6/2017.
 */
public class ReconnectViewModel {
    private static final Logger logger = Logger.getLogger(ReconnectViewModel.class);
    private SimpleStringProperty url = new SimpleStringProperty();
    private SimpleStringProperty port = new SimpleStringProperty();
    private SimpleStringProperty user = new SimpleStringProperty();
    private SimpleStringProperty DBName = new SimpleStringProperty();
    private SimpleStringProperty password = new SimpleStringProperty();
    private SimpleBooleanProperty nullPassword = new SimpleBooleanProperty();
    private SimpleBooleanProperty disableFields = new SimpleBooleanProperty();
    private SimpleBooleanProperty disablePass = new SimpleBooleanProperty();
    private SimpleBooleanProperty visibleProgressBar = new SimpleBooleanProperty();
    ConnectionModel connectionModel;

    public ReconnectViewModel(ConnectionModel connectionModel) {
        logger.info("initialize reconnect fields");
        this.connectionModel = connectionModel;
        url.set(connectionModel.getUrl());
        port.set(connectionModel.getPort());
        user.set(connectionModel.getUser());
        DBName.set(connectionModel.getDBName());
        disableFields.set(true);
        nullPassword.set(false);
        disablePass.set(false);
        disablePassChange();
        visibleProgressBar.set(false);
    }

    /**
     * if pass equals null, password field will be disabled
     */
    private void disablePassChange() {
        nullPassword.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                logger.info("select nullable pass");
                disablePass.set(true);
            } else {
                logger.info("cancel nullable pass");
                disablePass.set(false);
            }
        });
    }


    /**
     * Confirm password method, if password is incorrect method shows info menu
     * if password is correct, will be establishes new connection
     * @param event
     */
    public void confirm(ActionEvent event) {
        new Thread(() -> {
            try {
                visibleProgressBar.set(true);
                String pass = nullPassword.get() ? null : password.get();
                DBManager dbManager = new DBManager();
                dbManager.setStrategy(new MySQLConnectionStrategy());
                Connection connection = dbManager.getConnection(PathsConstants.JDBC_PATH + url.get() + ":" + port.get() + "?useSSL=false&characterEncoding=utf-8", user.get(), pass);
                LoadManager loadManager = LoadManager.getInstance();
                loadManager.setConnection(connection);
                connectionModel.setPass(pass);
                Platform.runLater(() -> {
                    Node source = (Node)event.getSource();
                    Stage stage = (Stage)source.getScene().getWindow();
                    stage.close();
                    logger.info("password successful verified");
                });
                } catch (Exception e) {
                Platform.runLater(() -> {
                    logger.info("entered incorrect password");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Wrong password");
                    alert.setHeaderText("Wrong password");
                    alert.setContentText("Please, enter the correct password");
                    alert.showAndWait();
                });

            } finally {
                visibleProgressBar.set(false);
            }
        }).start();
    }

    /**
     * Method closes reconnect menu
     * @param event
     */
    public void cancel(ActionEvent event) {
        logger.info("close confirm password menu");
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }


    public String getUrl() {
        return url.get();
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public String getPort() {
        return port.get();
    }

    public SimpleStringProperty portProperty() {
        return port;
    }

    public String getUser() {
        return user.get();
    }

    public SimpleStringProperty userProperty() {
        return user;
    }

    public String getDBName() {
        return DBName.get();
    }

    public SimpleStringProperty DBNameProperty() {
        return DBName;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public boolean isNullPassword() {
        return nullPassword.get();
    }

    public SimpleBooleanProperty nullPasswordProperty() {
        return nullPassword;
    }

    public boolean isDisableFields() {
        return disableFields.get();
    }

    public SimpleBooleanProperty disableFieldsProperty() {
        return disableFields;
    }

    public boolean isDisablePass() {
        return disablePass.get();
    }

    public SimpleBooleanProperty disablePassProperty() {
        return disablePass;
    }

    public boolean isVisibleProgressBar() {
        return visibleProgressBar.get();
    }

    public SimpleBooleanProperty visibleProgressBarProperty() {
        return visibleProgressBar;
    }
}
