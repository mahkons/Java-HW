package ru.hse.kostya.java;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPool {

    private BlockingQueue<PoolTask<?>> tasksQueue = new BlockingQueue<>();
    private Thread[] threads;

    public ThreadPool() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public ThreadPool(int threadNumber) {
        threads = new Thread[threadNumber];
        Arrays.setAll(threads, (i) -> new Thread(new PoolWorker()));
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public <T> LightFuture<T> submit(Supplier<T> supplier) {
        PoolTask<T> task = new PoolTask<>(supplier);
        tasksQueue.add(task);
        return task;
    }

    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

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
        public synchronized T get() throws LightExecutionException {
            try {
                while (!isReady) {
                    wait();
                }
            } catch (InterruptedException exception) {
                this.exception = exception;
            }

            if (exception != null) {
                throw new LightExecutionException("Exception occured during execution", exception);
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
