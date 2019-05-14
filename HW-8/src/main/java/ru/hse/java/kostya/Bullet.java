package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Game sprite that moves when updated
 *  and explodes when find itself below landscape.
 * Maintains velocity taking gravity into account
 */
public class Bullet implements Sprite {

    /**
     * Radius in meters of bullet image.
     */
    public static final double BULLET_RADIUS = 0.5;

    /**
     * Type of bullet.
     * Velocity on creating depends on weight
     * And affected area on explosion depends on amplitude
     */
    public enum BulletType {
        SMALL(1, 1.5),
        MEDIUM(2, 2),
        HUGE(3, 3);

        private final double amplitude;
        private final double weight;

        BulletType(double amplitude, double weight) {
            this.amplitude = amplitude;
            this.weight = weight;
        }

        /**
         * Returns BulletType by it's string representation.
         * Case insensitive
         */
        public static BulletType typeByString(String value) {
            return valueOf(value.toUpperCase());
        }

        public double getWeight() {
            return weight;
        }

        public double getAmplitude() {
            return amplitude;
        }
    }

    private final BulletType type;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private boolean alive = true;

    private final Landscape landscape;

    /**
     * Creates bullet on given position in Meters.
     * Velocity directed as angle and has module equal
     *  to given power divided by bullet weight which depends on it's type
     */
    public Bullet(BulletType type, double positionX, double positionY, double power, double angle,
                  Landscape landscape) {
        this.type = type;
        this.positionX = positionX;
        this.positionY = positionY;
        this.landscape = landscape;

        double velocity = power / type.getWeight();
        velocityX = -velocity * Math.cos(angle);
        velocityY = -velocity * Math.sin(angle);
    }

    private double distanceToAim(double x, double y) {
        return Math.sqrt((positionX - x) * (positionX - x) + (positionY - y) * (positionY - y));
    }

    /**
     * Destroys aim if it is in affected area of bullet,
     *  which depends on it's type.
     * Creates Boom object on the same position and radius dependent on bullet amplitude
     */
    public Boom explode(Aim aim) {
        if (distanceToAim(aim.getPositionX(), aim.getPositionY()) < type.amplitude) {
            aim.destroy();
        }
        return new Boom(positionX, positionY, type.getAmplitude());
    }

    /**
     * Draws circle on bullet position.
     * Color is BLACK and radius equals to BULLET_RADIUS
     */
    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.BLACK);
        Painter.drawCircle(positionX, positionY, BULLET_RADIUS, graphicsContext);
    }

    /**
     * Maintains bullet position and velocity.
     * Velocity depends on gravity
     */
    @Override
    public void update(double timeNano) {
        positionX += velocityX * timeNano * 1e-9;
        positionY += velocityY * timeNano * 1e-9;

        velocityY += GameLoop.GRAVITY * timeNano * 1e-9;

        if (positionY > landscape.getY(positionX)) {
            alive = false;
        }
    }

    @Override
    public boolean alive() {
        return alive;
    }


}
