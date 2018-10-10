import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import service.ConnectionCacheManager;

import java.util.concurrent.TimeoutException;


/**
 * Created by Makoiedov.H on 12/18/2017.
 */
public class ConnectionViewTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("view/Connection.fxml"));
        stage.setScene(new Scene(parent));
        stage.show();
        stage.toFront();
    }

    public <T extends Node> T find(final String query) {
        /** TestFX provides many operations to retrieve elements from the loaded GUI. */
        return lookup(query).query();
    }

    @Test
    public void randomTest() {
        TextField url = find("#url");
        TextField port = find("#port");
        TextField user = find("#user");
        TextField DBName = find("#DBName");
        TextField pass = find("#password");
        Button button = find("#btnConnect");

        sleep(300);
        url.setText("dev-mysql.cjj06khxetlc.us-west-2.rds.amazonaws.com");
        sleep(300);
        port.setText("3306");
        sleep(300);
        user.setText("min_privs");
        sleep(300);
        DBName.setText("test_mysql");
        sleep(300);
        pass.setText("min_privs");
        sleep(300);

        Platform.runLater(() -> clickOn(button));
        Assert.assertTrue(ConnectionCacheManager.getInstance().getConnectionPull().size() > 0);
    }
    @After
    public void afterEachTest() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
