package ru.hse.java.kostya;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AimTest {

    private Aim aim;

    @BeforeEach
    void setup() {
        aim = new Aim(0, 0);
    }

    @Test
    void destroy() {
        assertTrue(aim.alive());
        aim.destroy();
        assertFalse(aim.alive());
        aim.destroy();
        assertFalse(aim.alive());
    }

}