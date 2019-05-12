package ru.hse.java.kostya;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ru.hse.java.kostya.Painter.WIDTH;

public class GameLoop {

    public final static double GRAVITY = 9.81 * 4;
    public final static double CANNON_POWER = 80;

    private final ScorchedEarthGame parent;

    private final Timeline timeline = new Timeline();
    private final Duration targetFrameRate = Duration.millis(16); // 60 FPS
    private final GraphicsContext graphicsContext;

    private KeyCode keyCode;

    private List<Sprite> spriteList = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();

    private final Cannon cannon;
    private final Landscape landscape;
    private final Aim aim;

    private double lastNanoTime = System.nanoTime();
    private double afterEndCycleCount = 15;

    public GameLoop(ScorchedEarthGame parent, GraphicsContext graphicsContext) {
        this.parent = parent;
        this.graphicsContext = graphicsContext;

        landscape = new Landscape(graphicsContext);
        cannon = new Cannon(WIDTH / 1.5, landscape);
        aim = new Aim(WIDTH / 5.0, landscape.getY(WIDTH / 5.0));

        spriteList.add(landscape);
        spriteList.add(cannon);
        spriteList.add(aim);

        timeline.setCycleCount(Timeline.INDEFINITE);


        KeyFrame keyFrame = new KeyFrame(
                targetFrameRate,
                actionEvent -> {
                    double currentNanoTime = System.nanoTime();
                    double deltaNanoTime = currentNanoTime - lastNanoTime;
                    lastNanoTime = currentNanoTime;

                    if (keyCode != null) {
                        switch (keyCode) {
                            case LEFT:
                                cannon.updatePosition(-deltaNanoTime);
                                break;
                            case RIGHT:
                                cannon.updatePosition(+deltaNanoTime);
                                break;
                            case UP:
                                cannon.updateAngle(deltaNanoTime);
                                break;
                            case DOWN:
                                cannon.updateAngle(-deltaNanoTime);
                                break;
                            default:
                                throw new IllegalStateException("Unknown command");
                        }
                    }


                    graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(),
                            graphicsContext.getCanvas().getHeight());

                    Iterator<Sprite> iterator = spriteList.iterator();
                    while (iterator.hasNext()) {
                        Sprite sprite = iterator.next();
                        if (!sprite.alive()) {
                            iterator.remove();
                            continue;
                        }
                        sprite.update(deltaNanoTime);
                        sprite.draw(graphicsContext);
                    }

                    Iterator<Bullet> bulletIterator = bullets.iterator();
                    while (bulletIterator.hasNext()) {
                        Bullet bullet = bulletIterator.next();
                        if (!bullet.alive()) {
                            addSprite(bullet.explode(aim));
                            bulletIterator.remove();
                        }
                    }

                    if (!aim.alive()) {
                        if (afterEndCycleCount-- == 0) {
                            timeline.stop();
                            parent.showEndScreen();
                        }
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

    public void setBulletType(Bullet.BulletType type) {
        cannon.setBulletType(type);
    }


    public void pressKey(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            final Bullet bullet = cannon.fire();
            addSprite(bullet);
            bullets.add(bullet);
            return;
        }
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT ||
        keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
            this.keyCode = keyCode;
        }
    }

    public void releaseKey(KeyCode keyCode) {
        if (this.keyCode == keyCode) {
            this.keyCode = null;
        }
    }

}
