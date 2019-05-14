package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static ru.hse.java.kostya.Painter.WIDTH;

/**
 * Game sprite placed on map with
 *  some horizontal coordinate just above landscape.
 * Maintains bulletType and able to create new bullets whose
 *  state based on current cannon state
 */
public class Cannon implements Sprite {

    /**
     * Power with which knew bullets made by cannon.
     */
    public static final double CANNON_POWER = 80;
    /**
     * Speed of cannons horizontal movement.
     */
    public static final double CANNON_MOVEMENT_SPEED = 10;
    /**
     * Speed of cannon rotation.
     */
    public static final double CANNON_ROTATION_SPEED = 1;

    private double positionX;
    private double angle = Math.PI / 2;
    private Bullet.BulletType bulletType = Bullet.BulletType.MEDIUM;

    private final Landscape landscape;

    /**
     * Creates Cannon with given position in meters just above landscape
     *  and vertical angle.
     */
    public Cannon(double positionX, Landscape landscape) {
        this.positionX = positionX;
        this.landscape = landscape;
    }

    /**
     * If value not in borders returns nearest border.
     * Otherwise returns value itself.
     * Left border supposed to be smaller than right
     */
    private double squeezeIntoBorders(double value, double left, double right) {
        if (left > right) {
            throw new IllegalArgumentException("Left border should be smaller than right");
        }
        if (value < left) {
            value = left;
        }
        if (value > right) {
            value = right;
        }
        return value;
    }

    /**
     * Moves cannon horizontally.
     * It moves with CANNON_MOVEMENT_SPEED speed
     * Position wont increase more than WIDTH and decrease below zero
     * Vertically cannon remains right above landscape
     */
    public void updatePosition(double deltaNanoTime) {
        double deltaPosition = deltaNanoTime * 1e-9 * CANNON_MOVEMENT_SPEED;
        positionX += deltaPosition;
        positionX = squeezeIntoBorders(positionX, 0, WIDTH);
    }

    /**
     * Rotate cannon.
     * It rotates with CANNON_ROTATION_SPEED speed
     * Angle won't go below zero or above Math.PI value
     */
    public void updateAngle(double deltaNanoTime) {
        double deltaAngle = deltaNanoTime * 1e-9 * CANNON_ROTATION_SPEED;
        if (Math.abs(deltaAngle) > Math.PI) {
            deltaAngle = Math.signum(deltaAngle) * Math.PI;
        }
        angle += deltaAngle;
        angle = squeezeIntoBorders(angle, 0, Math.PI);
    }

    /**
     * Changes cannon bulletType.
     */
    public void setBulletType(Bullet.BulletType type) {
        this.bulletType = type;
    }

    /**
     * Creates new bullet according to cannon position.
     * BulletType is same as current cannon's bulletType
     * Power equals to CANNON_POWER
     */
    public Bullet fire() {
        double directionX = Math.cos(angle);
        double directionY = Math.sin(angle);

        return new Bullet(bulletType, positionX - directionX * 3,
                landscape.getY(positionX) - directionY * 3, CANNON_POWER, angle, landscape);
    }

    /**
     * Draws a cannon.
     * Cannon image consists of BROWN horizontal line and
     *  BLACK line positioned according to cannon current angle
     */
    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setLineWidth(Painter.widthToPixels(0.5, graphicsContext));

        graphicsContext.setStroke(Color.BROWN);
        Painter.drawLine(positionX - 2, landscape.getY(positionX),
                positionX + 2, landscape.getY(positionX), graphicsContext);

        graphicsContext.setStroke(Color.BLACK);
        double directionX = Math.cos(angle);
        double directionY = Math.sin(angle);
        Painter.drawLine(
                positionX + directionX * 1, landscape.getY(positionX) + directionY * 1,
                positionX - directionX * 3, landscape.getY(positionX) - directionY * 3,
                graphicsContext);
    }

    /**
     * Cannon's state does not changes just because of time.
     */
    @Override
    public void update(double timeNano) {

    }

    /**
     * Cannon never dies.
     */
    @Override
    public boolean alive() {
        return true;
    }
}
