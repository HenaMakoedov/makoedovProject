package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


/**
 * Created by Makoiedov.H on 1/2/2018.
 */
public class HelpView {
    private static Stage primaryStage;

    /**
     * Method shows connection window
     * @throws Exception
     */
    public static void show() throws Exception {
        if (primaryStage == null) {
            Parent root = FXMLLoader.load(HelpView.class.getClassLoader().getResource("view/Help.fxml"));
            Scene scene = new Scene(root, 800, 800);
            primaryStage = new Stage();
            primaryStage.setTitle("Help information");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } else {
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        }
        primaryStage.toFront();
    }

    public void close(ActionEvent event) {
        HelpView.primaryStage.close();
    }


    @FXML private ImageView startViewImg;
    @FXML private ImageView connectionViewImg;
    @FXML private ImageView mainViewImg;
    @FXML private ImageView saveObjectScriptActionImg;
    @FXML private ImageView saveObjectScriptRightClickImg;
    @FXML private ImageView saveDBScriptImg;

    @FXML
    public void initialize() {
        ClassLoader classLoader = getClass().getClassLoader();
        startViewImg.setImage(new Image(classLoader.getResourceAsStream("screenshots/startView.png")));
        connectionViewImg.setImage(new Image(classLoader.getResourceAsStream("screenshots/connectionView.png")));
        mainViewImg.setImage(new Image(classLoader.getResourceAsStream("screenshots/mainView.png")));
        saveObjectScriptActionImg.setImage(new Image(classLoader.getResourceAsStream("screenshots/saveObjectScriptActionImg.png")));
        saveObjectScriptRightClickImg.setImage(new Image(classLoader.getResourceAsStream("screenshots/saveObjectScriptRightClickImg.png")));
        saveDBScriptImg.setImage(new Image(classLoader.getResourceAsStream("screenshots/saveDBScriptImg.png")));
    }

}
