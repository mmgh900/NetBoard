package mains;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileChooserExample extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX App");

        FileChooser fileChooser = new FileChooser();

        Button button = new Button("Select File");
        Text text = new Text("file path");
        button.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            text.setText(selectedFile.getPath());

        });

        VBox vBox = new VBox(button, text);
        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
