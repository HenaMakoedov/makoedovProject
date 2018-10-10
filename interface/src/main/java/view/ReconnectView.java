package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ConnectionModel;
import viewModel.ReconnectViewModel;



/**
 * Created by Makoiedov.H on 12/6/2017.
 */
public class ReconnectView {
    private Stage primaryStage;
    private static ConnectionModel connectionModel;

    public ReconnectView() {

    }

    public ReconnectView(ConnectionModel connectionModel) {
        ReconnectView.connectionModel = connectionModel;
    }

    /**
     * Method shows reconnect window
     */
    public void show(Scene ownerScene) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/Reconnect.fxml"));
        Scene scene = new Scene(root, 440,300);
        primaryStage = new Stage();
        primaryStage.setTitle("Confirm the password");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initOwner(ownerScene.getWindow());
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.showAndWait();
    }


    @FXML private TextField url;
    @FXML private TextField port;
    @FXML private TextField user;
    @FXML private TextField DBName;
    @FXML private TextField password;
    @FXML private CheckBox checkBox;
    @FXML private ProgressBar progressBar;
    private ReconnectViewModel viewModel;

    /**
     * Gui objects binding
     */
    @FXML
    public void initialize() {
        viewModel = new ReconnectViewModel(connectionModel);
        url.textProperty().bindBidirectional(viewModel.urlProperty());
        port.textProperty().bindBidirectional(viewModel.portProperty());
        user.textProperty().bindBidirectional(viewModel.userProperty());
        DBName.textProperty().bindBidirectional(viewModel.DBNameProperty());
        password.textProperty().bindBidirectional(viewModel.passwordProperty());
        password.disableProperty().bindBidirectional(viewModel.disablePassProperty());
        checkBox.selectedProperty().bindBidirectional(viewModel.nullPasswordProperty());

        url.disableProperty().bindBidirectional(viewModel.disableFieldsProperty());
        port.disableProperty().bindBidirectional(viewModel.disableFieldsProperty());
        user.disableProperty().bindBidirectional(viewModel.disableFieldsProperty());
        DBName.disableProperty().bindBidirectional(viewModel.disableFieldsProperty());
        progressBar.visibleProperty().bindBidirectional(viewModel.visibleProgressBarProperty());
    }

    /**
     * Method which checks the correct password
     * If password is correct, creates new connection
     * @param event
     */
    public void confirm(ActionEvent event) {
        viewModel.confirm(event);
    }

    /**
     * Method closes reconnect window
     * @param event
     */
    public void cancel(ActionEvent event) {
        viewModel.cancel(event);
    }

}
