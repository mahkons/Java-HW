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

/**
 * Game inspired by ScorchedEarthVideoGame.
 * To achieve victory player needs to destroy an aim with bullets,
 *  which he can fire from cannon with ENTER key
 * Player can change bullet types
 * PLayer can move cannon using LEFT and RIGHT keys
 *  and change cannon's angle using UP and DOWN keys
 */
public class ScorchedEarthGame extends Application {

    /**
     * Preferred width of screen in pixels, with which it is created
     */
    private static final int PREFERRED_WIDTH = 1200;
    /**
     * Preferred height of screen in pixels, with which it is created
     */
    private static final int PREFERRED_HEIGHT = 800;

    private Scene primaryScene;

    /**
     * Creates scene, game objects,
     *  shows them to player and starts game loop.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Scorched earth");
        var center = new GridPane();
        center.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);

        Canvas canvas = new Canvas();
        center.getChildren().add(canvas);
        canvas.widthProperty().bind(center.widthProperty());
        canvas.heightProperty().bind(center.heightProperty());

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        var top = new HBox();
        var bulletTypesList = FXCollections.observableArrayList(
                "Small", "Medium", "Huge");
        var comboBox = new ComboBox<>(bulletTypesList);
        comboBox.setPrefSize(125, 50);
        top.getChildren().add(comboBox);
        center.getChildren().add(top);


        Scene scene = new Scene(center);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryScene = scene;

        var gameLoop = new GameLoop(this, graphicsContext);

        comboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            gameLoop.setBulletType(Bullet.BulletType.typeByString(bulletTypesList.get(newValue.intValue())));
            center.requestFocus();
        });
        comboBox.setValue("Medium");
        comboBox.setTooltip(new Tooltip("select bullet size"));
        center.requestFocus();


        scene.setOnKeyPressed(event -> gameLoop.pressKey(event.getCode()));
        scene.setOnKeyReleased(event -> gameLoop.releaseKey(event.getCode()));
        gameLoop.play();

    }

    /**
     * Shows game end screen with congratulation text only.
     */
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
