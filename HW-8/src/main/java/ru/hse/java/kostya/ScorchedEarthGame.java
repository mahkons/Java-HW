package ru.hse.java.kostya;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ScorchedEarthGame extends Application {

    private static final int PREFERRED_WIDTH = 1200;
    private static final int PREFERRED_HEIGHT = 800;

    private Scene primaryScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Scorched earth");
        var center = new GridPane();
        center.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);

        Canvas canvas = new Canvas();
        center.getChildren().add(canvas);
        canvas.widthProperty().bind(center.widthProperty());
        canvas.heightProperty().bind(center.heightProperty());

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        var top = new HBox();
        var comboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Small", "Medium", "Huge")
        );
        comboBox.setPrefSize(125, 50);
        top.getChildren().add(comboBox);
        center.getChildren().add(top);


        Scene scene = new Scene(center);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryScene = scene;

        var gameLoop = new GameLoop(this, graphicsContext);

        comboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            gameLoop.setBulletType(Bullet.BulletType.typeByInt(newValue.intValue()));
            center.requestFocus();
        });
        comboBox.setValue("Medium");
        comboBox.setTooltip(new Tooltip("select bullet size"));
        center.requestFocus();


        scene.setOnKeyPressed(event -> gameLoop.pressKey(event.getCode()));
        scene.setOnKeyReleased(event -> gameLoop.releaseKey(event.getCode()));
        gameLoop.play();

    }

    public void showEndScreen() {
        var endLabel = new Label("Congratulations, You won!");
        endLabel.setFont(new Font(50));
        endLabel.setMaxWidth(Double.MAX_VALUE);
        endLabel.setAlignment(Pos.CENTER);

        var root = new GridPane();
        root.getChildren().add(endLabel);
        GridPane.setHgrow(endLabel, Priority.ALWAYS);
        GridPane.setVgrow(endLabel, Priority.ALWAYS);

        primaryScene.setRoot(root);
    }


    public static void main(String[] args) {
        Application.launch(args);
    }

}
