package ru.hse.kostya.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolTest {

    private static class TaskObject {
        private boolean flag;
        private int counter;

        private synchronized void markFlag() {
            if (flag) {
                throw new AssertionError("Flag should be marked once only");
            }
            flag = true;
        }

        private synchronized void doSomeWork() {
            for (int i = 0; i < 100; i++) {
                counter = -counter * counter + counter + 1;
            }
        }
    }

    private Supplier<TaskObject> supplier = () -> {
        var taskObject = new TaskObject();
        taskObject.doSomeWork();
        return taskObject;
    };

    private void fixedAmountOfTasks(int numberOfThreads, int numberOfTasks) throws InterruptedException, LightExecutionException {
        var threadPool = new ThreadPool(numberOfThreads);
        var results = new ArrayList<LightFuture<TaskObject>>();
        for (int i = 0; i < numberOfTasks; i++) {
            results.add(threadPool.submit(supplier));
        }
        for (LightFuture<TaskObject> lightFuture : results) {
            lightFuture.get().markFlag();
            assertTrue(lightFuture.isReady());
        }
    }

    private void tasksConsumersAndProducers(int numberOfThreads, int numberOfProducers, int numberOfConsumers) throws InterruptedException {
        var threadPool = new ThreadPool(numberOfThreads);
        var producers = new Thread[numberOfProducers];
        var consumers = new Thread[numberOfConsumers];
        var queue = new BlockingQueue<LightFuture<TaskObject>>();

        Arrays.setAll(producers, (i) -> new Thread(() -> {
            for (int j = 0; j < numberOfConsumers; j++) {
                queue.add(threadPool.submit(supplier));
            }
        }));

        Arrays.setAll(consumers, (i) -> new Thread(() -> {
            try {
                for (int j = 0; j < numberOfProducers; j++) {
                    TaskObject taskObject = queue.take().get();
                    taskObject.markFlag();
                }
            } catch (InterruptedException | LightExecutionException e) {
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

    @Test
    void manyThreadsFewTasks() throws Exception {
        fixedAmountOfTasks(1000, 100);
    }

    @Test
    void sameThreadsAndTasks() throws Exception {
        fixedAmountOfTasks(1000, 1000);
    }

    @Test
    void fewThreadsManyTasks() throws Exception {
        fixedAmountOfTasks(10, 1000);
    }

    @Test
    void manyProducers() throws Exception {
        tasksConsumersAndProducers(10, 500, 50);
    }

    @Test
    void manyConsumers() throws Exception {
        tasksConsumersAndProducers(10, 50, 500);
    }

    @Test
    void throwingLightExecutionException() {
        var threadPool = new ThreadPool();
        assertThrows(LightExecutionException.class, () -> {
           LightFuture lightFuture = threadPool.submit(() -> {
               throw new RuntimeException();
           });
           lightFuture.get();
        });
    }

    @Test
    void testShutdown() throws Exception {
        var threadPool = new ThreadPool(10);
        List<LightFuture<Integer>> tasksBeforeShutdown = new ArrayList<>();
        for (int i  = 0; i < 1000; i++) {
            final int ii = i;
            tasksBeforeShutdown.add(threadPool.submit(() -> ii));
        }
        threadPool.shutdown();
        assertThrows(IllegalStateException.class, () -> threadPool.submit(() -> 239));

        threadPool.awaitTermination();

        for (int i = 0; i < 1000; i++) {
            if (tasksBeforeShutdown.get(i).isReady()) {
                try {
                    assertEquals(Integer.valueOf(i), tasksBeforeShutdown.get(i).get());

                } catch (LightExecutionException exception) {
                    assertEquals(exception.getCause().getClass(), IllegalStateException.class);
                }
            }
        }
    }

    @Test
    void testThenApply() throws Exception {
        ThreadPool threadPool = new ThreadPool(10);
        Supplier<Integer> longComputable = () -> {
            int current = 1;
            for (int i = 1; i < 100000; i++) {
                current = 1 + current - current * current;
            }
            return current;
        };

        LightFuture<Integer> lightFuture = threadPool.submit(longComputable);
        List<LightFuture<Integer>> results = new ArrayList<>();
        for (int j = 0; j < 100; j++) {
            final int jj = j;
            results.add(lightFuture.thenApply((i) -> i * i + jj));
        }
        for (int j = 0; j < 100; j++) {
            assertEquals((int)lightFuture.get() * lightFuture.get() + j, (int)results.get(j).get());
        }
    }

    @Test
    void testNumberOfThreads() {
        int threadOnStart = Thread.activeCount();
        ThreadPool threadPool = new ThreadPool(179);
        assertEquals(threadOnStart + 179, Thread.activeCount());
    }

}