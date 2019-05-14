package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Image that lives for LIFE_TIME life.
 * Supposed to be used after bullet explosion
 */
public class Boom implements Sprite {

    /**
     * Time in nanoseconds during which boom will remain alive.
     */
    public static final double LIFE_TIME = 5e8;
    private double timeLeft = LIFE_TIME;
    private boolean alive = true;

    private double positionX;
    private double positionY;
    private double radius;

    /**
     * Creates Circle on given position with given radius.
     * Lengths should be in Meters
     * Would be alive for fixed LIFE_TIME nanoseconds
     */
    public Boom(double positionX, double positionY, double radius) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
    }

    /**
     *  Draws circle with radius of Boom Meters.
     *  It uses BLACK color for body and RED for border.
     */
    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.RED);
        graphicsContext.setFill(Color.BLACK);
        Painter.drawCircle(positionX, positionY, radius, graphicsContext);
    }

    /**
     * Tracks life time of object and
     *  destroys it, if no time left.
     */
    @Override
    public void update(double timeNano) {
        timeLeft -= timeNano;
        if (timeLeft < 0) {
            alive = false;
        }
    }

    @Override
    public boolean alive() {
        return alive;
    }
}
