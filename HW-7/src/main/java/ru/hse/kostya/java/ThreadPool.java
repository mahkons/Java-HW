package ru.hse.kostya.java;

import java.util.Arrays;
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

    private BlockingQueue<PoolTask<?>> tasksQueue = new BlockingQueue<>();
    private Thread[] threads;

    /** Creates ThreadPool with {@code Runtime.getRuntime().availableProcessors()} threads.*/
    public ThreadPool() {
        this(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Creates ThreadPool with given numbers threads.
     * Initializes and starts threads
     */
    public ThreadPool(int threadNumber) {
        threads = new Thread[threadNumber];
        Arrays.setAll(threads, (i) -> new Thread(new PoolWorker()));
        for (Thread thread : threads) {
            thread.start();
        }
    }

    /**
     * Adds new task to poolQueue.
     * Task is to execute suppliers get method
     */
    public <T> LightFuture<T> submit(Supplier<T> supplier) {
        PoolTask<T> task = new PoolTask<>(supplier);
        tasksQueue.add(task);
        return task;
    }

    /**
     * Interrupts all threads in pool.
     * Running now tasks will be finished, but no more will start execution
     */
    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    /** Worker of the pool.
     *  Run function consists of main loop of every Thread in pool
     *      in which tasks are removed from the queue and executed
     */
    private class PoolWorker implements Runnable {

        @Override
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    PoolTask<?> task = tasksQueue.take();
                    task.makeReady();
                }

            } catch (InterruptedException e) {
                //Thread execution interrupted
                //Thread stops working
            }
        }
    }

    /**
     *  LightFuture Interface implementation for ThreadPool.
     */
    private class PoolTask<T> implements LightFuture<T> {

        private final Supplier<T> supplier;
        private boolean isReady;
        private Exception exception;
        private T result;

        public PoolTask(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        private synchronized void makeReady() {
            try {
                result = supplier.get();
            } catch (Exception exception) {
                this.exception = exception;
            }
            isReady = true;
            notifyAll();
        }

        @Override
        public synchronized boolean isReady() {
            return isReady;
        }

        @Override
        public synchronized T get() throws LightExecutionException, InterruptedException {
            while (!isReady) {
                wait();
            }
            if (exception != null) {
                throw new LightExecutionException("Exception occurred during execution", exception);
            }
            return result;
        }

        @Override
        public <R> LightFuture<R> thenApply(Function<? super T, R> function) {
            PoolTask<R> task = new PoolTask<>(() -> {
                try {
                    return function.apply(PoolTask.this.get());
                } catch (Exception e) {
                    throw new RuntimeException("Cannot get result to apply function to", e);
                }
            });
            tasksQueue.add(task);
            return task;
        }
    }
}
