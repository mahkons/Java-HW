package ru.hse.java.kostya;

import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hse.java.kostya.Cannon.*;
import static ru.hse.java.kostya.Painter.HEIGHT;
import static ru.hse.java.kostya.Painter.WIDTH;

class CannonTest {

    private static final double eps = 1e-6;

    private static class LandscapeMock extends Landscape {

        public LandscapeMock(@NotNull GraphicsContext graphicsContext) {
            super(graphicsContext);
        }
        @Override
        public double getY(double width) {
            return (width > WIDTH / 2 ? HEIGHT : 0);
        }
    }

    private Landscape landscape = new LandscapeMock(new Canvas().getGraphicsContext2D());
    private Cannon cannon;

    @BeforeEach
    void setup() {
        cannon = new Cannon(0, landscape);
    }

    private double getPosition(Cannon cannon) throws Exception {
        final Field field = Cannon.class.getDeclaredField("positionX");
        field.setAccessible(true);
        return field.getDouble(cannon);
    }

    private double getAngle(Cannon cannon) throws Exception {
        final Field field = Cannon.class.getDeclaredField("angle");
        field.setAccessible(true);
        return field.getDouble(cannon);
    }

    private double getBulletPrivateDoubleField(String fieldName, Bullet bullet) throws Exception {
        final Field field = Bullet.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getDouble(bullet);
    }

    private Bullet.BulletType getBulletType(Bullet bullet) throws Exception {
        final Field field = Bullet.class.getDeclaredField("type");
        field.setAccessible(true);
        return (Bullet.BulletType)field.get(bullet);
    }

    @Test
    void updatePosition() throws Exception {
        assertEquals(0, getPosition(cannon), eps);
        cannon.updatePosition(1e9);
        assertEquals(CANNON_MOVEMENT_SPEED, getPosition(cannon), eps);
        cannon.updatePosition(-1e9);
        assertEquals(0, getPosition(cannon), eps);
        cannon.updatePosition(-1e9);
        assertEquals(0, getPosition(cannon), eps);
        cannon.updatePosition(1e12);
        assertEquals(WIDTH, getPosition(cannon), eps);
    }

    @Test
    void updateAngle() throws Exception {
        assertEquals(Math.PI / 2, getAngle(cannon), eps);
        cannon.updateAngle(1e9);
        assertEquals(Math.PI / 2 + CANNON_ROTATION_SPEED, getAngle(cannon), eps);
        cannon.updateAngle(-1e9);
        assertEquals(Math.PI / 2, getAngle(cannon), eps);
        cannon.updateAngle( -1e9);
        assertEquals(Math.PI / 2 - CANNON_ROTATION_SPEED, getAngle(cannon), eps);
        cannon.updateAngle(1e12);
        assertEquals(Math.PI, getAngle(cannon), eps);
        cannon.updateAngle(-1e12);
        assertEquals(0, getAngle(cannon), eps);
    }

    private void checkFire(Bullet.BulletType type) throws Exception {
        cannon.setBulletType(type);

        Bullet bullet = cannon.fire();
        assertEquals(type, getBulletType(bullet));
        assertEquals(2.52441295, getBulletPrivateDoubleField("positionX", bullet), eps);
        assertEquals(-1.6209069, getBulletPrivateDoubleField("positionY", bullet), eps);
        final double expectedVelocity = CANNON_POWER / type.getWeight();
        assertEquals(-expectedVelocity * Math.cos(getAngle(cannon)), getBulletPrivateDoubleField("velocityX", bullet), eps);
        assertEquals(-expectedVelocity * Math.sin(getAngle(cannon)), getBulletPrivateDoubleField("velocityY", bullet), eps);
    }

    @Test
    void fire() throws Exception {
        cannon.updateAngle(1e9);
        assertEquals(Math.PI / 2 + CANNON_ROTATION_SPEED, getAngle(cannon), eps);
        checkFire(Bullet.BulletType.SMALL);
        checkFire(Bullet.BulletType.MEDIUM);
        checkFire(Bullet.BulletType.HUGE);
    }
}