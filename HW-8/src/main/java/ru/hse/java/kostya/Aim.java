package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Aim that positioned somewhere on the map
 *  and can be destroyed.
 */
public class Aim implements Sprite {

    /**
     * Radius of Aim image.
     */
    public static final double AIM_RADIUS = 2;

    private boolean alive = true;
    private double positionX;
    private double positionY;

    /**
     * Places alive aim somewhere on the map.
     */
    public Aim(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * Makes aim not alive.
     */
    public void destroy() {
        alive = false;
    }

    /**
     *  Draws circle with radius AIM_RADIUS Meters.
     *  It uses WHITE color for body and BLUE for border.
     */
    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setFill(Color.WHITE);
        Painter.drawCircle(positionX, positionY, AIM_RADIUS, graphicsContext);
    }

    /**
     * Aim is not changing during time.
     */
    @Override
    public void update(double timeNano) {

    }

    @Override
    public boolean alive() {
        return alive;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }
}
