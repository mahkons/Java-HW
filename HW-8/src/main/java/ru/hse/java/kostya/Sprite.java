package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;

/**
 * Common object in game.
 * Can be updated and drown
 * if it is not alive it never should become alive again
 *  and should not be drawn or updated
 */
public interface Sprite {

    /**
     * Draw object using given graphicsContext.
     */
    void draw(GraphicsContext graphicsContext);

    /**
     * Update object state.
     * Object should be updated regarding timeNano time in nanoseconds
     *  passed since last update or creating
     */
    void update(double timeNano);

    /**
     * Checks whether object is alive.
     * If it is not it should never be alive again
     */
    boolean alive();
}
