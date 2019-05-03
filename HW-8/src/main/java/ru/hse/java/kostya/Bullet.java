package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;

public class Bullet implements Sprite {

    public enum BulletType {
        SMALL(1, 1),
        MEDIUM(2, 2),
        HUGE(4, 4);

        private final double amplitude;
        private final double weight;

        BulletType(int amplitude, int weight) {
            this.amplitude = amplitude;
            this.weight = weight;
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

    public Bullet(BulletType type, double positionX, double positionY, double power, double angle) {
        this.type = type;
        this.positionX = positionX;
        this.positionY = positionY;

        double velocity = power / type.getWeight();
        velocityX = velocity * Math.cos(angle);
        velocityY = velocity * Math.sin(angle);
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {

    }

    public void update(double timeNano) {
        positionX += velocityX * timeNano;
        positionY += velocityY * timeNano;

        velocityY += GameLoop.GRAVITY * timeNano;
    }


}
