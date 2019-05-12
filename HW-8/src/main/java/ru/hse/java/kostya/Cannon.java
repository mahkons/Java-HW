package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static ru.hse.java.kostya.GameLoop.CANNON_POWER;
import static ru.hse.java.kostya.Painter.WIDTH;

public class Cannon implements Sprite {

    private static final double MAX_ABSOLUTE_ANGLE = Math.PI;

    private double positionX;
    private double angle = Math.PI / 2;
    private Bullet.BulletType bulletType = Bullet.BulletType.MEDIUM;

    private final Landscape landscape;

    public Cannon(double positionX, Landscape landscape) {
        this.positionX = positionX;
        this.landscape = landscape;
    }

    private double inBounds(double value, double left, double right) {
        if (value < left) {
            value = left;
        }
        if (value > right) {
            value = right;
        }
        return value;
    }

    public void updatePosition(double deltaNanoTime) {
        double deltaPosition = deltaNanoTime * 1e-8;
        positionX += deltaPosition;
        positionX = inBounds(positionX, 0, WIDTH);
    }

    public void updateAngle(double deltaNanoTime) {
        double deltaAngle = deltaNanoTime * 1e-9;
        if (Math.abs(deltaAngle) > Math.PI) {
            deltaAngle = Math.signum(deltaAngle) * Math.PI;
        }
        angle += deltaAngle;
        angle = inBounds(angle, 0, MAX_ABSOLUTE_ANGLE);
    }

    public void setBulletType(Bullet.BulletType type) {
        this.bulletType = type;
    }

    public Bullet fire() {
        double directionX = Math.cos(angle);
        double directionY = Math.sin(angle);

        return new Bullet(bulletType, positionX - directionX * 3, landscape.getY(positionX) - directionY * 3, CANNON_POWER, angle, landscape);
    }

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
                positionX - directionX * 3, landscape.getY(positionX) - directionY * 3, graphicsContext);
    }

    @Override
    public void update(double timeNano) {

    }

    @Override
    public boolean alive() {
        return true;
    }
}
