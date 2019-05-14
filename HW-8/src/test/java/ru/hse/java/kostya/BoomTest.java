package ru.hse.java.kostya;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoomTest {

    private Boom boom;

    @BeforeEach
    void setup() {
        boom = new Boom(0, 0, 1);
    }

    @Test
    void destroyAfterTime() {
        assertTrue(boom.alive());
        boom.update(Boom.LIFE_TIME / 2);
        assertTrue(boom.alive());
        boom.update(Boom.LIFE_TIME);
        assertFalse(boom.alive());
    }
}