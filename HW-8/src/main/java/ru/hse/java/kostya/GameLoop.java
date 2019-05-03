package ru.hse.java.kostya;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GameLoop {

    public final static double GRAVITY = 9.81;
    public final static double WIDTH = 32.0; //In meters
    public final static double HEIGHT = 32.0; //In meters

    private final Timeline timeline = new Timeline();
    private final Duration targetFrameRate = Duration.millis(16); // 60 FPS
    private final GraphicsContext graphicsContext;

    private KeyCode keyCode;

    private List<Sprite> spriteList = new ArrayList<>();
    private final Cannon cannon = new Cannon(WIDTH / 2.0);
    {
        spriteList.add(cannon);
    }

    private double lastNanoTime = System.nanoTime();

    public GameLoop(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        timeline.setCycleCount(Timeline.INDEFINITE);


        KeyFrame keyFrame = new KeyFrame(
                targetFrameRate,
                actionEvent -> {
                    double currentNanoTime = System.nanoTime();
                    double deltaNanoTime = lastNanoTime - currentNanoTime;
                    lastNanoTime = currentNanoTime;

                    graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(),
                            graphicsContext.getCanvas().getHeight());
                    for (Sprite sprite : spriteList) {
                        sprite.update(deltaNanoTime);
                        sprite.draw(graphicsContext);
                    }


                });

        timeline.getKeyFrames().add(keyFrame);
    }

    public void addSprite(Sprite sprite) {
        spriteList.add(sprite);
    }

    public void play() {
        timeline.play();
    }

    public void pressKey(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            spriteList.add(cannon.fire());
            return;
        }
        this.keyCode = keyCode;
    }

    public void releaseKey(KeyCode keyCode) {
        if (this.keyCode == keyCode) {
            this.keyCode = null;
        }
    }
}
