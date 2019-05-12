package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

import static ru.hse.java.kostya.Bullet.BulletType.SMALL;

public class Bullet implements Sprite {

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

        public static BulletType typeByInt(int value) {
            switch (value) {
                case 0:
                    return SMALL;
                case 1:
                    return MEDIUM;
                case 2:
                    return HUGE;
                default:
                    throw new IllegalArgumentException("only values 1, 2, 3 allowed adn actual value is:" + value);
            }
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

    public Boom explode(Aim aim) {
        if (distanceToAim(aim.getPositionX(), aim.getPositionY()) < type.amplitude) {
            aim.destroy();
        }
        return new Boom(positionX, positionY, type.getAmplitude());
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.BLACK);
        Painter.drawCircle(positionX, positionY, 0.5, graphicsContext);
    }

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
