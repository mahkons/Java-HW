package ru.hse.kostya;


import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PairGame extends Application {

    private static final int APP_WIDTH = 1200;
    private static final int APP_HEIGHT = 800;
    private static final Duration SHOWING_BUTTONS_DURATION = Duration.seconds(1);
    private GameInstance gameInstance;

    private final Label statusLine = new Label();
    private Cell[][] cells;

    @Override
    public void start(Stage primaryStage) throws Exception {

//        List<String> parameters = getParameters().getRaw();
//        if (parameters.size() != 1) {
//            throw new IllegalArgumentException("There should be exactly one parameter: size of field");
//        }
//
//        //throws NumberFormatException
//        int fieldSize = Integer.parseInt(parameters.get(0));
//        if (fieldSize < 2 || fieldSize > 10) {
//            throw new IllegalArgumentException("Field should be an integer between 2 and 10");
//        }
        int fieldSize = 3;

        primaryStage.setTitle("Pair Game");
        primaryStage.setScene(new Scene(createContent(fieldSize)));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private Pane createContent(int fieldSize) {

        cells = new Cell[fieldSize][fieldSize];
        gameInstance = new GameInstance(fieldSize);

        var root = new BorderPane();

        var table = new GridPane();
        table.setPrefSize(APP_WIDTH, APP_HEIGHT);
        table.setAlignment(Pos.CENTER);
        table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        table.setPadding(new Insets(15));

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {

                var cell = new Cell(i, j, APP_WIDTH / fieldSize, APP_HEIGHT / fieldSize);
                cells[i][j] = cell;

                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setMaxHeight(Double.MAX_VALUE);
                table.add(cell, i, j);
                GridPane.setHgrow(cell, Priority.ALWAYS);
                GridPane.setVgrow(cell, Priority.ALWAYS);

            }
        }

        root.setCenter(table);

        var bottom = new HBox(10);
        statusLine.setText(gameInstance.getStatus());
        statusLine.setFont(new Font(30));
        bottom.getChildren().add(statusLine);

        root.setBottom(bottom);

        return root;
    }

    public class Cell extends Pane {

        private final Button button = new Button();
        private final Label value;

        public int getCoordinateX() {
            return coordinateX;
        }

        public int getCoordinateY() {
            return coordinateY;
        }

        private final int coordinateX;
        private final int coordinateY;

        private Cell(int coordinateX, int coordinateY, int width, int height) {
            this.coordinateX = coordinateX;
            this.coordinateY = coordinateY;

            setPrefSize(width, height);

            button.setOnAction(event -> {
                    if (gameInstance.put(coordinateX, coordinateY)) {
                        if (gameInstance.likeToShow()) {
                            showValueForSmallTime();
                            cells[gameInstance.getPairX()][gameInstance.getPairY()].showValueForSmallTime();
                        }
                    }
            });

            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.setPrefSize(width, height);
            button.prefHeightProperty().bind(heightProperty());
            button.prefWidthProperty().bind(widthProperty());
            getChildren().add(button);

            value = new Label(Integer.valueOf(gameInstance.getValue(coordinateX, coordinateY)).toString());
            value.setFont(new Font(30));
            value.setAlignment(Pos.CENTER);
        }

        public void showValueForSmallTime() {

            getChildren().add(value);
            PauseTransition visiblePause = new PauseTransition(
                    SHOWING_BUTTONS_DURATION
            );
            visiblePause.setOnFinished(event -> {
                if (gameInstance.getState(coordinateX, coordinateY) == GameInstance.CellState.NOT_OPENED) {
                    getChildren().remove(value);
                }
                gameInstance.finishShowing();
                updateStatus();
            });
            visiblePause.play();
        }

        public void updateStatus() {
            statusLine.setText(gameInstance.getStatus());
        }
    }


}
