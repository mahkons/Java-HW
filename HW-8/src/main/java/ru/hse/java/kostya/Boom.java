package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Boom implements Sprite {

    private double lifeTime = 0.5 * 1e9;
    private boolean alive = true;

    private double positionX;
    private double positionY;
    private double radius;

    public Boom(double positionX, double positionY, double radius) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.RED);
        graphicsContext.setFill(Color.BLACK);
        Painter.drawCircle(positionX, positionY, radius, graphicsContext);
    }

    @Override
    public void update(double timeNano) {
        lifeTime -= timeNano;
        if (lifeTime < 0) {
            alive = false;
        }
    }

    @Override
    public boolean alive() {
        return alive;
    }
}
