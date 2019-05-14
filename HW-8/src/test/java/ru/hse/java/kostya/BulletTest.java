package ru.hse.java.kostya;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hse.java.kostya.Cannon.CANNON_POWER;
import static ru.hse.java.kostya.Painter.HEIGHT;

class BulletTest {

    private final static double eps = 1e-6;

    private static class LandscapeMock extends Landscape {

        public LandscapeMock(@NotNull GraphicsContext graphicsContext) {
            super(graphicsContext);
        }
        @Override
        public double getY(double width) {
            return HEIGHT;
        }
    }

    private Landscape landscape = new LandscapeMock(new Canvas().getGraphicsContext2D());
    private Bullet mediumBullet;

    @BeforeEach
    void setup() {
        mediumBullet = new Bullet(Bullet.BulletType.MEDIUM, 0, 0, CANNON_POWER, Math.PI / 3, landscape);
    }

    private double getPositionX(Bullet bullet) throws Exception {
        Field field = Bullet.class.getDeclaredField("positionX");
        field.setAccessible(true);
        return field.getDouble(bullet);
    }

    private double getPositionY(Bullet bullet) throws Exception {
        Field field = Bullet.class.getDeclaredField("positionY");
        field.setAccessible(true);
        return field.getDouble(bullet);
    }

    private void bulletSmallMove(Bullet bullet) {
        for (int i = 0; i < 100; i++) {
            bullet.update(1e6);
        }
    }

    private void bulletBigMove(Bullet bullet) {
        for (int i = 0; i < 1000; i++) {
            bullet.update(1e8);
        }
    }

    @Test
    void movement() throws Exception {
        bulletSmallMove(mediumBullet);
        assertEquals(-2, getPositionX(mediumBullet), eps);
        assertEquals(-3.269863615, getPositionY(mediumBullet), eps);

        bulletSmallMove(mediumBullet);
        assertEquals(-4, getPositionX(mediumBullet), eps);
        assertEquals(-6.14732723, getPositionY(mediumBullet), eps);
    }

    @Test
    void alive() throws Exception {
        assertTrue(mediumBullet.alive());
        mediumBullet.update(1e8);
        assertTrue(mediumBullet.alive());
        bulletBigMove(mediumBullet);
        assertFalse(mediumBullet.alive());
    }

    private boolean affectZeroAim(Bullet.BulletType type, double positionX, double positionY) {
        Aim aim = new Aim(0, 0);
        Bullet bullet = new Bullet(type, positionX, positionY, 0, 0, landscape);
        assertTrue(bullet.alive());
        bullet.explode(aim);
        return !aim.alive();
    }

    @Test
    void explodeSmall() {
        assertTrue(affectZeroAim(Bullet.BulletType.SMALL, 0, 0));
        assertTrue(affectZeroAim(Bullet.BulletType.SMALL, 0.5, 0.5));
        assertFalse(affectZeroAim(Bullet.BulletType.SMALL, 0, 1.5));
        assertFalse(affectZeroAim(Bullet.BulletType.SMALL, 1.5, 0));
        assertFalse(affectZeroAim(Bullet.BulletType.SMALL, 1.5, 1.5));
        assertFalse(affectZeroAim(Bullet.BulletType.SMALL, 2.5, 0));
        assertFalse(affectZeroAim(Bullet.BulletType.SMALL, 2.5, 2));
        assertFalse(affectZeroAim(Bullet.BulletType.SMALL, 4, 0));
    }

    @Test
    void explodeMedium() {
        assertTrue(affectZeroAim(Bullet.BulletType.MEDIUM, 0, 0));
        assertTrue(affectZeroAim(Bullet.BulletType.MEDIUM, 0.5, 0.5));
        assertTrue(affectZeroAim(Bullet.BulletType.MEDIUM, 0, 1.5));
        assertTrue(affectZeroAim(Bullet.BulletType.MEDIUM, 1.5, 0));
        assertFalse(affectZeroAim(Bullet.BulletType.MEDIUM, 1.5, 1.5));
        assertFalse(affectZeroAim(Bullet.BulletType.MEDIUM, 2.5, 0));
        assertFalse(affectZeroAim(Bullet.BulletType.MEDIUM, 2.5, 2));
        assertFalse(affectZeroAim(Bullet.BulletType.MEDIUM, 4, 0));
    }

    @Test
    void explodeBig() {
        assertTrue(affectZeroAim(Bullet.BulletType.HUGE, 0, 0));
        assertTrue(affectZeroAim(Bullet.BulletType.HUGE, 0.5, 0.5));
        assertTrue(affectZeroAim(Bullet.BulletType.HUGE, 0, 1.5));
        assertTrue(affectZeroAim(Bullet.BulletType.HUGE, 1.5, 0));
        assertTrue(affectZeroAim(Bullet.BulletType.HUGE, 1.5, 1.5));
        assertTrue(affectZeroAim(Bullet.BulletType.HUGE, 2.5, 0));
        assertFalse(affectZeroAim(Bullet.BulletType.HUGE, 2.5, 2));
        assertFalse(affectZeroAim(Bullet.BulletType.HUGE, 4, 0));
    }

}