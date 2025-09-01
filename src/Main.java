import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Startup class for the application.
 * @author CuteCat233
 * @date 2025.09.01
 * @Version 1.0
 */
public class Main extends Application{
    /**
     * Main entry point for the application.
     * @param args Command line arguments, not used
     */
    public static void main(String[] args){
        launch(args);
    }

    /**
     * Start the JavaFX application.
     * @param stage The primary stage for this application.
     * @throws Exception If there is an error loading the FXML file.
     */
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Main.fxml")));
        stage.setTitle("简易翻译器");
        stage.setScene(new Scene(root, null));
        stage.show();
    }
}
