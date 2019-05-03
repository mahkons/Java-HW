package ru.hse.java.kostya;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ScorchedEarthGame extends Application {

    private static final int PREFERRED_WIDTH = 800;
    private static final int PREFERRED_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Scorched earth");
        primaryStage.setScene(createContent());
        primaryStage.show();
    }

    private Scene createContent() {

        var root = new BorderPane();
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        var center = new GridPane();
        center.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);

        Canvas canvas = new Canvas();
        center.getChildren().add(canvas);
        GridPane.setHgrow(canvas, Priority.ALWAYS);
        GridPane.setVgrow(canvas, Priority.ALWAYS);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        var gameLoop = new GameLoop(graphicsContext);

        root.setCenter(center);


        var bottom = new HBox();
        bottom.getChildren().add(new Label("some"));
        root.setBottom(bottom);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> gameLoop.pressKey(event.getCode()));
        scene.setOnKeyReleased(event -> gameLoop.releaseKey(event.getCode()));

        gameLoop.play();

        return scene;
    }


    public static void main(String[] args) {
        Application.launch(args);
    }

}
