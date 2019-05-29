package ru.hse.kostya;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameInstanceTest {

    private GameInstance gameInstanceSizeTwo;
    private GameInstance gameInstanceSizeThree;

    @BeforeEach
    void setup() {
        gameInstanceSizeTwo = new GameInstance(2);
        gameInstanceSizeThree = new GameInstance(3);
    }

    @Test
    void wrongSize() {
        assertThrows(IllegalArgumentException.class, () -> new GameInstance(-100));
        assertThrows(IllegalArgumentException.class, () -> new GameInstance(1));
        assertThrows(IllegalArgumentException.class, () -> new GameInstance(11));
    }

    @Test
    void correctValues() {
        Map<Integer, Integer> values = new HashMap<>();
        values.put(1, 2);
        values.put(2, 2);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int value =  values.get(gameInstanceSizeTwo.getValue(i, j));
                values.put(gameInstanceSizeTwo.getValue(i, j), value - 1);
            }
        }

        assertEquals(0, (int)values.get(1));
        assertEquals(0, (int)values.get(2));
    }

    private void tryPair(int x1, int y1, int x2, int y2) {
        if (gameInstanceSizeTwo.getState(x1, y1) == GameInstance.CellState.OPENED ||
        gameInstanceSizeTwo.getState(x2, y2) == GameInstance.CellState.OPENED) {
            return;
        }
        gameInstanceSizeTwo.put(x1, y1);
        gameInstanceSizeTwo.put(x2, y2);
        gameInstanceSizeTwo.finishShowing();
    }

    private void openCell(int x, int y) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (i != x || j != y) {
                    if (gameInstanceSizeTwo.getValue(i, j) == gameInstanceSizeTwo.getValue(x, y)) {
                        tryPair(x, y, i, j);

                    }
                }
            }
        }
    }

    @Test
    void winGame() {
        for(int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                openCell(i, j);
            }
        }
        assertArrayEquals("You won!".getBytes(), gameInstanceSizeTwo.getStatus().getBytes());
    }

    @Test
    void successfulPut() {
        assertTrue(gameInstanceSizeThree.put(0, 0));
        assertTrue(gameInstanceSizeThree.put(0, 1));
    }

    @Test
    void failedPut() {
        assertFalse(gameInstanceSizeThree.put(1, 1));

        gameInstanceSizeTwo.put(0, 0);
        assertFalse(gameInstanceSizeTwo.put(0, 0));
        gameInstanceSizeTwo.put(0, 1);
        assertFalse(gameInstanceSizeTwo.put(1, 0));
    }

    @Test
    void wrongBoundedPut() {
        assertThrows(IllegalArgumentException.class, () -> {
           gameInstanceSizeTwo.put(2, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gameInstanceSizeThree.put(-1, 1);
        });
    }
}