package ru.hse.kostya.java;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fixed size thread pool.
 * Tasks saved in shared queue
 * On construction starts threads, whose take tasks from queue head
 * Pushes new tasks in the tail of the queue
 * The threads exist and execute tasks until shutdown method is called
 */
public class ThreadPool {

    private final BlockingQueue<PoolTask<?>> tasksQueue = new BlockingQueue<>();
    private final Thread[] threads;
    private volatile boolean shutdown;
    private final CountDownLatch terminationLatch;

    /**
     * Creates ThreadPool with {@code Runtime.getRuntime().availableProcessors()} threads.
     */
    public ThreadPool() {
        this(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Creates ThreadPool with given numbers threads.
     * Initializes and starts threads
     * @throws IllegalArgumentException if number of threads is zero or negative
     */
    public ThreadPool(int threadNumber) {
        if (threadNumber <= 0) {
            throw new IllegalArgumentException("Number of threads should be positive");
        }
        terminationLatch = new CountDownLatch(threadNumber);

        threads = new Thread[threadNumber];
        Arrays.setAll(threads, (i) -> new Thread(new PoolWorker()));
        for (Thread thread : threads) {
            thread.start();
        }
    }

    /**
     * Adds new task to poolQueue.
     * Task is to execute suppliers get method
     * @throws IllegalStateException if shutdown function had been called already
     */
    public <T> LightFuture<T> submit(@NotNull Supplier<T> supplier) {
        if (shutdown) {
            throw new IllegalStateException("Shutdown happened. No more tasks accepted");
        }

        return submit(new PoolTask<>(supplier));
    }

    /**
     * Adds new task to poolQueue.
     * Rejects task with IllegalState exception if shutdown happened
     */
    private <T> LightFuture<T> submit(PoolTask<T> task) {
        synchronized (tasksQueue) {
            if (shutdown) {
                task.reject(new IllegalStateException("Shutdown happened. No more tasks accepted"));
            }

            tasksQueue.add(task);
            return task;
        }
    }

    /**
     * Interrupts all threads in pool.
     * Running now tasks will be finished, but no more will start execution
     * Not waiting for current tasks to finish
     * There may remain tasks in queue, they'll be rejected with IllegalState exception
     */
    public void shutdown() {
        shutdown = true;
        for (Thread thread : threads) {
            thread.interrupt();
        }
        synchronized (tasksQueue) {
            try {
                while (!tasksQueue.isEmpty()) {
                    PoolTask<?> task = tasksQueue.take();
                    task.reject(new IllegalStateException("Shutdown happened. No more tasks accepted"));
                }
            } catch (InterruptedException exception) {
                //We do not want to leave ThreadPool in inconsistent state
                throw new RuntimeException("Thread was interrupted during ThreadPool shutdown", exception);
            }
        }
    }

    /**
     * Awaits for all threads in thread pool to finish their execution.
     * @throws InterruptedException if current thread was interrupted while waiting
     */
    public void awaitTermination() throws InterruptedException {
        terminationLatch.await();
    }

    /** Worker of the pool.
     *  Run function consists of main loop of every Thread in pool
     *      in which tasks are removed from the queue and executed
     */
    private class PoolWorker implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.interrupted() && !shutdown) {
                    PoolTask<?> task = tasksQueue.take();
                    task.makeReady();
                }

            } catch (InterruptedException e) {
                //Thread execution interrupted
                //Thread stops working
            } finally {
                terminationLatch.countDown();
            }
        }
    }

    /**
     *  LightFuture Interface implementation for ThreadPool.
     */
    private class PoolTask<T> implements LightFuture<T> {

        private volatile Supplier<T> supplier;
        private volatile boolean isReady;
        private Exception exception;
        private T result;

        private final List<PoolTask<?>> applyAfter = new ArrayList<>();

        public PoolTask(@NotNull Supplier<T> supplier) {
            this.supplier = supplier;
        }

        private void makeReady() {
            if (!isReady) {
                try {
                    result = supplier.get();
                } catch (Exception exception) {
                    this.exception = exception;
                }
            }
            supplier = null;

            isReady = true;
            synchronized (applyAfter) {
                applyAfter.forEach(ThreadPool.this::submit);
            }
            synchronized (this) {
                notifyAll();
            }
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public T get() throws LightExecutionException, InterruptedException {
            if (isReady) {
                return getResult();
            }

            synchronized (this) {
                while (!isReady) {
                    wait();
                }
                return getResult();
            }
        }

        private T getResult() throws LightExecutionException {
            if (exception != null) {
                throw new LightExecutionException("Exception occurred during execution", exception);
            }
            return result;
        }

        private void reject(Exception exception) {
            this.exception = exception;
            isReady = true;
            makeReady();
        }

        @Override
        public <R> LightFuture<R> thenApply(@NotNull Function<? super T, R> function) {
            var task = new PoolTask<>(() -> {
                try {
                    return function.apply(getResult());
                } catch (LightExecutionException e) {
                    throw new RuntimeException("Cannot get result to apply function to", e.getCause());
                }
            });
            if (isReady) {
                submit(task);
            } else {
                synchronized (applyAfter) {
                    if (isReady) {
                        submit(task);
                    } else {
                        applyAfter.add(task);
                    }
                }
            }
            return task;
        }
    }
}
