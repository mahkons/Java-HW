package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Aim implements Sprite {

    private boolean alive = true;
    private double positionX;
    private double positionY;

    public Aim(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public void destroy() {
        alive = false;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setFill(Color.WHITE);
        Painter.drawCircle(positionX, positionY, 2, graphicsContext);
    }

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
