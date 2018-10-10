package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ConnectionModel;
import viewModel.ConnectionViewModel;

import java.io.IOException;


/**
 * Created by Makoiedov.H on 10/27/2017.
 */
public class ConnectionView {
    private Stage primaryStage;

    /**
     * Method shows connection window
     * @param event
     * @throws Exception
     */
    public void show(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/Connection.fxml"));
        Scene scene = new Scene(root, 460,425);
        primaryStage = new Stage();
        primaryStage.setTitle("Create new project");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        Node source = (Node)event.getSource();
        primaryStage.initOwner(source.getScene().getWindow());
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }


    @FXML private Button btnConnect;
    @FXML private TextField url;
    @FXML private TextField user;
    @FXML private TextField password;
    @FXML private TextField port;
    @FXML private TextField DBName;
    @FXML private CheckBox checkBox;
    @FXML private ProgressBar progressBar;
    @FXML private ChoiceBox<ConnectionModel> choiceBox;
    @FXML private Label lblPortError;

    private ConnectionViewModel viewModel;

    /**
     * Gui objects binding
     */
    @FXML
    public void initialize() {
        viewModel = new ConnectionViewModel();
        url.textProperty().bindBidirectional(viewModel.urlProperty());
        user.textProperty().bindBidirectional(viewModel.userProperty());
        lblPortError.visibleProperty().bindBidirectional(viewModel.visibleErrorPortProperty());
        port.textProperty().bindBidirectional(viewModel.portProperty());
        port.styleProperty().bindBidirectional(viewModel.portBorderColorProperty());
        btnConnect.disableProperty().bindBidirectional(viewModel.btnConnectDisableProperty());

        password.textProperty().bindBidirectional(viewModel.passwordProperty());
        password.disableProperty().bindBidirectional(viewModel.passDisabledProperty());

        checkBox.selectedProperty().bindBidirectional(viewModel.checkBoxIsSelectedProperty());
        checkBox.onMousePressedProperty().bindBidirectional(viewModel.pressedEventProperty());

        choiceBox.itemsProperty().bindBidirectional(viewModel.choiceBoxItemsProperty());
        viewModel.choiceBoxModelPropertyProperty().bindBidirectional(choiceBox.selectionModelProperty());
        viewModel.addChoiceBoxModelProperty();

        progressBar.visibleProperty().bindBidirectional(viewModel.progressVisibleProperty());
        DBName.textProperty().bindBidirectional(viewModel.DBNameProperty());
    }

    /**
     * Method trees to connect to DB
     * @param event
     * @throws IOException
     */
    public void connect(ActionEvent event) throws IOException {
        viewModel.connect(event);
    }

    /**
     * Method closes connection window
     * @param event
     */
    public void cancel(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }
}
