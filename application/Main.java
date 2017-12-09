package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Image Tagger");
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../viewFiles/main.fxml")), 1190, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
