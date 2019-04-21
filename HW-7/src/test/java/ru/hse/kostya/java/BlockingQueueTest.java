package ru.hse.kostya.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {

    private BlockingQueue<Integer> integerBlockingQueue;
    private BlockingQueue<ObjectWithFlag> objectWithFlagBlockingQueue;

    @BeforeEach
    void setUp() {
        integerBlockingQueue = new BlockingQueue<>();
        objectWithFlagBlockingQueue = new BlockingQueue<>();
    }

    @Test
    void testQueueSingleThread() throws InterruptedException {
        integerBlockingQueue.add(1);
        integerBlockingQueue.add(2);
        assertEquals(Integer.valueOf(1), integerBlockingQueue.take());
        integerBlockingQueue.add(3);
        assertEquals(Integer.valueOf(2), integerBlockingQueue.take());
        assertEquals(Integer.valueOf(3), integerBlockingQueue.take());
    }

    @Test
    void testWaitingForElementAddition() throws InterruptedException {
        var actor = new Thread(() -> {
            try {
                assertEquals(Integer.valueOf(1), integerBlockingQueue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        actor.start();
        integerBlockingQueue.add(1);
        actor.join();
    }

    private static class ObjectWithFlag {

        private boolean flag;

        private synchronized void markFlag() {
            if (flag) {
                throw new AssertionError("Flag should be marked once only");
            }
            flag = true;
        }
    }

    @Test
    void testDataRaces() throws InterruptedException {
        var producers = new Thread[1000];
        var consumers = new Thread[5000];

        Arrays.setAll(producers, (i) -> new Thread(() -> {
            for (int j = 0; j < 500; j++) {
                objectWithFlagBlockingQueue.add(new ObjectWithFlag());
            }
        }));

        Arrays.setAll(consumers, (i) -> new Thread(() -> {
            try {
                for (int j = 0; j < 100; j++) {
                    ObjectWithFlag objectWithFlag = objectWithFlagBlockingQueue.take();
                    objectWithFlag.markFlag();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        for (Thread thread : producers) {
            thread.start();
        }
        for (Thread thread : consumers) {
            thread.start();
        }

        for (Thread thread : producers) {
            thread.join();
        }
        for (Thread thread : consumers) {
            thread.join();
        }
    }
}