package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;

public class Cannon implements Sprite {

    private static final double MAX_ABSOLUTE_ANGLE = Math.PI / 2;

    private double positionX;
    private double angle;
    private Bullet.BulletType bulletType = Bullet.BulletType.MEDIUM;

    public Cannon(double positionX) {
        this.positionX = positionX;
    }

    public void updatePosition(double deltaPosition) {
        positionX += deltaPosition;
        if (positionX < 0) {
            positionX = 0;
        }
        if (positionX > GameLoop.WIDTH) {
            positionX = GameLoop.WIDTH;
        }
    }

    public void updateAngle(double deltaAngle) {
        if (Math.abs(deltaAngle) > Math.PI) {
            deltaAngle = Math.signum(deltaAngle) * Math.PI;
        }
        angle += deltaAngle;
        if (angle < -MAX_ABSOLUTE_ANGLE) {
            angle = -MAX_ABSOLUTE_ANGLE;
        }
        if (angle > MAX_ABSOLUTE_ANGLE) {
            angle = MAX_ABSOLUTE_ANGLE;
        }
    }

    public Bullet fire() {
        //return new Bullet(bulletType, positionX, );
        return null;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {

    }

    @Override
    public void update(double timeNano) {

    }
}
