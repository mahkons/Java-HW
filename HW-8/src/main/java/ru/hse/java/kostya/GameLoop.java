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

/**
 * Game loop which keeps all game objects, updates their state,
 *  draws them, parses keyboard input and end the game after aim destroyed.
 */
public class GameLoop {

    /**
     * Gravity that affects bullets.
     */
    public static final double GRAVITY = 9.81 * 4;
    /**
     * Initial horizontal position of a cannon.
     */
    public static final double CANNON_INITIAL_POSITION = WIDTH / 1.5;
    /**
     * Initial horizontal position of an aim.
     */
    public static final double AIM_POSITION = WIDTH / 5.0;

    private final ScorchedEarthGame parent;

    private final Timeline timeline = new Timeline();
    /**
     * Game frames updated every targetFrameRate milliseconds.
     */
    private final Duration targetFrameRate = Duration.millis(16); // 60 FPS
    private final GraphicsContext graphicsContext;

    /**
     * Active movement keyCode.
     */
    private KeyCode keyCode;

    /**
     * List of game sprites.
     * Supposed to be used for drawing and updating them
     */
    private List<Sprite> spriteList = new ArrayList<>();
    /**
     * Separate list for bullets to control their explosion.
     */
    private List<Bullet> bullets = new ArrayList<>();

    private final Cannon cannon;
    private final Landscape landscape;
    private final Aim aim;

    private double lastNanoTime = System.nanoTime();
    /**
     * Game shows AFTER_END_CYCLE_COUNT frames after aim destroyed
     *  and than stops.
     */
    public static final double AFTER_END_CYCLE_COUNT = 15;
    private double afterEndCycleCount = AFTER_END_CYCLE_COUNT;

    /**
     * Creates all game initial objects.
     * After construction game can be started at any moment with
     *  following play method
     */
    public GameLoop(ScorchedEarthGame parent, GraphicsContext graphicsContext) {
        this.parent = parent;
        this.graphicsContext = graphicsContext;

        landscape = new Landscape(graphicsContext);
        cannon = new Cannon(CANNON_INITIAL_POSITION, landscape);
        aim = new Aim(AIM_POSITION, landscape.getY(AIM_POSITION));

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
                                throw new IllegalArgumentException("Unknown command");
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

    /**
     * Adds new sprite.
     */
    public void addSprite(Sprite sprite) {
        spriteList.add(sprite);
    }

    /**
     * Starts frames changing.
     */
    public void play() {
        timeline.play();
    }

    /**
     * Changes bulletType of cannon.
     */
    public void setBulletType(Bullet.BulletType type) {
        cannon.setBulletType(type);
    }


    /**
     * Parses pressed key.
     * If it is ENTER key ask cannon to fire a bullet
     * if it is a movement key, makes it active key
     */
    public void pressKey(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            final Bullet bullet = cannon.fire();
            addSprite(bullet);
            bullets.add(bullet);
            return;
        }
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT
                || keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
            this.keyCode = keyCode;
        }
    }

    /**
     * If on current moment given key is active,
     *  no key becomes active.
     */
    public void releaseKey(KeyCode keyCode) {
        if (this.keyCode == keyCode) {
            this.keyCode = null;
        }
    }

}
